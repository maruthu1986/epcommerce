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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.RulePredicate;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.awares
	.AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter;
import com.elasticpath.service.rules.RuleService;

/**
 * A test.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartPromotionRuleMatcherImplTest {

	private static final String RULE_CODE = "testRuleCode";
	private static final Long RULE_ID = 1L;
	private static final Set<Long> APPLIED_RULES = new HashSet<>(Collections.singleton(RULE_ID));

	@Mock
	private RuleService ruleService;
	@InjectMocks
	private CartPromotionRuleMatcherImpl promotionRuleMatcher;
	@Mock
	RulePredicate<Rule> rulePredicate;
	@Mock
	ShoppingCartPricingSnapshot pricingSnapshot;
	@Mock
	Rule mockRule;
	@Mock
	private PromotionRecordContainer promotionRecordContainer;

	@Before
	public void setUp() {
		when(pricingSnapshot.getPromotionRecordContainer()).thenReturn(promotionRecordContainer);
	}

	@Test
	public void testFindMatchingAppliedRules() {
		when(mockRule.getCode()).thenReturn(RULE_CODE);
		when(ruleService.findByUids(APPLIED_RULES)).thenReturn(Collections.singletonList(mockRule));
		when(rulePredicate.isSatisfied(mockRule)).thenReturn(true);
		when(promotionRecordContainer.getAppliedRules()).thenReturn(APPLIED_RULES);

		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter cartAdapter
				= new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		Collection<String> matchingAppliedRules
				= promotionRuleMatcher.findMatchingAppliedRules(cartAdapter, rulePredicate);

		assertTrue(matchingAppliedRules.contains(RULE_CODE));
	}

	@Test
	public void testNoRulesOnCart() {
		when(promotionRecordContainer.getAppliedRules()).thenReturn(Collections.emptySet());
		when(ruleService.findByUids(Collections.emptySet())).thenReturn(Collections.emptyList());

		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter cartAdapter
				= new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		Collection<String> matchingAppliedRules
				= promotionRuleMatcher.findMatchingAppliedRules(cartAdapter, rulePredicate);

		assertTrue(matchingAppliedRules.isEmpty());
	}

	@Test
	public void testNoMatchingRules() {
		when(ruleService.findByUids(APPLIED_RULES)).thenReturn(Collections.singletonList(mockRule));
		when(rulePredicate.isSatisfied(mockRule)).thenReturn(false);
		when(promotionRecordContainer.getAppliedRules()).thenReturn(APPLIED_RULES);

		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter cartAdapter
				= new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		Collection<String> matchingAppliedRules
				= promotionRuleMatcher.findMatchingAppliedRules(cartAdapter, rulePredicate);

		assertTrue(matchingAppliedRules.isEmpty());
	}
}