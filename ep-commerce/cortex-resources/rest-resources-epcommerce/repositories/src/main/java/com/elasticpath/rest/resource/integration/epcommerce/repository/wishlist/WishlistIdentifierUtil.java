/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist;

import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Utility class for building identifiers.
 */
public final class WishlistIdentifierUtil {

	private WishlistIdentifierUtil() {
	}

	/**
	 * Builds a WishlistsIdentifier.
	 * @param scope		scope
	 * @return WishlistsIdentifier
	 */
	public static WishlistsIdentifier buildWishlistsIdentifier(final String scope) {
		return WishlistsIdentifier.builder()
				.withScope(StringIdentifier.of(scope))
				.build();
	}

	/**
	 * Builds a WishlistIdentifier.
	 * @param scope			scope
	 * @param wishlistId	wish list id
	 * @return WishlistIdentifier
	 */
	public static WishlistIdentifier buildWishlistIdentifier(final String scope, final String wishlistId) {
		return WishlistIdentifier.builder()
				.withWishlists(buildWishlistsIdentifier(scope))
				.withWishlistId(StringIdentifier.of(wishlistId))
				.build();
	}

	/**
	 * Builds a WishlistLineItemsIdentifier.
	 * @param scope			scope
	 * @param wishlistId	wish list id
	 * @return WishlistLineItemsIdentifier
	 */
	public static WishlistLineItemsIdentifier buildWishLineItemsIdentifier(final String scope, final String wishlistId) {
		return WishlistLineItemsIdentifier.builder()
				.withWishlist(buildWishlistIdentifier(scope, wishlistId))
				.build();
	}

	/**
	 * Builds a WishlistLineItemIdentifier.
	 * @param scope			scope
	 * @param wishlistId	wish list id
	 * @param itemId		item id
	 * @return WishlistLineItemIdentifier
	 */
	public static WishlistLineItemIdentifier buildWishlistLineItemIdentifier(final String scope, final String wishlistId, final String itemId) {
		return WishlistLineItemIdentifier.builder()
				.withWishlistLineItems(buildWishLineItemsIdentifier(scope, wishlistId))
				.withLineItemId(StringIdentifier.of(itemId))
				.build();
	}
}
