/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.pricing.SessionPriceListLifecycle;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Encapsulates operations on customer session.
 */
@Singleton
@Named("customerSessionRepository")
public class CustomerSessionRepositoryImpl implements CustomerSessionRepository {
	private static final Logger LOG = LoggerFactory.getLogger(CustomerSessionRepositoryImpl.class);

	private static final String CUSTOMER_WAS_NOT_FOUND = "Customer was not found.";

	private final ResourceOperationContext resourceOperationContext;
	private final ShopperService shopperService;
	private final CustomerService customerService;
	private final CustomerSessionService customerSessionService;
	private final SessionPriceListLifecycle sessionPriceListLifecycle;
	private final StoreRepository storeRepository;
	private final CustomerSessionTagSetFactory tagSetFactory;
	private final ReactiveAdapter reactiveAdapter;


	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext  the resource operation context
	 * @param shopperService            the shopper service
	 * @param customerService           the customer service
	 * @param customerSessionService    the customer session service
	 * @param sessionPriceListLifecycle the price list lifecycle manager.
	 * @param storeRepository           the store repository
	 * @param tagSetFactory             the customer session tag set factory
	 * @param reactiveAdapter 			the reactive adapter
	 */
	@Inject
	@SuppressWarnings({"checkstyle:parameternumber", "PMD.ExcessiveParameterList"})
	public CustomerSessionRepositoryImpl(
			@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext,
			@Named("shopperService") final ShopperService shopperService,
			@Named("customerService") final CustomerService customerService,
			@Named("customerSessionService") final CustomerSessionService customerSessionService,
			@Named("sessionPriceListLifecycle") final SessionPriceListLifecycle sessionPriceListLifecycle,
			@Named("storeRepository") final StoreRepository storeRepository,
			@Named("customerSessionTagSetFactory") final CustomerSessionTagSetFactory tagSetFactory,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.resourceOperationContext = resourceOperationContext;
		this.shopperService = shopperService;
		this.customerService = customerService;
		this.customerSessionService = customerSessionService;
		this.sessionPriceListLifecycle = sessionPriceListLifecycle;
		this.storeRepository = storeRepository;
		this.tagSetFactory = tagSetFactory;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	public ExecutionResult<CustomerSession> findOrCreateCustomerSession() {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				String userGuid = resourceOperationContext.getUserIdentifier();
				return findCustomerSessionByGuid(userGuid);
			}
		}.execute();
	}

	@Override
	public Single<CustomerSession> findOrCreateCustomerSessionAsSingle() {
		String userGuid = resourceOperationContext.getUserIdentifier();
		return reactiveAdapter.fromRepositoryAsSingle(() -> findCustomerSessionByGuid(userGuid));
	}

	@Override
	@CacheResult
	public ExecutionResult<CustomerSession> findCustomerSessionByGuid(final String customerGuid) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				final Shopper shopper = getShopperByCustomerGuid(customerGuid);

				Ensure.notNull(shopper, OnFailure.returnNotFound(CUSTOMER_WAS_NOT_FOUND));

				return createCustomerSession(getStoreCodeFromSubjectIfPossible(shopper), shopper);
			}
		}.execute();
	}

	@Override
	public Single<CustomerSession> findCustomerSessionByGuidAsSingle(final String customerGuid) {
		return reactiveAdapter.fromRepositoryAsSingle(() -> findCustomerSessionByGuid(customerGuid));
	}

	private Shopper getShopperByCustomerGuid(final String customerGuid) {
		final Subject subject = resourceOperationContext.getSubject();
		Shopper shopper;

		if (subject == null) {
			shopper = shopperService.findByCustomerGuid(customerGuid);
		} else {
			final String storeCode = SubjectUtil.getScope(subject);
			shopper = shopperService.findByCustomerGuidAndStoreCode(customerGuid, storeCode);
		}

		if (shopper == null) {
			shopper = Assign.ifSuccessful(findByCustomerGuidWithoutException(customerGuid));
		}

		return shopper;
	}

	@Override
	@CacheRemove(typesToInvalidate = CustomerSession.class)
	public void invalidateCustomerSessionByGuid(final String customerGuid) {
		//this method causes invalidation of CustomerSession instance for given customer guid
	}

	private String getStoreCodeFromSubjectIfPossible(final Shopper shopper) {
		final Subject subject = resourceOperationContext.getSubject();
		return subject == null ? shopper.getStoreCode() : SubjectUtil.getScope(subject);
	}

	private ExecutionResult<Shopper> findByCustomerGuidWithoutException(final String customerGuid) {
		try {
			final Customer customer = customerService.findByGuid(customerGuid);
			if (customer == null) {
				return ExecutionResultFactory.createNotFound(
						String.format("Customer not found for given customer guid %s", customerGuid));
			}
			final Shopper shopper = shopperService.findOrCreateShopper(customer, customer.getStoreCode());

			return ExecutionResultFactory.createReadOK(shopper);

		} catch (Exception e) {
			LOG.error(String.format("Error when finding shopper by guid %s", customerGuid), e);
			return ExecutionResultFactory.createServerError("Server error when finding shopper by guid");
		}
	}

	@Override
	@CacheResult
	public ExecutionResult<CustomerSession> findCustomerSessionByUserId(final String storeCode, final String customerUserId) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				Shopper shopper = shopperService.findByCustomerUserIdAndStoreCode(customerUserId, storeCode);
				if (shopper == null) {
					shopper = Assign.ifSuccessful(findOrCreateShopperWithoutException(customerUserId, storeCode));
				}

				Ensure.notNull(shopper, OnFailure.returnNotFound(CUSTOMER_WAS_NOT_FOUND));

				return createCustomerSession(storeCode, shopper);
			}
		}.execute();
	}

	private ExecutionResult<Shopper> findOrCreateShopperWithoutException(final String customerUserId, final String storeCode) {
		try {

			final Customer customer = customerService.findByUserId(customerUserId, storeCode);

			if (customer == null) {
				return ExecutionResultFactory.createNotFound(
						String.format("Customer not found for given customer user id %s and store %s", customerUserId, storeCode));
			}
			final Shopper shopper = shopperService.findOrCreateShopper(customer, customer.getStoreCode());

			return ExecutionResultFactory.createReadOK(shopper);
		} catch (Exception e) {
			LOG.error(String.format("Error when finding/creating shopper for store %s and customer user ID %s", storeCode, customerUserId), e);
			return ExecutionResultFactory.createServerError("Server error when finding/creating shopper by customer ID");
		}
	}

	@CacheResult
	private ExecutionResult<CustomerSession> createCustomerSession(final String storeCode, final Shopper shopper) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ExecutionResult<CustomerSession> result;
				Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));

				try {
					Locale locale = findLocaleForCurrentOperation(store);
					Currency currency = findCurrencyForCurrentOperation(store);
					CustomerSession customerSession = createCustomerSession(shopper, locale);
					TagSet tagSet = tagSetFactory.createTagSet(shopper.getCustomer());
					customerSession = configurePersonalisedPricing(customerSession, store, currency, tagSet);
					result = ExecutionResultFactory.createReadOK(customerSession);
				} catch (Exception e) {
					LOG.error("Error when initializing customer session", e);
					result = ExecutionResultFactory.createServerError("Server error when creating customer session");
				}

				return result;
			}
		}.execute();
	}

	/**
	 * Configure personalised pricing for the session.
	 *
	 * @param customerSession The session.
	 * @param store           The store.
	 * @param currency        The currency
	 * @param tagSet          The tags.
	 * @return CustomerSession - The session.
	 */
	protected CustomerSession configurePersonalisedPricing(final CustomerSession customerSession,
														   final Store store,
														   final Currency currency,
														   final TagSet tagSet) {
		CustomerSession customerSessionForPricing = customerSessionService.initializeCustomerSessionForPricing(
				customerSession, store.getCode(), currency);

		for (Map.Entry<String, Tag> tagEntry : tagSet.getTags().entrySet()) {
			customerSessionForPricing.getCustomerTagSet().addTag(tagEntry.getKey(), tagEntry.getValue());
		}

		sessionPriceListLifecycle.refreshPriceListStack(customerSessionForPricing, store);
		return customerSessionForPricing;
	}

	/**
	 * Creates a {@link CustomerSession} given a {@link Shopper}.
	 *
	 * @param shopper the shopper
	 * @param locale  the locale to use for the customer session
	 * @return the created customer session
	 */
	CustomerSession createCustomerSession(final Shopper shopper, final Locale locale) {
		CustomerSession customerSession = customerSessionService.createWithShopper(shopper);
		Date now = new Date();
		customerSession.setCreationDate(now);
		customerSession.setLastAccessedDate(now);
		customerSession.setLocale(locale);
		return customerSession;
	}

	/**
	 * Find the locale for this session given the current Subject's preferences or the Store's default locale.
	 *
	 * @param store the store
	 * @return the locale locale to use
	 */
	@VisibleForTesting
	Locale findLocaleForCurrentOperation(final Store store) {
		Optional<Locale> localeOptional = Optional.empty();
		Subject subject = resourceOperationContext.getSubject();

		if (subject != null) {
			localeOptional = Optional.ofNullable(SubjectUtil.getLocale(subject));
		}

		return localeOptional
				.orElse(store.getDefaultLocale());
	}

	/**
	 * Find the best currency for this session given the current Subject's preferences and Store's default currency.
	 *
	 * @param store the store
	 * @return the right currency
	 */
	@VisibleForTesting
	Currency findCurrencyForCurrentOperation(final Store store) {
		Optional<Currency> currencyOptional = Optional.empty();
		Subject subject = resourceOperationContext.getSubject();

		if (subject != null) {
			currencyOptional = Optional.ofNullable(SubjectUtil.getCurrency(subject));
		}

		return currencyOptional
				.orElse(store.getDefaultCurrency());
	}
}
