/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotiondetails;

import java.util.Collection;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.rules.RuleService;

/**
 * To retrieve promotion details report parameters.
 */
public class PromotionDetailsReportParametersRetriever {
	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();

	private final RuleService ruleService = LoginManager.getInstance().getBean(ContextIdNames.RULE_SERVICE);

	/**
	 * Gets all available stores.
	 * 
	 * @return all stores.
	 */
	public Collection<Store> getAvailableStores() {
		return reportUtility.getAuthorizedStores();
	}

	/**
	 * Gets all available promotions.
	 * 
	 * @param storeCode store code.
	 * @return a collection rules.
	 */
	public Collection<Rule> getAvailablePromotions(final String storeCode) {
		return ruleService.findByScenarioAndStore(RuleScenarios.CART_SCENARIO, storeCode);
	}
}
