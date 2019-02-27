/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.matchers;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.RuleService;

/**
 * Filters a collection of applied promotions to carts.
 */
@Singleton
@Named("cartPromotionRuleMatcher")
public class CartPromotionRuleMatcherImpl extends AbstractPromotionRuleMatcher<Long, Rule> {

	/**
	 * Constructor.
	 * @param ruleService The promotion rule service.
	 */
	@Inject
	public CartPromotionRuleMatcherImpl(
			@Named("ruleService")
			final RuleService ruleService) {
		super(ruleService);
	}

	@Override
	protected Collection<Rule> getRules(final Collection<Long> ruleIds) {
		return super.getRulesByRuleIds(ruleIds);
	}

	@Override
	protected String getGuid(final Rule rule) {
		return rule.getCode();
	}
}