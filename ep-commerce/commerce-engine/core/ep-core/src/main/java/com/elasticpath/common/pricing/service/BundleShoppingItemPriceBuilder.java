/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.common.pricing.service;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;

/**
 *  A strategy interface to build bundle shopping item price.
 *  There are two different implementations now: one for assigned bundle, the other for calculated bundle.
 */
public interface BundleShoppingItemPriceBuilder {

	/**
	 * Builds the <code>Price</code> object for the bundle shopping item.
	 * The result price is promoted and adjusted.
	 *
	 * @param bundleShoppingItem ShoppingItem.
	 * @param shopper CustomerSession.
	 * @param store the store
	 * @return Price.
	 */
	Price build(ShoppingItem bundleShoppingItem, Shopper shopper, Store store);

}