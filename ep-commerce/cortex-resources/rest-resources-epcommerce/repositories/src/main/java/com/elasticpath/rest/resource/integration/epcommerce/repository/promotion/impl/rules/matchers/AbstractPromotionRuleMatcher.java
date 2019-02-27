/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.matchers;

import java.util.Collection;
import java.util.stream.Collectors;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.AppliedPromotionRuleAware;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRuleMatcher;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.RulePredicate;
import com.elasticpath.service.rules.RuleService;

/**
 * AbstractPromotionRuleMatcher encapsulates the algorithm of filtering a collection of
 * applied rules using a predicate.
 * @param <ID> the type of the applied rule records.
 * @param <RULE> The rule type, either {@link com.elasticpath.domain.rules.AppliedRule} or {@link com.elasticpath.domain.rules.Rule}
 */
public abstract class AbstractPromotionRuleMatcher<ID, RULE> implements PromotionRuleMatcher<ID, RULE> {

	/** The rule service. */
	private final RuleService ruleService;

	/**
	 * Constructor.
	 * @param ruleService The rule service.
	 */
	public AbstractPromotionRuleMatcher(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * <p>
	 * {@inheritDoc}.
	 * </p>
	 * <p>
	 * This implementation uses the template method to encode the common algorithm
	 * of filtering a collection of applied rules using a predicate.
	 * The variability in the algorithm is in the way that rule codes are
	 * retrieved, see {@link #getGuid(Object)}, and in the way that rules themselves are
	 * retrieved, see {@link #getRules(Collection<ID>)}.
	 * </p>
	 * @param ruleSource the rule aware input object to search.
	 * @param rulePredicate The promotion rule predicate.
	 * @return Collection<String> The matching promotion ids.
	 */
	@Override
	public Collection<String> findMatchingAppliedRules(final AppliedPromotionRuleAware<ID> ruleSource, final RulePredicate<RULE> rulePredicate) {

		Collection<ID> appliedRules = ruleSource.getAppliedRules();

		Collection<RULE> rules = getRules(appliedRules);

		return rules.stream()
					.filter(rulePredicate::isSatisfied)
					.map(this::getGuid)
					.collect(Collectors.toSet());
	}

	/**
	 * Gets the identifier for a rule.
	 * @param rule The rule.
	 * @return The rule identifier
	 */
	protected abstract String getGuid(RULE rule);

	/**
	 * Gets list of Rule entities for a given list of ids.
	 * @param appliedRule The applied rule ids.
	 * @return The list of rules.
	 */
	protected abstract Collection<RULE> getRules(Collection<ID> appliedRule);

	/**
	 * Called by {@link CartPromotionRuleMatcherImpl}.
	 * @param ruleIds The list of rule ids to fetch Rule entities for.
	 * @return If found, the list of optimized Rule entities, otherwise, an empty list.
	 */
	// TODO: Move this method to CE (probably RuleService).
	protected Collection<Rule> getRulesByRuleIds(final Collection<Long> ruleIds) {
		return ruleService.findByUids(ruleIds);
	}
}