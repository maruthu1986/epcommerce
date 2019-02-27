/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Validation Context for Shopping Cart.
 */
public interface ShoppingCartValidationContext {

	/**
	 * Getter for shopping cart.
	 * @return the shopping cart.
	 */
	ShoppingCart getShoppingCart();

	/**
	 * Setter for shopping cart.
	 *
	 * @param shoppingCart the shopping cart.
	 */
	void setShoppingCart(ShoppingCart shoppingCart);

	/**
	 * Getter for cartOrder.
	 * @return the cart order
	 */
	CartOrder getCartOrder();

	/**
	 * Setter for car order.
	 * @param cartOrder the cart order
	 */
	void setCartOrder(CartOrder cartOrder);
}
