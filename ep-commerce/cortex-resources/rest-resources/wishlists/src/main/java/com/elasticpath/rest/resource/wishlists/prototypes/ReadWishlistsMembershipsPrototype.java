/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.ReadWishlistMembershipsIdentifier;
import com.elasticpath.rest.definition.wishlists.ReadWishlistMembershipsResource;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Wishlist memberships for an item.
 */
public class ReadWishlistsMembershipsPrototype implements ReadWishlistMembershipsResource.Read {

	private final ItemIdentifier itemIdentifier;

	private final LinksRepository<ItemIdentifier, WishlistIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param wishlistMembershipsIdentifier wishlistMembershipsIdentifier
	 * @param repository                    repository
	 */
	@Inject
	public ReadWishlistsMembershipsPrototype(@RequestIdentifier final ReadWishlistMembershipsIdentifier wishlistMembershipsIdentifier,
											 @ResourceRepository final LinksRepository<ItemIdentifier, WishlistIdentifier> repository) {
		this.itemIdentifier = wishlistMembershipsIdentifier.getItem();
		this.repository = repository;
	}

	@Override
	public Observable<WishlistIdentifier> onRead() {
		return repository.getElements(itemIdentifier);
	}
}
