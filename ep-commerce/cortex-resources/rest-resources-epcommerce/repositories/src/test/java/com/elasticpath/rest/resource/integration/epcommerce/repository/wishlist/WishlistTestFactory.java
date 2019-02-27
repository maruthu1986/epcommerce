/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.WISHLIST_ID;

import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Factory methods for building identifiers.
 */
public final class WishlistTestFactory {

	private WishlistTestFactory() {

	}

	/**
	 * Builds a WishlistsIdentifier.
	 * @return WishlistsIdentifier
	 */
	public static WishlistsIdentifier buildWishlistsIdentifier() {
		return WishlistsIdentifier.builder()
				.withScope(SCOPE_IDENTIFIER_PART)
				.build();
	}

	/**
	 * Builds a WishlistIdentifier.
	 * @return WishlistIdentifier
	 */
	public static WishlistIdentifier buildWishlistIdentifier() {
		return WishlistIdentifier.builder()
				.withWishlistId(StringIdentifier.of(WISHLIST_ID))
				.withWishlists(buildWishlistsIdentifier())
				.build();
	}

	/**
	 * Builds a WishlistLineItemsIdentifier.
	 * @return WishlistLineItemsIdentifier
	 */
	public static WishlistLineItemsIdentifier buildWishlistLineItemsIdentifier() {
		return WishlistLineItemsIdentifier.builder()
				.withWishlist(buildWishlistIdentifier())
				.build();
	}

	/**
	 * Builds a WishlistLineItemIdentifier.
	 * @return WishlistLineItemIdentifier
	 */
	public static WishlistLineItemIdentifier buildWishlistLineItemIdentifier() {
		return WishlistLineItemIdentifier.builder()
				.withLineItemId(StringIdentifier.of(LINE_ITEM_ID))
				.withWishlistLineItems(buildWishlistLineItemsIdentifier())
				.build();
	}
}
