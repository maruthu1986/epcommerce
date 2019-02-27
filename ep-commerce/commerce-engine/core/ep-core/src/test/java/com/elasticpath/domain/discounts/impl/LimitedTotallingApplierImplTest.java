/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Test cases for <code>LimitedTotallingApplierImpl</code>.
 */
public class LimitedTotallingApplierImplTest {

	private static final int FOUR = 4;

	private static final int ELEVEN = 11;

	private static final int NO_MAX_ITEMS = 0;

	private static final int TWO = 2;

	private static final int FIVE = 5;

	private static final BigDecimal TWO_DOLLARS = new BigDecimal("2.00");

	private static final BigDecimal THREE_DOLLARS = new BigDecimal("3.00");

	private static final BigDecimal FIVE_DOLLARS = new BigDecimal("5.00");

	private static final BigDecimal ONE_DOLLAR = new BigDecimal("1.00");

	private static int cartItemCount;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final DiscountItemContainer discountItemContainer = context.mock(DiscountItemContainer.class);

	private final ProductSkuLookup productSkuLookup = context.mock(ProductSkuLookup.class);

	/**
	 * Test case for calculating discount to 1 item.
	 */
	@Test
	public void ensureDiscountIsCalculatedButNotApplied() {
		boolean actuallyApply = false;
		BigDecimal unitDiscount = TWO_DOLLARS;
		int numberOfUnitsToDiscount = FIVE;

		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, NO_MAX_ITEMS);
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount, FIVE_DOLLARS, FIVE, numberOfUnitsToDiscount, applier);

		BigDecimal expectedDiscount = calculateExpectedTotalDiscountForCartItem(unitDiscount, numberOfUnitsToDiscount);
		Assert.assertEquals("Expected discount does not match calculated discount.", expectedDiscount,
				applier.getTotalDiscount());
	}

	/**
	 * Test case for applying discount to 1 item without limit.
	 */
	@Test
	public void ensureDiscountAppliesToAllQuantitiesOfAnItemWhenNoLimit() {
		boolean actuallyApply = true;
		BigDecimal unitDiscount = TWO_DOLLARS;
		int numberOfUnitsToDiscount = FIVE;

		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, NO_MAX_ITEMS);
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount, FIVE_DOLLARS, FIVE, numberOfUnitsToDiscount, applier);

		BigDecimal expectedDiscount = calculateExpectedTotalDiscountForCartItem(unitDiscount, numberOfUnitsToDiscount);
		Assert.assertEquals("Expected discount does not match discount * cart item quantity", expectedDiscount,
				applier.getTotalDiscount());
	}

	/**
	 * Test case for calculating discount to 1 item with quantity > limit.
	 */
	@Test
	public void ensureDiscountOnlyAppliesToLimitedQuantitiesOfAnItem() {
		boolean actuallyApply = true;
		BigDecimal unitDiscount = TWO_DOLLARS;
		int numberOfUnitsToDiscount = FOUR;

		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, numberOfUnitsToDiscount);
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount, FIVE_DOLLARS, FIVE, numberOfUnitsToDiscount, applier);

		BigDecimal expectedDiscount = calculateExpectedTotalDiscountForCartItem(unitDiscount, numberOfUnitsToDiscount);
		Assert.assertEquals("Expected discount does not match discount * max discount item quantity", expectedDiscount,
				applier.getTotalDiscount());
	}

	/**
	 * Test case for calculating discount to 1 item with quantity < limit.
	 */
	@Test
	public void ensureDiscountAppliesToAllQuantitiesOfAnItemWhenBelowLimit() {
		boolean actuallyApply = true;
		BigDecimal unitDiscount = TWO_DOLLARS;
		int cartItemQuantity = TWO;

		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, FOUR);
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount, FIVE_DOLLARS, cartItemQuantity, cartItemQuantity, applier);

		BigDecimal expectedDiscount = calculateExpectedTotalDiscountForCartItem(unitDiscount, cartItemQuantity);
		Assert.assertEquals("Expected discount does not match discount * cart item quantity", expectedDiscount,
				applier.getTotalDiscount());
	}

	/**
	 * Test case for calculating discount to 3 items without limit.
	 */
	@Test
	public void ensureCorrectDiscountAppliesToMultipleItems() {
		boolean actuallyApply = true;
		BigDecimal unitPrice = FIVE_DOLLARS;
		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, NO_MAX_ITEMS);

		BigDecimal unitDiscount1 = TWO_DOLLARS;
		int numberOfUnitsToDiscount1 = TWO;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount1, unitPrice, TWO, numberOfUnitsToDiscount1, applier);
		BigDecimal expectedDiscount1 = calculateExpectedTotalDiscountForCartItem(unitDiscount1, numberOfUnitsToDiscount1);

		BigDecimal unitDiscount2 = THREE_DOLLARS;
		int numberOfUnitsToDiscount2 = FIVE;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount2, unitPrice, FIVE, numberOfUnitsToDiscount2, applier);
		BigDecimal expectedDiscount2 = calculateExpectedTotalDiscountForCartItem(unitDiscount2, numberOfUnitsToDiscount2);

		BigDecimal unitDiscount3 = THREE_DOLLARS;
		int numberOfUnitsToDiscount3 = TWO;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount3, unitPrice, TWO, numberOfUnitsToDiscount3, applier);
		BigDecimal expectedDiscount3 = calculateExpectedTotalDiscountForCartItem(unitDiscount3, numberOfUnitsToDiscount3);

		BigDecimal totalDiscount = expectedDiscount1.add(expectedDiscount2).add(expectedDiscount3);
		Assert.assertEquals("Returned total discount does not match expected discount.", totalDiscount,
				applier.getTotalDiscount());
	}

	/**
	 * Test case for calculating discount to 3 item with quantity of 1st 2 items > limit.
	 */
	@Test
	public void ensureOnlyFirstFourQuantityIsDiscountedWhenQuantityOfMultipleItemsExceedsLimit() {
		boolean actuallyApply = true;
		BigDecimal unitPrice = FIVE_DOLLARS;
		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, FOUR);

		BigDecimal unitDiscount1 = TWO_DOLLARS;
		int numberOfUnitsToDiscount1 = TWO;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount1, unitPrice, TWO, numberOfUnitsToDiscount1, applier);
		BigDecimal expectedDiscount1 = calculateExpectedTotalDiscountForCartItem(unitDiscount1, numberOfUnitsToDiscount1);

		BigDecimal unitDiscount2 = THREE_DOLLARS;
		int numberOfUnitsToDiscount2 = TWO;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount2, unitPrice, FIVE, numberOfUnitsToDiscount2, applier);
		BigDecimal expectedDiscount2 =  calculateExpectedTotalDiscountForCartItem(unitDiscount2, numberOfUnitsToDiscount2);

		BigDecimal unitDiscount3 = THREE_DOLLARS;
		int numberOfUnitsToDiscount3 = 0;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount3, unitPrice, TWO, numberOfUnitsToDiscount3, applier);
		BigDecimal expectedDiscount3 = calculateExpectedTotalDiscountForCartItem(unitDiscount3, numberOfUnitsToDiscount3);

		BigDecimal totalDiscount = expectedDiscount1.add(expectedDiscount2).add(expectedDiscount3);
		Assert.assertEquals("Returned total discount does not match expected discount.", totalDiscount,
				applier.getTotalDiscount());
	}

	/**
	 * Test case for calculating discount to 3 item with quantity of 3 items < limit.
	 */
	@Test
	public void ensureDiscountAppliesToAllItemsWhenQuantityIsLessThanLimit() {
		boolean actuallyApply = true;
		BigDecimal unitPrice = FIVE_DOLLARS;
		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, ELEVEN);

		BigDecimal unitDiscount1 = TWO_DOLLARS;
		int numberOfUnitsToDiscount1 = TWO;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount1, unitPrice, TWO, numberOfUnitsToDiscount1, applier);
		BigDecimal expectedDiscount1 = calculateExpectedTotalDiscountForCartItem(unitDiscount1, numberOfUnitsToDiscount1);

		BigDecimal unitDiscount2 = THREE_DOLLARS;
		int numberOfUnitsToDiscount2 = FIVE;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount2, unitPrice, FIVE, numberOfUnitsToDiscount2, applier);
		BigDecimal expectedDiscount2 =  calculateExpectedTotalDiscountForCartItem(unitDiscount2, numberOfUnitsToDiscount2);

		BigDecimal unitDiscount3 = THREE_DOLLARS;
		int numberOfUnitsToDiscount3 = TWO;
		createCartItemAndApplyDiscount(actuallyApply, unitDiscount3, unitPrice, TWO, numberOfUnitsToDiscount3, applier);
		BigDecimal expectedDiscount3 = calculateExpectedTotalDiscountForCartItem(unitDiscount3, numberOfUnitsToDiscount3);

		BigDecimal totalDiscount = expectedDiscount1.add(expectedDiscount2).add(expectedDiscount3);
		Assert.assertEquals("Returned total discount does not match expected discount.", totalDiscount,
				applier.getTotalDiscount());
	}

	/**
	 * Test case for ensuring discount isn't carried over to extra quantities when discount > unit price.
	 */
	@Test
	public void ensureDiscountDoesNotExceedPrice() {
		boolean actuallyApply = true;
		LimitedTotallingApplierImpl applier = initializeApplier(actuallyApply, NO_MAX_ITEMS);

		BigDecimal unitPrice = ONE_DOLLAR;
		int numberOfUnitsToDiscount = FIVE;
		createCartItemAndApplyDiscount(actuallyApply, TWO_DOLLARS, unitPrice, FIVE, numberOfUnitsToDiscount, applier);

		BigDecimal expectedDiscount = calculateExpectedTotalDiscountForCartItem(unitPrice, numberOfUnitsToDiscount);
		Assert.assertEquals("Expected discount does not match cart price * max discount item quantity", expectedDiscount,
				applier.getTotalDiscount());
	}

	private void createCartItemAndApplyDiscount(final boolean actuallyApply, final BigDecimal unitDiscount, final BigDecimal unitPrice,
			final int cartItemQuantity, final int numberOfUnitsToDiscount, final LimitedTotallingApplierImpl applier) {
		ShoppingItem cartItem = createCartItemAndDefineExpectations(actuallyApply, cartItemQuantity,
				unitDiscount, unitPrice, numberOfUnitsToDiscount);
		applier.apply(cartItem, unitDiscount);
	}

	private ShoppingItem createCartItemAndDefineExpectations(final boolean actuallyApply, final int quantity,
			final BigDecimal unitDiscount, final BigDecimal unitPrice, final int expectedQuantityToApplyDiscount) {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));

		final BigDecimal discount = unitDiscount.min(unitPrice);
		final BigDecimal totalDiscount = calculateExpectedTotalDiscountForCartItem(discount, expectedQuantityToApplyDiscount);

		context.checking(new Expectations() {
			{
				allowing(discountItemContainer).getPrePromotionUnitPriceAmount(cartItem); will(returnValue(unitPrice));

				if (actuallyApply) {
					oneOf(cartItem).applyDiscount(totalDiscount, productSkuLookup);
					oneOf(discountItemContainer).recordRuleApplied(1, 0L, cartItem, totalDiscount, expectedQuantityToApplyDiscount);
				} else {
					never(cartItem).applyDiscount(totalDiscount, productSkuLookup);
				}
				oneOf(cartItem).getQuantity();
				will(returnValue(quantity));
			}
		});
		return cartItem;
	}

	private BigDecimal calculateExpectedTotalDiscountForCartItem(final BigDecimal expectedUnitDiscount, final int expectedQuantityToApplyDiscount) {
		return expectedUnitDiscount.multiply(BigDecimal.valueOf(expectedQuantityToApplyDiscount));
	}

	private LimitedTotallingApplierImpl initializeApplier(final boolean actuallyApply, final int maxUnitsToDiscount) {
		LimitedTotallingApplierImpl applier = new LimitedTotallingApplierImpl();
		applier.setProductSkuLookup(productSkuLookup);
		applier.initializeMaxItems(maxUnitsToDiscount);
		applier.setActuallyApply(actuallyApply);
		applier.setDiscountItemContainer(discountItemContainer);
		applier.setRuleId(1);
		return applier;
	}
}
