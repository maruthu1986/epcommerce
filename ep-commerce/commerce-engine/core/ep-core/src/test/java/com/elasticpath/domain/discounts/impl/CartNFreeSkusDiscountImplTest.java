/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.discounts.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>CartNFreeSkusDiscountImpl</code>.
 */
public class CartNFreeSkusDiscountImplTest {

	private static final long RULE_ID = 123L;
	private static final long ACTION_ID = 456L;

	private static final String CART_ITEM_PRODUCT_CODE = "product1";

	private static int cartItemCount;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private TotallingApplier totallingApplier;

	private DiscountItemContainer container;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		totallingApplier = context.mock(TotallingApplier.class);
		container = context.mock(DiscountItemContainer.class);
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.TOTALLING_APPLIER);
				will(returnValue(totallingApplier));

				oneOf(totallingApplier).setDiscountItemContainer(container);
				oneOf(totallingApplier).setRuleId(RULE_ID);
				oneOf(totallingApplier).setActionId(ACTION_ID);

				ignoring(container).getCatalog();
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private CartNFreeSkusDiscountImpl createCartNFreeSkusDiscountImpl(final String ruleElementType,
			final long ruleId, final String skuCode,
			final int numSkus) {
		return new CartNFreeSkusDiscountImpl(ruleElementType, ruleId, ACTION_ID, skuCode, numSkus);
	}

	/**
	 * Test case for doApply() with 1 item.
	 */
	@Test
	public void testApplyOnSingleItem() {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final int maxItems = 1;
		final int numSkus = 1;
		final BigDecimal itemPrice = BigDecimal.TEN;
		final BigDecimal totalDiscountAmount = BigDecimal.TEN;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).setActuallyApply(true);
				oneOf(totallingApplier).initializeMaxItems(maxItems);
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem, itemPrice);

				oneOf(container).getLowestPricedShoppingItemForSku(CART_ITEM_PRODUCT_CODE);
				will(returnValue(Optional.empty()));

				oneOf(container).getPriceAmount(cartItem);
				will(returnValue(itemPrice));
				oneOf(container).addCartItem(CART_ITEM_PRODUCT_CODE, numSkus);
				will(returnValue(cartItem));
			}
		});

		CartNFreeSkusDiscountImpl discount = createCartNFreeSkusDiscountImpl("", RULE_ID, CART_ITEM_PRODUCT_CODE, numSkus);
		BigDecimal total = discount.apply(container);
		Assert.assertEquals("Amount does not matched expected value for cart with 1 item", totalDiscountAmount, total);
	}

	/**
	 * Test case for doApply() with 2 items.
	 */
	@Test
	public void testCalculateOnSingleItem() {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class, String.valueOf(cartItemCount++));
		final int maxItems = 1;
		final int numSkus = 1;
		final BigDecimal itemPrice = BigDecimal.TEN;
		final BigDecimal totalDiscountAmount = BigDecimal.TEN;
		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).setActuallyApply(false);
				oneOf(totallingApplier).initializeMaxItems(maxItems);
				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(totalDiscountAmount));
				oneOf(totallingApplier).apply(cartItem, itemPrice);

				oneOf(container).getLowestPricedShoppingItemForSku(CART_ITEM_PRODUCT_CODE);
				will(returnValue(Optional.empty()));

				oneOf(container).getPriceAmount(cartItem);
				will(returnValue(itemPrice));
				oneOf(container).addCartItem(CART_ITEM_PRODUCT_CODE, numSkus);
				will(returnValue(cartItem));
			}
		});

		CartNFreeSkusDiscountImpl discount = createCartNFreeSkusDiscountImpl("", RULE_ID, CART_ITEM_PRODUCT_CODE, numSkus);
		BigDecimal total = discount.calculate(container);
		Assert.assertEquals(totalDiscountAmount, total);
	}

	@Test
	public void verifyExistingCartItemsAreMadeFreeButNotAddedAgain() {
		final String skuCode = "MYSKU001";
		final BigDecimal itemPrice = BigDecimal.TEN;
		final int numberOfItems = 1;
		final ShoppingItem matchingShoppingItem = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(totallingApplier).setActuallyApply(true);
				oneOf(totallingApplier).initializeMaxItems(numberOfItems);

				oneOf(container).getLowestPricedShoppingItemForSku(skuCode);
				will(returnValue(Optional.of(matchingShoppingItem)));

				oneOf(container).getPriceAmount(matchingShoppingItem);
				will(returnValue(itemPrice));

				never(container).addCartItem(CART_ITEM_PRODUCT_CODE, numberOfItems);

				oneOf(totallingApplier).apply(matchingShoppingItem, itemPrice);

				oneOf(totallingApplier).getTotalDiscount();
				will(returnValue(itemPrice));
			}
		});

		final CartNFreeSkusDiscountImpl discount =
				new CartNFreeSkusDiscountImpl("", RULE_ID, ACTION_ID, skuCode, numberOfItems);

		final BigDecimal applied = discount.apply(container);

		assertThat(applied)
				.isEqualTo(itemPrice);
	}

}
