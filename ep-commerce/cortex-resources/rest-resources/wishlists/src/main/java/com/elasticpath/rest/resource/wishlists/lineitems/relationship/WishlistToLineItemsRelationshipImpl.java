/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsForWishlistRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Wishlist to line items link.
 */
public class WishlistToLineItemsRelationshipImpl implements WishlistLineItemsForWishlistRelationship.LinkTo {

	private final WishlistIdentifier wishlistIdentifier;

	/**
	 * Constructor.
	 *
	 * @param wishlistIdentifier wishlistIdentifier
	 */
	@Inject
	public WishlistToLineItemsRelationshipImpl(@RequestIdentifier final WishlistIdentifier wishlistIdentifier) {
		this.wishlistIdentifier = wishlistIdentifier;
	}

	@Override
	public Observable<WishlistLineItemsIdentifier> onLinkTo() {
		return Observable.just(WishlistLineItemsIdentifier.builder()
				.withWishlist(wishlistIdentifier)
				.build());
	}
}
