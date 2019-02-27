/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;

/**
 * Unit test for {@link PricedShippableItemPopulatorVisitorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricedShippableItemPopulatorVisitorImplTest {

	private static final Currency CAD = Currency.getInstance("CAD");

	private static final Money UNIT_PRICE_BEFORE_SUBTOTAL_DISCOUNT = Money.valueOf("10", CAD);
	private static final Money TOTAL_PRICE_BEFORE_SUBTOTAL_DISCOUNT = Money.valueOf("50", CAD);

	private static final Money APPORTIONED_SUBTOTAL_UNIT_DISCOUNT = Money.valueOf("5", CAD);
	private static final Money APPORTIONED_SUBTOTAL_TOTAL_DISCOUNT = Money.valueOf("25", CAD);

	@Mock
	private ShoppingItem shoppingItem;

	@Mock(answer = RETURNS_DEEP_STUBS)
	private ShoppingItemPricingSnapshot itemPricingSnapshot;

	@Mock
	private PricedShippableItemBuilderPopulator populator;

	@Mock
	private ShippableItemPricing shippableItemPricing;

	private PricedShippableItemPopulatorVisitorImpl objectUnderTest;

	@Before
	public void setUp() {
		objectUnderTest = new PricedShippableItemPopulatorVisitorImpl();

		when(shippableItemPricing.getShoppingItemPricingSnapshot()).thenReturn(itemPricingSnapshot);
		when(shippableItemPricing.getApportionedItemSubtotalUnitDiscount()).thenReturn(APPORTIONED_SUBTOTAL_UNIT_DISCOUNT);
		when(shippableItemPricing.getApportionedItemSubtotalDiscount()).thenReturn(APPORTIONED_SUBTOTAL_TOTAL_DISCOUNT);

		when(populator.withUnitPrice(any(Money.class))).thenReturn(populator);
		when(populator.withTotalPrice(any(Money.class))).thenReturn(populator);

		when(itemPricingSnapshot.getPriceCalc().forUnitPrice().withCartDiscounts().getMoney()).thenReturn(UNIT_PRICE_BEFORE_SUBTOTAL_DISCOUNT);
		when(itemPricingSnapshot.getPriceCalc().withCartDiscounts().getMoney()).thenReturn(TOTAL_PRICE_BEFORE_SUBTOTAL_DISCOUNT);
	}

	@Test
	public void verifyUnitPriceSetCorrectlyOnPopulator() {
		objectUnderTest.accept(shoppingItem, shippableItemPricing, populator);

		final Money expectedUnitPriceAfterSubtotalDiscount = UNIT_PRICE_BEFORE_SUBTOTAL_DISCOUNT.subtract(APPORTIONED_SUBTOTAL_UNIT_DISCOUNT);
		verify(populator).withUnitPrice(expectedUnitPriceAfterSubtotalDiscount);
	}

	@Test
	public void verifyTotalPriceSetCorrectlyOnPopulator() {
		objectUnderTest.accept(shoppingItem, shippableItemPricing, populator);

		final Money expectedTotalItemPriceAfterSubtotalDiscount = TOTAL_PRICE_BEFORE_SUBTOTAL_DISCOUNT.subtract(APPORTIONED_SUBTOTAL_TOTAL_DISCOUNT);
		verify(populator).withTotalPrice(expectedTotalItemPriceAfterSubtotalDiscount);
	}

	@Test
	public void verifyNPEThrownWhenNoPricingInformationSupplied() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.accept(shoppingItem, null, populator))
				.withMessage("No pricing provided for ShoppingItem");
	}
}