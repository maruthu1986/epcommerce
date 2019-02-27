/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.rules.impl;

import java.util.List;

/**
 * Retrieval strategy for data required by the rule engine.
 */
public interface RuleEngineDataStrategy {

	/**
	 * @param scenario rule scenario
	 * @param storeCode store code
	 * @return list of entries with rule id and its selling context
	 */
	List<Object[]> findActiveRuleIdSellingContextByScenarioAndStore(int scenario, String storeCode);
}
