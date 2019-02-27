/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import java.util.Collection;

import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.RulePredicate;

/**
 * This predicate matches a Rule as an applied promotion for a cart line item.
 */
public class CartLineItemRulePredicate implements RulePredicate<Rule> {

	private final Collection<Long> appliedRules;

	/**
	 * Constructor.
	 *
	 * @param appliedRules the applied rules for the line item
	 */
	public CartLineItemRulePredicate(final Collection<Long> appliedRules) {
		this.appliedRules = appliedRules;
	}

	@Override
	public boolean isSatisfied(final Rule rule) {
		long ruleId = rule.getUidPk();

		if (appliedRules.contains(ruleId)) {
			for (RuleAction ruleAction : rule.getActions()) {
				if (DiscountType.CART_ITEM_DISCOUNT.equals(ruleAction.getDiscountType())) {
					return true;
				}
			}
		}
		return false;
	}
}
