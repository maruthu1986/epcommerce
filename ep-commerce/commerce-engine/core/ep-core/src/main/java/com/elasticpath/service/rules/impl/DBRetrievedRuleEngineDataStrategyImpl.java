/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.rules.impl;

import java.util.List;

import com.elasticpath.service.rules.RuleService;

/**
 * Rule engine data strategy that retrieves data from the database.
 */
public class DBRetrievedRuleEngineDataStrategyImpl implements RuleEngineDataStrategy {

	private RuleService ruleService;

	@Override
	public List<Object[]> findActiveRuleIdSellingContextByScenarioAndStore(final int scenario, final String storeCode) {
		return getRuleService().findActiveRuleIdSellingContextByScenarioAndStore(scenario, storeCode);
	}
	
	/**
	 *
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 *
	 * @return the ruleService
	 */
	public RuleService getRuleService() {
		return ruleService;
	}

}
