/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;

/**
 * Unit Tests for {@link CartRulePredicate}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartRulePredicateTest {

	private final CartRulePredicate predicate = new CartRulePredicate();

	@Mock
	private Rule mockRule;

	@Test
	public void testIsSatisfied() {
		mockRuleAction(DiscountType.CART_SUBTOTAL_DISCOUNT);

		boolean result = predicate.isSatisfied(mockRule);

		assertTrue("Predicate should be satisfied when rule action discount is for a cart", result);
	}

	@Test
	public void testIsNotSatisfied() {
		mockRuleAction(DiscountType.CART_ITEM_DISCOUNT);

		boolean result = predicate.isSatisfied(mockRule);

		assertFalse("Predicate should not be satisfied when rule action discount is not for a cart", result);
	}

	private void mockRuleAction(final DiscountType discountType) {
		RuleAction mockRuleAction = mock(RuleAction.class);
		when(mockRuleAction.getDiscountType()).thenReturn(discountType);
		when(mockRule.getActions()).thenReturn(Sets.newSet(mockRuleAction));
	}
}
