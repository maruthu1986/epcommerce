/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * A test.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedShippingRulePredicateTest {

	@Mock
	private ShoppingCartPricingSnapshot pricingSnapshot;

	@Mock
	private PromotionRecordContainer promotionRecordContainer;

	@Before
	public void setUp() {
		when(pricingSnapshot.getPromotionRecordContainer())
				.thenReturn(promotionRecordContainer);
	}

	@Test
	public void testDiscountedShippingWithMatchingRuleIsSatisfied() {
		ShippingOption shippingOption = givenShippingOptionWithDiscount();
		AppliedShippingRulePredicate predicate = new AppliedShippingRulePredicate(pricingSnapshot, shippingOption);
		Rule rule = mock(Rule.class);
		long expectedRuleUid = 1L;
		when(rule.getUidPk()).thenReturn(expectedRuleUid);
		HashSet<Long> appliedRules = new HashSet<>();
		appliedRules.add(expectedRuleUid);
		when(promotionRecordContainer.getAppliedRulesByShippingOption(shippingOption)).thenReturn(appliedRules);
		assertTrue(predicate.isSatisfied(rule));
	}

	@Test
	public void testNonDiscountedShippingIsNotSatisfied() {
		ShippingOption shippingOption = givenShippingOptionWithoutDiscount();
		AppliedShippingRulePredicate predicate = new AppliedShippingRulePredicate(pricingSnapshot, shippingOption);
		Rule rule = mock(Rule.class);
		long expectedRuleUid = 1L;
		HashSet<Long> appliedRules = new HashSet<>();
		appliedRules.add(expectedRuleUid);
		when(promotionRecordContainer.getAppliedRulesByShippingOption(shippingOption)).thenReturn(appliedRules);
		assertFalse(predicate.isSatisfied(rule));
	}

	@Test
	public void testDiscountedShippingWithNonMatchingRuleIsNotSatisfied() {
		ShippingOption shippingOption = givenShippingOptionWithDiscount();
		AppliedShippingRulePredicate predicate = new AppliedShippingRulePredicate(pricingSnapshot, shippingOption);
		Rule rule = mock(Rule.class);
		long expectedRuleUid = 1L;
		when(rule.getUidPk()).thenReturn(expectedRuleUid);
		HashSet<Long> appliedRules = new HashSet<>();
		long anotherNonMatchingRuleUid = 0L;
		appliedRules.add(anotherNonMatchingRuleUid);
		when(promotionRecordContainer.getAppliedRulesByShippingOption(shippingOption)).thenReturn(appliedRules);
		assertFalse(predicate.isSatisfied(rule));
	}

	@Test
	public void testDiscountedShippingWithNoAppliedRulesIsNotSatisfied() {
		ShippingOption shippingOption = givenShippingOptionWithDiscount();
		AppliedShippingRulePredicate predicate = new AppliedShippingRulePredicate(pricingSnapshot, shippingOption);
		Rule rule = mock(Rule.class);
		long expectedRuleUid = 1L;
		when(rule.getUidPk()).thenReturn(expectedRuleUid);
		when(promotionRecordContainer.getAppliedRulesByShippingOption(shippingOption)).thenReturn(new HashSet<>());
		assertFalse(predicate.isSatisfied(rule));
	}

	private ShippingOption givenShippingOptionWithDiscount() {
		return mockShippingOption(BigDecimal.TEN, BigDecimal.ONE);
	}

	private ShippingOption givenShippingOptionWithoutDiscount() {
		return mockShippingOption(BigDecimal.TEN, BigDecimal.TEN);
	}

	private ShippingOption mockShippingOption(
			final BigDecimal regularShippingCostAmount, final BigDecimal actualShippingCostAmount) {
		final ShippingOption shippingOption = mock(ShippingOption.class);
		final Money regularPrice = Money.valueOf(regularShippingCostAmount, Currency.getInstance("CAD"));
		final Money discountedPrice = Money.valueOf(actualShippingCostAmount, Currency.getInstance("CAD"));

		final ShippingPricingSnapshot shippingPricingSnapshot = mock(ShippingPricingSnapshot.class);

		when(pricingSnapshot.getShippingPricingSnapshot(shippingOption))
				.thenReturn(shippingPricingSnapshot);

		when(shippingPricingSnapshot.getShippingListPrice()).thenReturn(regularPrice);
		when(shippingPricingSnapshot.getShippingPromotedPrice()).thenReturn(discountedPrice);

		return shippingOption;
	}

}
