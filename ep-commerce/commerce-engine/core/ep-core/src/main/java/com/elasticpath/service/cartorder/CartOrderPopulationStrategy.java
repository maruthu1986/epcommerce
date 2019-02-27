/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.cartorder;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * The Interface CartOrderPopulationStrategy.
 * It is used by <code>CartOrderService</code> to decide how to fill the billing address GUID and payment method GUID
 * when a <code>CartOrder</code> is created.
 */
public interface CartOrderPopulationStrategy {

	/**
	 * Creates the cart order.
	 *
	 * @param shoppingCart the shopping caty
	 * @return the cart order
	 */
	CartOrder createCartOrder(ShoppingCart shoppingCart);
}
