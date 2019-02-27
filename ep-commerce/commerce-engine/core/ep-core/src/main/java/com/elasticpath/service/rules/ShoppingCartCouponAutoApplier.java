/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.rules;

import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Adapter interface to filter and auto apply coupons to a shopping cart.
 */
public interface ShoppingCartCouponAutoApplier {

	/**
	 * Filter existing coupons on shopping cart and auto apply new coupons.
	 *
	 * @param shoppingCart shopping cart.
	 * @return true if shopping cart is updated, false otherwise.
	 */
	boolean filterAndAutoApplyCoupons(ShoppingCart shoppingCart);
}
