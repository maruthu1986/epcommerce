/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.cartorder;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * This Class can perform services for CartOrders related to coupons, 
 * CartOrder should not be used in versions of EP prior to 6.4.
 */
public interface CartOrderCouponService {

	/**
	 * Populate the coupon codes on the shopping cart.
	 *
	 * @param shoppingCart shopping cart
	 * @param cartOrder cart order
	 * @return enriched shopping cart
	 */
	ShoppingCart populateCouponCodesOnShoppingCart(ShoppingCart shoppingCart, CartOrder cartOrder);

}
