/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.discounts.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.discounts.Discount;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>CartCategoryPercentDiscountImpl</code>.
 */
public class CartCategoryPercentDiscountImplTest {

	private static final BigDecimal TWENTY = BigDecimal.valueOf(20);

	private static final BigDecimal FORTY = BigDecimal.valueOf(40);

	private static final String EXCEPTION_STR = "exceptionStr";

	private static final String CART_ITEM_PRODUCT_CODE = "product1";

	private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);

	private static final long RULE_ID = 123L;

	private static final long ACTION_ID = 456L;

	private static final int MAX_ITEMS = 4;

	private static int cartItemCount;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	@Mock
	private TotallingApplier totallingApplier;

	@Mock
	private DiscountItemContainer container;

	private PercentDiscountTestExpectations expectations;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		expectations = new PercentDiscountTestExpectations(context, container, totallingApplier, RULE_ID, ACTION_ID);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() {
			{
				final PromotionRuleExceptions exceptions = context.mock(PromotionRuleExceptions.class);
				oneOf(beanFactory).getBean(ContextIdNames.PROMOTION_RULE_EXCEPTIONS);
				will(returnValue(exceptions));
				oneOf(beanFactory).getBean(ContextIdNames.TOTALLING_APPLIER);
				will(returnValue(totallingApplier));

				oneOf(exceptions).populateFromExceptionStr(EXCEPTION_STR);
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private CartCategoryPercentDiscountImpl createCartCategoryPercentDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String categoryCode,
			final String percent, final int maxItems, final String exceptions) {
		return new CartCategoryPercentDiscountImpl(ruleElementType, ruleId, actionId, categoryCode, percent, exceptions, maxItems) {
			private static final long serialVersionUID = -2707640507313602956L;

			@Override
			protected boolean cartItemIsEligibleForPromotion(
					final DiscountItemContainer discountItemContainer, final ShoppingItem cartItem,
					final String productCode,
					final PromotionRuleExceptions promotionRuleExceptions) {
				return true;
			}
		};
	}

	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testCalculateOnEmptyCart() {
		final int maxItems = 0;
		expectations.givenATotallingApplierWithMaxItemsThatApplies(maxItems, false);
		expectations.givenAnEmptyShoppingContainer();
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(BigDecimal.ZERO));
			}
		});

		CartCategoryPercentDiscountImpl discount =
				createCartCategoryPercentDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, "50", maxItems, EXCEPTION_STR);
		BigDecimal total = discount.calculate(container);
		assertEquals("Empty cart should not have any discount amount.", BigDecimal.ZERO, total);
	}

	/**
	 * Test case for apply() with empty cart.
	 */
	@Test
	public void testApplyOnEmptyCart() {
		final int maxItems = 0;
		expectations.givenATotallingApplierWithMaxItemsThatApplies(maxItems, true);
		expectations.givenAnEmptyShoppingContainer();
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(BigDecimal.ZERO));
			}
		});

		CartCategoryPercentDiscountImpl discount =
				createCartCategoryPercentDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, "50", maxItems, EXCEPTION_STR);
		BigDecimal total = discount.apply(container);
		assertEquals("Empty cart should not have any discount amount.", BigDecimal.ZERO, total);
	}

	/**
	 * Test case for apply() with 1 cart item.
	 */
	@Test
	public void testApplyOnSingleItem() {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final int maxItems = 0;
		final BigDecimal discountPercent = new BigDecimal("0.25").setScale(CartCategoryPercentDiscountImpl.CALCULATION_SCALE);
		final BigDecimal itemPrice = BigDecimal.TEN;
		final BigDecimal totalDiscountAmount = TWENTY;

		expectations.givenATotallingApplierWithMaxItemsThatApplies(maxItems, true);
		expectations.givenItemsWithPrices(ImmutableMap.of(cartItem, itemPrice));
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem, itemPrice.multiply(discountPercent));
			}
		});

		final CartCategoryPercentDiscountImpl discount =
				createCartCategoryPercentDiscountImpl("", RULE_ID, ACTION_ID,
						CART_ITEM_PRODUCT_CODE, discountPercent.multiply(HUNDRED_PERCENT).toString(), maxItems, EXCEPTION_STR);
		BigDecimal total = discount.doApply(true, container);
		assertEquals("Amount does not matched expected value for cart with 1 item", totalDiscountAmount, total);
	}

	/**
	 * Test case for doApply() with 2 items.
	 */
	@Test
	public void testApplyOnTwoItems() {
		final ShoppingItem cartItem1 = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final ShoppingItem cartItem2 = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final BigDecimal discountPercent = new BigDecimal("0.35").setScale(CartCategoryPercentDiscountImpl.CALCULATION_SCALE);
		final BigDecimal itemPrice1 = BigDecimal.ONE;
		final BigDecimal itemPrice2 = BigDecimal.TEN;
		final BigDecimal totalDiscountAmount = FORTY;

		expectations.givenATotallingApplierWithMaxItemsThatApplies(MAX_ITEMS, true);
		expectations.givenItemsWithPrices(ImmutableMap.of(cartItem1, itemPrice1, cartItem2, itemPrice2));
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem1, itemPrice1.multiply(discountPercent));
				oneOf(totallingApplier).apply(cartItem2, itemPrice2.multiply(discountPercent));
			}
		});

		final CartCategoryPercentDiscountImpl discount = createCartCategoryPercentDiscountImpl("", RULE_ID, ACTION_ID,
				CART_ITEM_PRODUCT_CODE, discountPercent.multiply(HUNDRED_PERCENT).toString(), MAX_ITEMS, EXCEPTION_STR);
		BigDecimal total = discount.doApply(true, container);
		assertEquals(totalDiscountAmount, total);
	}

	@Test
	public void testDiscountRounding() {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final String discountPercent = "57.5";
		final BigDecimal itemPrice = new BigDecimal("1000");

		final BigDecimal expectedDiscount = new BigDecimal("575").setScale(CartCategoryPercentDiscountImpl.CALCULATION_SCALE);

		expectations.givenATotallingApplierWithMaxItemsThatApplies(MAX_ITEMS, true);
		expectations.givenItemsWithPrices(ImmutableMap.of(cartItem, itemPrice));
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).apply(cartItem, expectedDiscount);
				oneOf(totallingApplier).getTotalDiscount(); will(returnValue(expectedDiscount));
			}
		});

		Discount discount = createCartCategoryPercentDiscountImpl("", RULE_ID, ACTION_ID, CART_ITEM_PRODUCT_CODE, discountPercent,
				MAX_ITEMS, EXCEPTION_STR);
		BigDecimal result = discount.apply(container);
		assertEquals("The result should be exactly 57.5% off", expectedDiscount, result);

	}
}

