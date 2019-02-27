/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

/**
 * Implements {@link ShoppingCartValidationContext}.
 */
public class ShoppingCartValidationContextImpl implements ShoppingCartValidationContext {

	private ShoppingCart shoppingCart;

	private CartOrder cartOrder;

	@Override
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	@Override
	public void setShoppingCart(final ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	@Override
	public CartOrder getCartOrder() {
		return cartOrder;
	}

	@Override
	public void setCartOrder(final CartOrder cartOrder) {
		this.cartOrder = cartOrder;
	}
}
