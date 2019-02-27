/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;

/**
 * The facade for operations with data policies.
 */
public interface DataPolicyRepository {

	/**
	 * Get the customer consent by data policy and customer guid.
	 *
	 * @param dataPolicyGuid the data policy guid
	 * @param customerGuid   the customer guid
	 * @return Maybe with the customer consent
	 */
	Maybe<CustomerConsent> findCustomerConsentByDataPolicyGuidForCustomer(String dataPolicyGuid, String customerGuid);

	/**
	 * Get the data policy based on order GUID.
	 *
	 * @param dataPolicyGuid the data policy guid
	 * @param scope          the scope
	 * @param headers        the collection of headers
	 * @return Maybe with the data policy
	 */
	Single<DataPolicy> findValidDataPolicy(String dataPolicyGuid, String scope, Collection<SubjectAttribute> headers);

	/**
	 * Get the data policy segment headers.
	 *
	 * @param headers the collection of headers
	 * @return List of segment headers
	 */
	List<String> getSegmentHeadersLowerCase(Collection<SubjectAttribute> headers);

	/**
	 * Get active data policies for the given segment headers and scope.
	 *
	 * @param headers the collection of headers
	 * @param scope   the scope
	 * @return List of active data policies
	 */
	Observable<DataPolicy> findActiveDataPoliciesForSegmentsAndStore(List<String> headers, String scope);

	/**
	 * Persist the incoming customer consent.
	 *
	 * @param consent the customer consent to persist
	 * @return persisted customer consent
	 */
	Single<CustomerConsent> saveCustomerConsent(CustomerConsent consent);

	/**
	 * Creates a customer consent for the input user guid and data policy.
	 * If a customer consent exists for these inputs, it is returned.
	 * If one does not exist, a new customer consent is created for
	 * the given user guid and data policy.
	 *
	 * @param userGuid         the user guid
	 * @param dataPolicy       the data policy
	 * @param dataPolicyEntity the entity's data policy consent value
	 * @return the customer consent
	 */
	Single<CustomerConsent> createCustomerConsentForDataPolicy(String userGuid, DataPolicy dataPolicy, DataPolicyEntity dataPolicyEntity);

	/**
	 * Set customer consent fields and persist the object.
	 *
	 * @param entity          the data policy entity
	 * @param customerConsent the customer consent object
	 * @return the customer consent
	 */
	Single<CustomerConsent> setAndSaveConsentOnCustomerConsent(DataPolicyEntity entity, CustomerConsent customerConsent);

	/**
	 * Check whether there exists an accepted data policy containing the given data point location value.
	 *
	 * @param customerGuid     the customer guid
	 * @param filteredPolicies the data policies that contain the data points
	 * @return true if there exists an data policy with the data point location values
	 */
	Boolean customerHasGivenConsentForAtLeastOneDataPolicy(String customerGuid, Set<DataPolicy> filteredPolicies);
}