/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.ReadWishlistMembershipsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistMembershipFromItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Item to favorites memberships link.
 */
public class WishlistMembershipFromItemRelationshipImpl implements WishlistMembershipFromItemRelationship.LinkTo {

	private final ItemIdentifier itemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param itemIdentifier itemIdentifier
	 */
	@Inject
	public WishlistMembershipFromItemRelationshipImpl(@RequestIdentifier final ItemIdentifier itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	@Override
	public Observable<ReadWishlistMembershipsIdentifier> onLinkTo() {
		return Observable.just(ReadWishlistMembershipsIdentifier.builder()
				.withItem(itemIdentifier)
				.build());
	}
}
