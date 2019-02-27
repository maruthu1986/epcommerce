/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.ItemForWishlistLineItemRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Line item to item link.
 */
public class LineItemToItemRelationshipImpl implements ItemForWishlistLineItemRelationship.LinkTo {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	private final LinksRepository<WishlistLineItemIdentifier, ItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemIdentifier wishlistLineItemIdentifier
	 * @param repository                 repository
	 */
	@Inject
	public LineItemToItemRelationshipImpl(@RequestIdentifier final WishlistLineItemIdentifier wishlistLineItemIdentifier,
										  @ResourceRepository final LinksRepository<WishlistLineItemIdentifier, ItemIdentifier> repository) {
		this.wishlistLineItemIdentifier = wishlistLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<ItemIdentifier> onLinkTo() {
		return repository.getElements(wishlistLineItemIdentifier);
	}
}
