/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer;

import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.EpPersistenceService;

/**
 * Provide customerGroup-related business service.
 */
public interface CustomerGroupService extends EpPersistenceService {
	/**
	 * Adds the given customerGroup.
	 *
	 * @param customerGroup the customerGroup to add
	 * @return the persisted instance of customerGroup
	 * @throws GroupExistException - if a customerGroup with the specified name already exists.
	 */
	CustomerGroup add(CustomerGroup customerGroup) throws GroupExistException;

	/**
	 * Updates the given customerGroup.
	 *
	 * @param customerGroup the customerGroup to update
	 * @return the updated instance of CustomerGroup
	 * @throws GroupExistException - if a customerGroup with the specified name already exists.
	 * @see CustomerGroup
	 */
	CustomerGroup update(CustomerGroup customerGroup) throws GroupExistException;

	/**
	 * Delete the customerGroup.
	 *
	 * @param customerGroup the customerGroup to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(CustomerGroup customerGroup) throws EpServiceException;

	/**
	 * Remove all customer roles from customer group.
	 * 
	 * @param customerGroup the customer group
	 * @return the customer group
	 * @throws EpServiceException in case of any errors
	 */
	CustomerGroup removeAllRoles(CustomerGroup customerGroup) throws EpServiceException;

	/**
	 * List all customerGroups stored in the database.
	 *
	 * @return a list of customerGroups
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	List<CustomerGroup> list() throws EpServiceException;

	/**
	 * Load the customerGroup with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param customerGroupUid the customerGroup UID
	 *
	 * @return the customerGroup if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerGroup load(long customerGroupUid) throws EpServiceException;

	/**
	 * Get the customerGroup with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param customerGroupUid the customerGroup UID
	 *
	 * @return the customerGroup if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerGroup get(long customerGroupUid) throws EpServiceException;


	/**
	 * Return the default customerGroup, namely, the group with name "PUBLIC".
	 *
	 * @return the default customerGroup.
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerGroup getDefaultGroup() throws EpServiceException;

	/**
	 * Check the given customer group's name exists or not.
	 *
	 * @param groupName - the group name to check
	 * @return true if the given group name exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean groupExists(String groupName) throws EpServiceException;

	/**
	 * Check if a different customer group with the given customer group's name exists exists or not.
	 *
	 * @param customerGroup - the customerGroup to check
	 * @return true if a different customer group with the same name exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean groupExists(CustomerGroup customerGroup) throws EpServiceException;

	/**
	 * Find the customer group with the given group name.
	 *
	 * @param groupName - the customer group name
	 *
	 * @return the customerGroup with the given name if exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerGroup findByGroupName(String groupName) throws EpServiceException;

	/**
	 * Find the customer group with the given guid.
	 *
	 * @param guid the guid of the customergruop
	 *
	 * @return the customerGroup with the given guid if exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerGroup findByGuid(String guid) throws EpServiceException;

	/**
	 * Checks if a group is in use.
	 * 
	 * @param customerGroup the customer group to check
	 * @return True if in use, else false
	 */
	boolean checkIfInUse(CustomerGroup customerGroup);

	/**
	 * Checks if a group is a system group i.e. not to be deleted.
	 * 
	 * @param customerGroup the customer group to check
	 * @return True if is system group, else false
	 */
	boolean checkIfSystemGroup(CustomerGroup customerGroup);

}
