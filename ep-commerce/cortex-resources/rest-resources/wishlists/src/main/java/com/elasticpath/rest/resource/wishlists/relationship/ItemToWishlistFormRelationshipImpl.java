/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.AddItemToWishlistFormIdentifier;
import com.elasticpath.rest.definition.wishlists.ItemToWishlistFormRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Add item to favorites form link.
 */
public class ItemToWishlistFormRelationshipImpl implements ItemToWishlistFormRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier itemIdentifier
	 */
	@Inject
	public ItemToWishlistFormRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Observable<AddItemToWishlistFormIdentifier> onLinkTo() {
		return Observable.just(AddItemToWishlistFormIdentifier.builder()
				.withItem(itemIdentifier)
				.build());
	}
}
