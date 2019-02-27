/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.RulePredicate;

/**
 * This predicates matches all Applied Rules.
 */
public class PurchaseAppliedRulePredicate implements RulePredicate<AppliedRule> {
	/**
	 * Every rule is satisfied for a purchase if it is applied.
	 * @param appliedRule applied rule.
	 * @return true since Applied Rule is a synonym for Purchase Applied Rule.
	 */
	@Override
	public boolean isSatisfied(final AppliedRule appliedRule) {
		return true;
	}
}
