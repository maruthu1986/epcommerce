/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsForWishlistLineItemRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Line item to line items link.
 */
public class LineItemToLineItemsRelationshipImpl implements WishlistLineItemsForWishlistLineItemRelationship.LinkTo {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemIdentifier line item identifier
	 */
	@Inject
	public LineItemToLineItemsRelationshipImpl(@RequestIdentifier final WishlistLineItemIdentifier wishlistLineItemIdentifier) {
		this.wishlistLineItemIdentifier = wishlistLineItemIdentifier;
	}

	@Override
	public Observable<WishlistLineItemsIdentifier> onLinkTo() {
		return Observable.just(wishlistLineItemIdentifier.getWishlistLineItems());
	}

}
