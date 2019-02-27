/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;

/**
 * Test the shopping item subtotal calculator implementation.
 */
public class ShoppingItemSubtotalCalculatorImplTest {

	private static final Currency CURRENCY = Currency.getInstance(Locale.CANADA);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private ShoppingCartPricingSnapshot cartPricingSnapshot;

	private ShoppingItemSubtotalCalculatorImpl calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new ShoppingItemSubtotalCalculatorImpl();
	}

	@Test
	public void verifySubtotalZeroWhenNoCartItems() throws Exception {
		final Money expectedSubtotal = Money.valueOf(BigDecimal.ZERO, CURRENCY);

		final Money actual = calculator.calculate(Stream.empty(), cartPricingSnapshot, CURRENCY);

		assertEquals(expectedSubtotal, actual);
	}

	@Test
	public void verifySubtotalSumOfShoppingItems() throws Exception {
		final Money shoppingItemMoney1 = Money.valueOf(new BigDecimal("1.00"), CURRENCY);
		final Money shoppingItemMoney2 = Money.valueOf(new BigDecimal("2.00"), CURRENCY);
		final Money expectedSubtotal = Money.valueOf(new BigDecimal("3.00"), CURRENCY); // 1 + 2 = 3

		final ShoppingItem shoppingItem1 = createShoppingItemWithTotal(shoppingItemMoney1);
		final ShoppingItem shoppingItem2 = createShoppingItemWithTotal(shoppingItemMoney2);

		final Stream<ShoppingItem> items = Stream.of(shoppingItem1, shoppingItem2);

		final Money actual = calculator.calculate(items, cartPricingSnapshot, CURRENCY);

		assertEquals(expectedSubtotal, actual);
	}

	@Test
	public void verifySubtotalSumOfOrderSkus() throws Exception {

		final Money orderSkuMoney1 = Money.valueOf(new BigDecimal("10.00"), CURRENCY);
		final Money orderSkuMoney2 = Money.valueOf(new BigDecimal("20.00"), CURRENCY);
		final Money expectedSubtotal = Money.valueOf(new BigDecimal("30.00"), CURRENCY); // 10 + 20 = 30

		final OrderSku orderSku1 = createOrderSkuWithTotal(orderSkuMoney1);
		final OrderSku orderSku2 = createOrderSkuWithTotal(orderSkuMoney2);
		final Stream<ShoppingItem> items = Stream.of(orderSku1, orderSku2);
		final Money actual = calculator.calculate(items, cartPricingSnapshot, CURRENCY);

		assertEquals(expectedSubtotal, actual);
	}

	private ShoppingItem createShoppingItemWithTotal(final Money money) {
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class, "Shopping Item " + UUID.randomUUID());

		context.checking(new Expectations() {
			{
				oneOf(cartPricingSnapshot).getShoppingItemPricingSnapshot(shoppingItem);
				will(returnValue(createShoppingItemPricingSnapshotWithTotal(money)));
			}
		});

		return shoppingItem;
	}

	private OrderSku createOrderSkuWithTotal(final Money money) {
		final OrderSku orderSku = context.mock(OrderSku.class, "Order Sku " + UUID.randomUUID());

		context.checking(new Expectations() {
			{
				oneOf(cartPricingSnapshot).getShoppingItemPricingSnapshot(orderSku);
				will(returnValue(createShoppingItemPricingSnapshotWithTotal(money)));
			}
		});

		return orderSku;
	}

	private ShoppingItemPricingSnapshot createShoppingItemPricingSnapshotWithTotal(final Money money) {
		final ShoppingItemPricingSnapshot itemPricingSnapshot =
			context.mock(ShoppingItemPricingSnapshot.class, "Item Pricing Snapshot " + UUID.randomUUID());

		final PriceCalculator priceCalc = context.mock(PriceCalculator.class, "Price Calculator " + UUID.randomUUID());

		context.checking(new Expectations() {
			{
				oneOf(itemPricingSnapshot).getPriceCalc();
				will(returnValue(priceCalc));

				oneOf(priceCalc).withCartDiscounts();
				will(returnValue(priceCalc));

				oneOf(priceCalc).getMoney();
				will(returnValue(money));
			}
		});

		return itemPricingSnapshot;
	}

}
