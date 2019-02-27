/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Refreshes shopping cart.
 */
public interface ShoppingCartRefresher {

	/**
	 * Refreshes the given shopping cart. For example, updating all its {@link com.elasticpath.domain.shoppingcart.ShoppingItem ShoppingItems} with
	 * the latest prices.
	 *
	 * @param shoppingCart the shopping cart to refresh
	 */
	void refresh(ShoppingCart shoppingCart);

}
