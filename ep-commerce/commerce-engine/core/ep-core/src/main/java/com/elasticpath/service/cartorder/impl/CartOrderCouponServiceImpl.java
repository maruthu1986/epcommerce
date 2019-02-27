/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.cartorder.impl;

import java.util.Collection;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderCouponService;
/**
 * This Class can perform services for CartOrders related to coupons, 
 * CartOrder should not be used in versions of EP prior to 6.4.
 */
public class CartOrderCouponServiceImpl implements CartOrderCouponService {

	@Override
	public ShoppingCart populateCouponCodesOnShoppingCart(final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		final Collection<String> couponCodes = cartOrder.getCouponCodes();

		shoppingCart.applyPromotionCodes(couponCodes);

		return shoppingCart;
	}

}
