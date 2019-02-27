/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion;

import java.util.Collection;

/**
 * A PromotionRuleMatcher finds all of the promotion rules on a "rule aware"
 * input object that match a specific condition.
 *
 * @param <ID> the type of the applied rule records.
 * @param <RULE> The rule type, either {@link com.elasticpath.domain.rules.AppliedRule} or {@link com.elasticpath.domain.rules.Rule}
 * */
public interface PromotionRuleMatcher<ID, RULE> {

	/**
	 * Finds all of the promotions that match the specified predicate.
	 * @param ruleSource the rule aware input object to search.
	 * @param rulePredicate The promotion rule predicate.
	 * @return All the matching promotion rule codes.  May be empty if there are no matches.
	 */
	Collection<String> findMatchingAppliedRules(AppliedPromotionRuleAware<ID> ruleSource, RulePredicate<RULE> rulePredicate);
}
