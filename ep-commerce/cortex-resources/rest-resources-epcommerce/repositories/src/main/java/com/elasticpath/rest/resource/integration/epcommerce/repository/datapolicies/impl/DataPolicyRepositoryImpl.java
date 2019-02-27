/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.DataPolicyRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.misc.TimeService;

/**
 * The facade for {@link DataPolicyRepository} related operations.
 */
@Singleton
@Named("dataPolicyRepository")
public class DataPolicyRepositoryImpl implements DataPolicyRepository {

	/**
	 * Error for active policy not found.
	 */
	@VisibleForTesting
	static final String ACTIVE_POLICY_NOT_FOUND = "Active policy is not found.";

	/**
	 * Error for option value being not found.
	 */
	@VisibleForTesting
	static final String VALUE_NOT_FOUND = "Option value not found.";

	/**
	 * Error for data policy segments not found.
	 */
	@VisibleForTesting
	static final String HEADER_NOT_FOUND = "Data policy segments header not found.";

	/**
	 * Error for boolean value expected.
	 */
	@VisibleForTesting
	static final String BOOLEAN_EXPECTED = "Boolean value expected for consent field";

	private static final String DATA_POLICY_SEGMENTS = "DATA_POLICY_SEGMENTS";
	private static final int SECONDS_ALLOWED = 60;

	private final DataPolicyService coreDataPolicyService;
	private final CustomerConsentService coreCustomerConsentService;
	private final TimeService timeService;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Constructor.
	 *
	 * @param coreDataPolicyService      the core data policy service
	 * @param coreCustomerConsentService the customer consent service repository
	 * @param timeService                time service
	 * @param reactiveAdapter            reactiveAdapter
	 */
	@Inject
	public DataPolicyRepositoryImpl(
			@Named("dataPolicyService") final DataPolicyService coreDataPolicyService,
			@Named("customerConsentService") final CustomerConsentService coreCustomerConsentService,
			@Named("timeService") final TimeService timeService,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.coreDataPolicyService = coreDataPolicyService;
		this.coreCustomerConsentService = coreCustomerConsentService;
		this.timeService = timeService;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	public Maybe<CustomerConsent> findCustomerConsentByDataPolicyGuidForCustomer(final String dataPolicyGuid, final String customerGuid) {
		return reactiveAdapter.fromServiceAsMaybe(() -> getByDataPolicyGuidForCustomer(dataPolicyGuid, customerGuid), Maybe.empty());
	}

	@Override
	public Single<DataPolicy> findValidDataPolicy(final String dataPolicyGuid, final String scope, final Collection<SubjectAttribute> headers) {
		if (!coreDataPolicyService.areEnabledByStore(scope)) {
			return Single.error(ResourceOperationFailure.notFound(VALUE_NOT_FOUND));
		}

		DataPolicy dataPolicy = coreDataPolicyService.findActiveByGuid(dataPolicyGuid);

		if (dataPolicy == null) {
			return Single.error(ResourceOperationFailure.notFound(ACTIVE_POLICY_NOT_FOUND));
		}

		List<String> segmentHeaders = getSegmentHeadersLowerCase(headers);

		Set<String> dataPolicySegments = dataPolicy.getSegments().stream()
				.map(string -> string.toLowerCase(Locale.getDefault()))
				.collect(Collectors.toSet());

		if (!dataPolicySegments.removeAll(segmentHeaders)) {
			return Single.error(ResourceOperationFailure.notFound(HEADER_NOT_FOUND));
		}
		return Single.just(dataPolicy);
	}

	@Override
	public List<String> getSegmentHeadersLowerCase(final Collection<SubjectAttribute> headers) {
		return headers.stream()
				.filter(attribute -> DATA_POLICY_SEGMENTS.equals(attribute.getType()))
				.flatMap(attribute -> Splitter.on(",").trimResults(CharMatcher.anyOf("[] "))
						.splitToList(attribute.getValue()).stream()
						.map(value -> value.toLowerCase(Locale.getDefault())))
				.collect(Collectors.toList());
	}

	@Override
	public Observable<DataPolicy> findActiveDataPoliciesForSegmentsAndStore(final List<String> headers, final String scope) {
		return reactiveAdapter.fromService(() -> coreDataPolicyService.findActiveDataPoliciesForSegmentsAndStore(headers, scope))
				.flatMap(Observable::fromIterable);
	}

	@Override
	public Single<CustomerConsent> saveCustomerConsent(final CustomerConsent consent) {
		return Single.just(coreCustomerConsentService.save(consent));
	}

	@Override
	public Single<CustomerConsent> createCustomerConsentForDataPolicy(final String customerGuid, final DataPolicy dataPolicy,
																	  final DataPolicyEntity dataPolicyEntity) {
		CustomerConsent customerConsent = findCustomerConsentByDataPolicyGuidForCustomer(dataPolicy.getGuid(), customerGuid).blockingGet();

		if (customerConsent == null || !isSubmittingRepeatConsentAction(customerConsent, dataPolicyEntity.getDataPolicyConsent())) {
			customerConsent = createCustomerConsent(customerGuid, dataPolicy);
			return setAndSaveConsentOnCustomerConsent(dataPolicyEntity, customerConsent);
		}
		return Single.just(customerConsent);
	}

	@Override
	public Single<CustomerConsent> setAndSaveConsentOnCustomerConsent(final DataPolicyEntity entity, final CustomerConsent customerConsent) {
		if (entity.getDataPolicyConsent().equalsIgnoreCase(Boolean.TRUE.toString())) {
			customerConsent.setAction(ConsentAction.GRANTED);
		} else if (entity.getDataPolicyConsent().equalsIgnoreCase(Boolean.FALSE.toString())) {
			customerConsent.setAction(ConsentAction.REVOKED);
		}
		customerConsent.setConsentDate(timeService.getCurrentTime());

		return saveCustomerConsent(customerConsent);
	}

	@Override
	public Boolean customerHasGivenConsentForAtLeastOneDataPolicy(final String customerGuid, final Set<DataPolicy> filteredPolicies) {
		return coreCustomerConsentService.customerHasGivenConsentForAtLeastOneDataPolicy(customerGuid, filteredPolicies);
	}

	private CustomerConsent getByDataPolicyGuidForCustomer(final String dataPolicyGuid, final String customerGuid) {
		return coreCustomerConsentService.findByDataPolicyGuidForCustomerLatest(dataPolicyGuid, customerGuid);
	}

	private boolean isSubmittingRepeatConsentAction(final CustomerConsent consent, final String dataPolicyConsent) {
		ConsentAction consentAction = ConsentAction.valueOf(Boolean.valueOf(dataPolicyConsent));
		return consentAction.equals(consent.getAction())
				&& Math.abs(Duration.between(timeService.getCurrentTime().toInstant(),
				consent.getConsentDate().toInstant()).getSeconds()) <= SECONDS_ALLOWED;
	}

	private CustomerConsent createCustomerConsent(final String customerGuid, final DataPolicy dataPolicy) {
		CustomerConsent customerConsent = new CustomerConsentImpl();
		customerConsent.initialize();
		customerConsent.setDataPolicy(dataPolicy);
		customerConsent.setCustomerGuid(customerGuid);
		return customerConsent;
	}
}
