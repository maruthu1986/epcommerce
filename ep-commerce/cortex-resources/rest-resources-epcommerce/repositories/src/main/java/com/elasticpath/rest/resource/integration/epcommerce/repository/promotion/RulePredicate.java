/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion;

/**
 * A RulePredicate is used to test whether a promotion Rule matches a condition.
 * @param <RULE> The promotion rule type, either {@link com.elasticpath.domain.rules.AppliedRule} or {@link com.elasticpath.domain.rules.Rule}
 */
@FunctionalInterface
public interface RulePredicate<RULE> {

	/**
	 * Whether the promotion Rule satisfies the predicate criteria.
	 * @param rule The rule.
	 * @return yay or nay.
	 */
	boolean isSatisfied(RULE rule);
}
