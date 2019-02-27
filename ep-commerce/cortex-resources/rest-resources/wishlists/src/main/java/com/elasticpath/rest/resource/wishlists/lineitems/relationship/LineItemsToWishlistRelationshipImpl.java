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
 * Line items to favorites link.
 */
public class LineItemsToWishlistRelationshipImpl implements WishlistLineItemsForWishlistRelationship.LinkFrom {

	private final WishlistLineItemsIdentifier wishlistLineItemsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemsIdentifier line items identifier
	 */
	@Inject
	public LineItemsToWishlistRelationshipImpl(@RequestIdentifier final WishlistLineItemsIdentifier wishlistLineItemsIdentifier) {
		this.wishlistLineItemsIdentifier = wishlistLineItemsIdentifier;
	}

	@Override
	public Observable<WishlistIdentifier> onLinkFrom() {
		return Observable.just(wishlistLineItemsIdentifier.getWishlist());
	}
}
