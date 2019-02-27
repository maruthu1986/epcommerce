/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.matchers;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.service.rules.RuleService;

/**
 * Filters a collection of applied promotions to orders.
 */
@Singleton
@Named("orderPromotionRuleMatcher")
public class OrderPromotionRuleMatcherImpl extends AbstractPromotionRuleMatcher<AppliedRule, AppliedRule> {

	/**
	 * Constructor.
	 * @param ruleService The promotion rule service.
	 */
	@Inject
	public OrderPromotionRuleMatcherImpl(
			@Named("ruleService")
			final RuleService ruleService) {
		super(ruleService);
	}

	@Override
	protected Collection<AppliedRule> getRules(final Collection<AppliedRule> appliedRules) {
		return appliedRules;
	}

	@Override
	protected String getGuid(final AppliedRule appliedRule) {
		return appliedRule.getGuid();
	}
}