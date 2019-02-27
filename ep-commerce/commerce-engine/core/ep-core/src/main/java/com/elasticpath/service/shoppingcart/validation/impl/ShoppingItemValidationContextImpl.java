/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidationContext;

/**
 * Implements {@link ShoppingItemValidationContext}.
 */
public class ShoppingItemValidationContextImpl
		extends ProductSkuValidationContextImpl
		implements ShoppingItemValidationContext {

	private ShoppingCart shoppingCart;

	private ShoppingItem shoppingItem;

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(final ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	@Override
	public ShoppingItem getShoppingItem() {
		return shoppingItem;
	}

	@Override
	public void setShoppingItem(final ShoppingItem shoppingItem) {
		this.shoppingItem = shoppingItem;
	}
}
