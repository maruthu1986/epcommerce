/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy;

import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;

/**
 * Perform CRUD operations with DataPolicy entity.
 */
public interface DataPolicyService {

	/**
	 * Configuration setting for enabling data policies for a store.
	 */
	String COMMERCE_STORE_ENABLE_DATA_POLICIES = "COMMERCE/STORE/enableDataPolicies"; //$NON-NLS-1$

	/**
	 * Returns true if data policies are enabled for a store via configuration setting COMMERCE/STORE/enableDataPolicies.
	 *
	 * @param store the store.
	 * @return whether data policies are enabled for the store.
	 */
	Boolean areEnabledByStore(String store);

	/**
	 * Saves the given data policy.
	 *
	 * @param dataPolicy the data policy to add.
	 * @return the persisted instance of data policy.
	 * @throws DuplicateKeyException - if trying to add an existing data policy.
	 */
	DataPolicy save(DataPolicy dataPolicy) throws DuplicateKeyException;

	/**
	 * Updates the given data policy.
	 * NOTE: be cautious when updating/removing a policy that you aren't changing the terms of what a user has consented to.
	 * Best practice is to treat a data policy object as immutable once it's active.
	 *
	 * @param dataPolicy the data policy to update.
	 * @return the new persisted instance of the data policy object.
	 * @throws DuplicateKeyException - if trying to update to an existing data policy.
	 */
	DataPolicy update(DataPolicy dataPolicy) throws DuplicateKeyException;

	/**
	 * Delete the data policy.
	 * NOTE: be cautious when updating/removing a policy that you aren't changing the terms of what a user has consented to.
	 * Best practice is to treat a data policy object as immutable once it's active.
	 *
	 * @param dataPolicy the data policy to remove.
	 * @throws EpServiceException - in case of any errors.
	 */
	void remove(DataPolicy dataPolicy) throws EpServiceException;

	/**
	 * List all data policies stored in the database.
	 *
	 * @return a list of data policies.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<DataPolicy> list() throws EpServiceException;

	/**
	 * List data policies by provided states.
	 *
	 * @param state1 data policy state.
	 * @param state2 data policy state.
	 * @return a list of data policies.
	 */
	List<DataPolicy> findByStates(DataPolicyState state1, DataPolicyState state2);

	/**
	 * Load the data policy with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param dataPolicyUid the data policy UID
	 * @return the data policy if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	DataPolicy load(long dataPolicyUid) throws EpServiceException;

	/**
	 * Load the dataPolicy with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param dataPolicyUid the data policy UID.
	 * @return the data policy if UID exists, otherwise null.
	 * @throws EpServiceException - in case of any errors.
	 */
	DataPolicy get(long dataPolicyUid) throws EpServiceException;

	/**
	 * Retrieve the data policy with the given guid.
	 *
	 * @param guid the guid of the data policy.
	 * @return the data policy with the given guid.
	 * @throws EpServiceException - in case of any errors.
	 */
	DataPolicy findByGuid(String guid) throws EpServiceException;

	/**
	 * Retrieve the active data policy with the given guid.
	 *
	 * @param guid the guid of the data policy.
	 * @return the active data policy with the given guid.
	 * @throws EpServiceException - in case of any errors.
	 */
	DataPolicy findActiveByGuid(String guid) throws EpServiceException;

	/**
	 * Retrieve the list of data policies with the given guids.
	 *
	 * @param guids the guids of the data policy .
	 * @return the data policy with the given guids.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<DataPolicy> findByGuids(List<String> guids) throws EpServiceException;

	/**
	 * Retrieve the list of active data policies for the given segments.
	 * Active implies that both the data policy's state is set to 'active' and
	 * that the data policy's start date is before the current date.
	 *
	 * @param segmentCodes list representation of the segments.
	 * @param store        the store to find the data policies for.
	 * @return list of data policy objects corresponding to the segments.
	 * @throws EpServiceException - in case of any errors.
	 */
	List<DataPolicy> findActiveDataPoliciesForSegmentsAndStore(List<String> segmentCodes, String store) throws EpServiceException;

	/**
	 * Check if the data policies are enabled for a store via a system configuration setting.
	 *
	 * @param store the store to find the data policies for.
	 * @return true if data policies are enabled for a store.
	 */
	boolean areDataPoliciesEnabledForTheStore(String store);
}
