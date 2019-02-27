/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Context for validating Shopping Items.
 */
public interface ShoppingItemValidationContext extends ProductSkuValidationContext {

	/**
	 * Getter for shopping cart.
	 * @return the shopping cart
	 */
	ShoppingCart getShoppingCart();

	/**
	 * Setter for shopping cart.
	 * @param shoppingCart the shopping cart
	 */
	void setShoppingCart(ShoppingCart shoppingCart);

	/**
	 * Getter for shopping item.
	 * @return the shopping item.
	 */
	ShoppingItem getShoppingItem();

	/**
	 * Setter for shopping item.
	 * @param shoppingItem the shopping item.
	 */
	void setShoppingItem(ShoppingItem shoppingItem);
}
