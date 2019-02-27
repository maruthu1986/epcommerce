/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.shoppingcart.impl;

import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * The result of adding an item to favorites.
 */
public final class AddToWishlistResult {

	private final ShoppingItem shoppingItem;

	private final boolean newlyCreated;

	/**
	 * Constructor.
	 *
	 * @param shoppingItem   the shopping item
	 * @param newlyCreated <code>true</code> if this is a new shopping item, false otherwise.
	 */
	public AddToWishlistResult(final ShoppingItem shoppingItem, final boolean newlyCreated) {
		this.shoppingItem = shoppingItem;
		this.newlyCreated = newlyCreated;
	}

	/**
	 * The shopping item added to favorites. It could be a newly created item, or it could be an existing item.
	 *
	 * @return the shopping item.
	 */
	public ShoppingItem getShoppingItem() {
		return shoppingItem;
	}

	/**
	 * Returns <code>true</code> if the add operation resulted in creation of a new item, <code>false</code> otherwise.
	 *
	 * @return boolean value indicating if a new shopping item was created.
	 */
	public boolean isNewlyCreated() {
		return newlyCreated;
	}
}
