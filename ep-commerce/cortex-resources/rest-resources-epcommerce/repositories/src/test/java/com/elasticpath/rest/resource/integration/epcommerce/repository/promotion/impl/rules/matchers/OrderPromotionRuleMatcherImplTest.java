/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.matchers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.RulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares.AppliedPromotionRuleAwareOrderAdapter;
import com.elasticpath.service.rules.RuleService;

/**
 * Tests {@link OrderPromotionRuleMatcherImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderPromotionRuleMatcherImplTest {

	private static final String APPLIED_RULE_GUID = "RULE_CODE";

	@Mock
	private RuleService ruleService;
	private final OrderPromotionRuleMatcherImpl promotionRuleMatcher = new OrderPromotionRuleMatcherImpl(ruleService);

	@Mock
	private RulePredicate<AppliedRule> mockRulePredicate;
	@Mock
	private Order mockOrder;
	@Mock
	private AppliedRule mockAppliedRule;


	@Test
	public void testFindMatchingAppliedRulesForOrder() {
		Set<AppliedRule> appliedRules = new HashSet<>(Collections.singleton(mockAppliedRule));
		appliedRules.add(mockAppliedRule);
		when(mockAppliedRule.getGuid()).thenReturn(APPLIED_RULE_GUID);
		when(mockOrder.getAppliedRules()).thenReturn(appliedRules);
		when(mockRulePredicate.isSatisfied(mockAppliedRule)).thenReturn(true);
		AppliedPromotionRuleAwareOrderAdapter orderAdapter = new AppliedPromotionRuleAwareOrderAdapter(mockOrder);

		Collection<String> matchingAppliedRules = promotionRuleMatcher.findMatchingAppliedRules(orderAdapter, mockRulePredicate);

		assertTrue(matchingAppliedRules.contains(APPLIED_RULE_GUID));
	}

	@Test
	public void testNoAppliedRulesOnOrder() {
		Set<AppliedRule> appliedRules = new HashSet<>();
		when(mockOrder.getAppliedRules()).thenReturn(appliedRules);
		AppliedPromotionRuleAwareOrderAdapter orderAdapter = new AppliedPromotionRuleAwareOrderAdapter(mockOrder);
		Collection<String> matchingAppliedRules = promotionRuleMatcher.findMatchingAppliedRules(orderAdapter, mockRulePredicate);

		assertTrue(matchingAppliedRules.isEmpty());
	}

	@Test
	public void testNoMatchingAppliedRulesForOrder() {
		Set<AppliedRule> appliedRules = new HashSet<>();
		appliedRules.add(mockAppliedRule);
		when(mockOrder.getAppliedRules()).thenReturn(appliedRules);
		when(mockRulePredicate.isSatisfied(mockAppliedRule)).thenReturn(false);

		AppliedPromotionRuleAwareOrderAdapter orderAdapter = new AppliedPromotionRuleAwareOrderAdapter(mockOrder);
		Collection<String> matchingAppliedRules = promotionRuleMatcher.findMatchingAppliedRules(orderAdapter, mockRulePredicate);

		assertTrue(matchingAppliedRules.isEmpty());
	}

}