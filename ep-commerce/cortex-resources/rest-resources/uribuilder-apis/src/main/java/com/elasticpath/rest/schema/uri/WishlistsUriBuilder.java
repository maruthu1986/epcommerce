/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to wishlists.
 */
public interface WishlistsUriBuilder extends ScopedUriBuilder<WishlistsUriBuilder> {
	/**
	 * Set the favorites ID.
	 *
	 * @param wishlistId item id
	 * @return the builder
	 */
	WishlistsUriBuilder setWishlistId(String wishlistId);

	/**
	 * Set the form uri.
	 *
	 * @param formUri form uri.
	 * @return the builder
	 */
	WishlistsUriBuilder setFormUri(String formUri);

	/**
	 * Set the item uri.
	 *
	 * @param itemUri item uri
	 * @return the builder
	 */
	WishlistsUriBuilder setItemUri(String itemUri);
}
