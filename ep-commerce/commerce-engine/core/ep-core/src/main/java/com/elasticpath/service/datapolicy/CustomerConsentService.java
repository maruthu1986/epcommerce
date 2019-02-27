/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy;

import java.util.List;
import java.util.Set;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * Perform CRUD operations with CustomerConsent entity.
 */
public interface CustomerConsentService {

	/**
	 * Saves the given customer consent.
	 *
	 * @param customerConsent the customer consent to add.
	 * @return the persisted instance of customer consent.
	 * @throws DuplicateKeyException - if trying to add an existing customer consent.
	 */
	CustomerConsent save(CustomerConsent customerConsent) throws DuplicateKeyException;

	/**
	 * Updates the given customer consent. NOTE: To be used when updating the customer guid when merging customers.
	 *
	 * @param customerConsentUids the list of customer consent uids to update.
	 * @param customerGuid        the customer guid to update to.
	 * @throws DuplicateKeyException - if trying to add an existing customer consent.
	 */
	void updateCustomerGuids(List<Long> customerConsentUids, String customerGuid) throws DuplicateKeyException;

	/**
	 * List all customer consents stored in the database.
	 *
	 * @return a list of customer consents.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<CustomerConsent> list() throws EpServiceException;

	/**
	 * Load the customer consent with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param customerConsentUid the customer consent UID
	 * @return the customer consent if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerConsent load(long customerConsentUid) throws EpServiceException;

	/**
	 * Load the customerConsent with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param customerConsentUid the customer consent UID.
	 * @return the customer consent if UID exists, otherwise null.
	 * @throws EpServiceException - in case of any errors.
	 */
	CustomerConsent get(long customerConsentUid) throws EpServiceException;

	/**
	 * Retrieve the customer consent with the given guid.
	 *
	 * @param guid the guid of the customer consent.
	 * @return the customer consent with the given guid.
	 * @throws EpServiceException - in case of any errors.
	 */
	CustomerConsent findByGuid(String guid) throws EpServiceException;

	/**
	 * Retrieve the list of customer consents with the list of given guids.
	 *
	 * @param guids the list of guids of the customer consents.
	 * @return the list of customer consent with the given guids.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<CustomerConsent> findByGuids(List<String> guids) throws EpServiceException;

	/**
	 * Retrieve the list of customer consents for a given customer guid.
	 *
	 * @param customerGuid the customer guid of the customer consent.
	 * @return the customer consents with the given guid.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<CustomerConsent> findByCustomerGuid(String customerGuid) throws EpServiceException;

	/**
	 * Retrieve a list of customer consents with data policies in {@link com.elasticpath.domain.datapolicy.DataPolicyState#ACTIVE} state
	 * only for the selected customer.
	 * If requested, disabled policies will be included too.
	 *
	 * @param customerGuid            the consent customer guid.
	 * @param includeDisabledPolicies if true, disabled policies will be included
	 * @return the list of customer consents with active and (optionally) disabled policies.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<CustomerConsent> findWithActiveDataPoliciesByCustomerGuid(String customerGuid, boolean includeDisabledPolicies) throws EpServiceException;

	/**
	 * Retrieve the most recent customer consent for a given data policy guid and customer guid.
	 *
	 * @param customerGuid   the customer guid of the customer consent.
	 * @param dataPolicyGuid the data policy guid of the customer consent.
	 * @return the customer consent with the given guid.
	 * @throws EpServiceException - in case of any errors.
	 */
	CustomerConsent findByDataPolicyGuidForCustomerLatest(String dataPolicyGuid, String customerGuid);

	/**
	 * Check whether the customer has consented to at least one the given data policies.
	 *
	 * @param customerGuid the customer guid
	 * @param policies     the data policies that contain the data points
	 * @return true if the customer has consented to at least one data policy
	 */
	Boolean customerHasGivenConsentForAtLeastOneDataPolicy(String customerGuid, Set<DataPolicy> policies);
}
