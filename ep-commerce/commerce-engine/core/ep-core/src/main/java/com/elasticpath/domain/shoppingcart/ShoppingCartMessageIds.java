/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.shoppingcart;

/**
 * Collection of StructuredErrorMessage message ids for the cart domain.
 * These message ids should be used for localization of error messages on client side.
 */
public final class ShoppingCartMessageIds {
	/**
	 * Item is not available for purchase and cannot be added to the cart.
	 */
	public static final String ITEM_NOT_AVAILABLE = "cart.item.not.available";

	private ShoppingCartMessageIds() {
		// private constructor to ensure class can't be instantiated
	}
}
