/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds URIs for carts.
 */
public interface CartsUriBuilder extends ScopedUriBuilder<CartsUriBuilder> {

	/**
	 * Set the cart ID.
	 *
	 * @param cartId cart ID.
	 * @return the builder
	 */
	CartsUriBuilder setCartId(String cartId);

	/**
	 * Set the URI for the thing being added to the cart.
	 *
	 * @param addUri the source uri.
	 * @return the builder
	 */
	CartsUriBuilder setFormUri(String addUri);

	/**
	 * Set the URI for the item being added to the cart.
	 *
	 * @param itemUri the item uri.
	 * @return the builder
	 */
	CartsUriBuilder setItemUri(String itemUri);
}
