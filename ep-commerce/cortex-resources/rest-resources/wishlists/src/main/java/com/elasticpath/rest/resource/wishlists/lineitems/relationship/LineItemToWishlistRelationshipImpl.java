/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.wishlists.WishlistForWishlistLineItemRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Line item to favorites link.
 */
public class LineItemToWishlistRelationshipImpl implements WishlistForWishlistLineItemRelationship.LinkTo {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemIdentifier line item identifier
	 */
	@Inject
	public LineItemToWishlistRelationshipImpl(@RequestIdentifier final WishlistLineItemIdentifier wishlistLineItemIdentifier) {
		this.wishlistLineItemIdentifier = wishlistLineItemIdentifier;
	}

	@Override
	public Observable<WishlistIdentifier> onLinkTo() {
		return Observable.just(wishlistLineItemIdentifier.getWishlistLineItems().getWishlist());
	}
}
