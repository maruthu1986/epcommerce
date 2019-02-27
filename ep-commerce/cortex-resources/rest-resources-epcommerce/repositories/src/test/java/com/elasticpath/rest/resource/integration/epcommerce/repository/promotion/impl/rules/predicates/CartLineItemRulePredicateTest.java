/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;

/**
 * Unit Tests for {@link CartLineItemRulePredicate}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartLineItemRulePredicateTest {

	private static final Long EXPECTED_RULE_ID = 1L;
	private static final Long UNEXPECTED_RULE_ID = 2L;

	private CartLineItemRulePredicate predicate;

	@Mock
	private Rule mockRule;

	@Before
	public void setUp() {
		List<Long> ruleIds = Arrays.asList(EXPECTED_RULE_ID);
		predicate = new CartLineItemRulePredicate(ruleIds);
	}

	@Test
	public void testIsSatisfied() {
		mockRuleAction(DiscountType.CART_ITEM_DISCOUNT, EXPECTED_RULE_ID);

		boolean result = predicate.isSatisfied(mockRule);

		assertTrue("Predicate should be satisfied when rule action discount is for a cart line item", result);
	}

	@Test
	public void testIsNotSatisfiedWithValidRuleIdInvalidDiscountType() {
		mockRuleAction(DiscountType.CART_SUBTOTAL_DISCOUNT, EXPECTED_RULE_ID);

		boolean result = predicate.isSatisfied(mockRule);

		assertFalse("Predicate should not be satisfied when rule action discount is not for a cart line item", result);
	}

	@Test
	public void testIsNotSatisfiedWithInvalidRuleIdValidDiscountType() {
		mockRuleAction(DiscountType.CART_ITEM_DISCOUNT, UNEXPECTED_RULE_ID);

		boolean result = predicate.isSatisfied(mockRule);

		assertFalse("Predicate should not be satisfied when rule is not applied for a cart line item", result);
	}

	private void mockRuleAction(final DiscountType discountType, final Long ruleId) {
		when(mockRule.getUidPk()).thenReturn(ruleId);
		RuleAction mockRuleAction = mock(RuleAction.class);
		when(mockRuleAction.getDiscountType()).thenReturn(discountType);
		when(mockRule.getActions()).thenReturn(Sets.newSet(mockRuleAction));
	}
}
