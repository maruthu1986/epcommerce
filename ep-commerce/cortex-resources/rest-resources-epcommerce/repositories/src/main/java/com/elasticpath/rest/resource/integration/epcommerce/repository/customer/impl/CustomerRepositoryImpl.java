/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * A repository for {@link Customer}s.
 */
@Singleton
@Named("customerRepository")
public class CustomerRepositoryImpl implements CustomerRepository {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerRepositoryImpl.class);
	private static final String CUSTOMER_WAS_NOT_FOUND = "Customer was not found.";
	private static final boolean IS_AUTHENTICATE = false;

	private final CustomerService customerService;
	private final CustomerSessionRepository customerSessionRepository;
	private final CustomerSessionService customerSessionService;
	private final ShoppingCartService shoppingCartService;
	private final BeanFactory coreBeanFactory;
	private final Provider<CartOrderRepository> cartOrderRepositoryProvider;
	private final ExceptionTransformer exceptionTransformer;
	private final ReactiveAdapter reactiveAdapter;


	/**
	 * Constructor.
	 *
	 * @param customerService             The customerService instance.
	 * @param customerSessionRepository   the customer session repository
	 * @param customerSessionService      the customer session service
	 * @param shoppingCartService         shopping cart service
	 * @param coreBeanFactory             beanFactory.
	 * @param cartOrderRepositoryProvider cart order repository
	 * @param exceptionTransformer        the exception transformer
	 * @param reactiveAdapter             the reactive adapter
	 */
	@Inject
	@SuppressWarnings({"checkstyle:parameternumber", "PMD.ExcessiveParameterList"})
	CustomerRepositoryImpl(
			@Named("customerService")
			final CustomerService customerService,
			@Named("customerSessionRepository")
			final CustomerSessionRepository customerSessionRepository,
			@Named("customerSessionService")
			final CustomerSessionService customerSessionService,
			@Named("shoppingCartService")
			final ShoppingCartService shoppingCartService,
			@Named("coreBeanFactory")
			final BeanFactory coreBeanFactory,
			@Named("cartOrderRepository")
			final Provider<CartOrderRepository> cartOrderRepositoryProvider,
			@Named("exceptionTransformer")
			final ExceptionTransformer exceptionTransformer,
			@Named("reactiveAdapter")
			final ReactiveAdapter reactiveAdapter) {

		this.customerService = customerService;
		this.customerSessionRepository = customerSessionRepository;
		this.customerSessionService = customerSessionService;
		this.shoppingCartService = shoppingCartService;
		this.coreBeanFactory = coreBeanFactory;
		this.cartOrderRepositoryProvider = cartOrderRepositoryProvider;
		this.exceptionTransformer = exceptionTransformer;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	public Customer createNewCustomerEntity() {
		return coreBeanFactory.getBean(ContextIdNames.CUSTOMER);
	}

	@Override
	@CacheResult
	public ExecutionResult<Customer> findCustomerByUserId(final String storeCode, final String userId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CustomerSession customerSessionResult = Assign.ifSuccessful(findCustomerSessionByUserIdWithoutException(storeCode, userId));
				Customer customer = customerSessionResult.getShopper().getCustomer();
				Ensure.notNull(customer, OnFailure.returnNotFound(CUSTOMER_WAS_NOT_FOUND));

				return ExecutionResultFactory.createReadOK(customer);
			}
		}.execute();
	}

	private ExecutionResult<CustomerSession> findCustomerSessionByUserIdWithoutException(final String storeCode, final String userId) {
		try {
			return customerSessionRepository.findCustomerSessionByUserId(storeCode, userId);
		} catch (Exception e) {
			LOG.error(String.format("Error when finding customer session by store code %s and user ID %s", storeCode, userId), e);
			return ExecutionResultFactory.createServerError("Server error when finding customer session by user ID");
		}
	}

	@Override
	@CacheResult
	public ExecutionResult<Customer> findCustomerByGuid(final String guid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CustomerSession customerSessionResult = Assign.ifSuccessful(findByCustomerSessionByGuidWithoutException(guid));
				Customer customer = customerSessionResult.getShopper().getCustomer();
				Ensure.notNull(customer, OnFailure.returnNotFound(CUSTOMER_WAS_NOT_FOUND));

				return ExecutionResultFactory.createReadOK(customer);
			}
		}.execute();
	}

	private ExecutionResult<CustomerSession> findByCustomerSessionByGuidWithoutException(final String guid) {
		try {
			return customerSessionRepository.findCustomerSessionByGuid(guid);
		} catch (Exception e) {
			LOG.error(String.format("Error when finding customer session by guid %s", guid), e);
			return ExecutionResultFactory.createServerError("Server error when finding customer session by guid");
		}
	}

	@Override
	@CacheResult
	public Single<Customer> getCustomer(final String guid) {
		return reactiveAdapter.fromServiceAsSingle(() -> customerService.findByGuid(guid), CUSTOMER_WAS_NOT_FOUND);
	}

	@Override
	@CacheRemove(typesToInvalidate = Customer.class)
	public ExecutionResult<Void> updateCustomer(final Customer customer) {
		ExecutionResult<Void> result;
		// Note: We need to catch EpValidationException before EpSystemException.
		try {
			customerService.update(customer);
			customerSessionRepository.invalidateCustomerSessionByGuid(customer.getGuid());
			result = ExecutionResultFactory.createUpdateOK();
		} catch (UserIdExistException error) {
			result = exceptionTransformer.getExecutionResult(error);
		} catch (EpValidationException error) {
			result = exceptionTransformer.getExecutionResult(error);
		} catch (EpSystemException error) {
			result = ExecutionResultFactory.createServerError(error.getMessage());
		}
		return result;
	}

	@Override
	public Completable updateCustomerAsCompletable(final Customer customer) {
		return reactiveAdapter.fromServiceAsCompletable(() -> customerService.update(customer));
	}

	@Override
	@CacheRemove(typesToInvalidate = Customer.class)
	public Single<Customer> addAddress(final Customer customer, final CustomerAddress address) {
		return reactiveAdapter.fromServiceAsSingle(() -> customerService.addOrUpdateAddress(customer, address));
	}

	@Override
	@CacheRemove(typesToInvalidate = Customer.class)
	public Completable updateAddress(final Customer customer, final CustomerAddress address) {
		return reactiveAdapter.fromServiceAsSingle(() -> customerService.addOrUpdateAddress(customer, address))
				.flatMapCompletable(updatedCustomer -> updateShippingAddressOnCustomerCart(updatedCustomer, address));
	}

	/**
	 * Update the shipping address on the customer's shopping cart if the shipping address exists.
	 *
	 * @param updatedCustomer updatedCustomer
	 * @param address         address
	 * @return Completable
	 */
	protected Completable updateShippingAddressOnCustomerCart(final Customer updatedCustomer, final CustomerAddress address) {
		if (updatedCustomer.getPreferredShippingAddress().getGuid().equals(address.getGuid())) {
			CartOrderRepository cartOrderRepository = cartOrderRepositoryProvider.get();
			return cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(updatedCustomer.getStoreCode(), updatedCustomer.getGuid())
					.firstElement()
					.flatMapCompletable(cartGuid -> cartOrderRepository
							.updateShippingAddressOnCartOrderAsSingle(address.getGuid(), cartGuid, updatedCustomer.getStoreCode()).toCompletable());
		}

		return Completable.complete();
	}

	@Override
	@CacheRemove(typesToInvalidate = Customer.class)
	public Single<Customer> update(final Customer updatedCustomer) {
		return reactiveAdapter.fromServiceAsSingle(() -> customerService.update(updatedCustomer));
	}

	@Override
	public ExecutionResult<Object> mergeCustomer(final CustomerSession customerSession,
			final Customer recipientCustomer,
			final String validatedStoreCode) {
		try {
			ensureShopperHasAssociatedShoppingCart(customerSession);

			customerSessionService.changeFromAnonymousToRegisteredCustomer(customerSession, recipientCustomer, validatedStoreCode);

		} catch (Exception exception) {
			LOG.error("Error merging customer session", exception);
			return ExecutionResultFactory.createServerError("Server error when merging customer session");
		}
		return ExecutionResultFactory.createUpdateOK();
	}

	@Override
	@CacheRemove(typesToInvalidate = Customer.class)
	public ExecutionResult<Customer> addUnauthenticatedUser(final Customer customer) {
		try {
			Customer authenticatedCustomer = customerService.addByAuthenticate(customer, IS_AUTHENTICATE);

			return ExecutionResultFactory.createReadOK(authenticatedCustomer);
		} catch (Exception exception) {
			LOG.error("Error adding unauthenticated user: {}", customer, exception);
			return ExecutionResultFactory.createServerError("Server error when adding unauthenticated user");
		}
	}

	@Override
	public boolean isFirstTimeBuyer(final Customer customer) {
		return customer.isFirstTimeBuyer();
	}


	/*
	* This is necessary for the CE session management update handlers to work correctly.
 	*/
	private void ensureShopperHasAssociatedShoppingCart(final CustomerSession customerSession) {
		ShoppingCart donorShoppingCart = shoppingCartService.findOrCreateByShopper(customerSession.getShopper());
		donorShoppingCart.getShopper().setCurrentShoppingCart(donorShoppingCart);
	}
}
