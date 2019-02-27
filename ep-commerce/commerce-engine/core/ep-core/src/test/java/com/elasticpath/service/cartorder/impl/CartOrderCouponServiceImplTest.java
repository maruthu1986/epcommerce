/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.cartorder.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
/**
 * Test for {@link CartOrderCouponServiceImpl}.
 */
public class CartOrderCouponServiceImplTest {
	private static final String NEW_COUPON = "NEW_COUPON";
	private static final String EXISTING_COUPON = "EXISTING_COUPON";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final CartOrderCouponServiceImpl cartOrderCouponService = new CartOrderCouponServiceImpl();
	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);;
	private final CartOrder cartOrder = context.mock(CartOrder.class);
	
	private final Set<String> cartOrderCoupons = new HashSet<>(Arrays.asList(NEW_COUPON, EXISTING_COUPON));
	
	@Before
	public void setUp() {
		context.checking(new Expectations() {
			{
				allowing(cartOrder).getCouponCodes();
				will(returnValue(cartOrderCoupons));

				allowing(shoppingCart).applyPromotionCodes(cartOrderCoupons);
			}
		});
	}
	
	@Test
	public void testAllCouponsFromCartOrderAreAddedToShoppingCart() {

		cartOrderCouponService.populateCouponCodesOnShoppingCart(shoppingCart, cartOrder);
	}
}
