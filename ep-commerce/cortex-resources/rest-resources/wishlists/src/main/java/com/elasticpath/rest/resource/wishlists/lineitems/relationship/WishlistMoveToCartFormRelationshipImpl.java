/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.wishlists.MoveToCartFormIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistMoveToCartFormRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Move to cart form link.
 */
public class WishlistMoveToCartFormRelationshipImpl implements WishlistMoveToCartFormRelationship.LinkTo {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemIdentifier wishlistLineItemIdentifier
	 */
	@Inject
	public WishlistMoveToCartFormRelationshipImpl(@RequestIdentifier final WishlistLineItemIdentifier wishlistLineItemIdentifier) {
		this.wishlistLineItemIdentifier = wishlistLineItemIdentifier;
	}

	@Override
	public Observable<MoveToCartFormIdentifier> onLinkTo() {
		return Observable.just(MoveToCartFormIdentifier.builder()
				.withWishlistLineItem(wishlistLineItemIdentifier)
				.build());
	}
}
