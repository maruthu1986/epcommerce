/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import java.util.Date;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.service.EpPersistenceService;

/***
 * Provides rule engine-related services.
 */
public interface RuleSetService extends EpPersistenceService  {

	/**
	 * Adds the given ruleSet.
	 *
	 * @param ruleSet the ruleSet to add
	 * @return the persisted instance of ruleSet
	 * @throws EpServiceException - in case of any errors
	 */
	RuleSet add(RuleSet ruleSet) throws EpServiceException;

	/**
	 * Updates the given ruleset.
	 *
	 * @param ruleSet the ruleSet to update
	 * @return RuleSet the updated RuleSet
	 * @throws EpServiceException - in case of any errors
	 */
	RuleSet update(RuleSet ruleSet) throws EpServiceException;

	/**
	 * Delete the ruleSet.
	 *
	 * @param ruleSet the ruleSet to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(RuleSet ruleSet) throws EpServiceException;

	/**
	 * Load the ruleSet with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param ruleSetUid the ruleSet UID
	 *
	 * @return the ruleSet if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleSet load(long ruleSetUid) throws EpServiceException;

	/**
	 * Find the rule set by its scenario id.
	 *
	 * @param scenarioId the id of the scenario (see <code>Scenarios</code> interface)
	 * @return the Rule Set if it exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	RuleSet findByScenarioId(int scenarioId) throws EpServiceException;

	/**
	 * Find the rule set by its name.
	 *
	 * @param name the name of the scenario (see <code>Scenarios</code> interface)
	 * @return the Rule Set if it exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	RuleSet findByName(String name) throws EpServiceException;

	/**
	 * Get the ruleSet with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param ruleSetUid the rule UID
	 *
	 * @return the ruleSet if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	RuleSet get(long ruleSetUid) throws EpServiceException;


	/**
	 * List all ruleSets stored in the database.
	 *
	 * @return a list of ruleSets
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	List<RuleSet> list() throws EpServiceException;

	/**
	 * Retrieves list of <code>RuleSet</code>s where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>RuleSet</code> whose last modified date is later than the specified date
	 */
	List<RuleSet> findByModifiedDate(Date date);

	/**
	 * Update the <code>ruleSet</code>'s last modified timestamp to the current time.
	 *
	 * @param ruleSet the ruleSet whose timestamp is to be updated.
	 */
	void updateLastModifiedTime(RuleSet ruleSet);

}
