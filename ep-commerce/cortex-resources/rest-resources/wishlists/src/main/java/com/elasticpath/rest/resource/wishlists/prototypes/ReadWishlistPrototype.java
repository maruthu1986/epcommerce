/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.wishlists.WishlistEntity;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read favorites.
 */
public class ReadWishlistPrototype implements WishlistResource.Read {

	private final WishlistIdentifier wishlistIdentifier;

	private final Repository<WishlistEntity, WishlistIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param wishlistIdentifier wishlistIdentifier
	 * @param repository         repository
	 */
	@Inject
	public ReadWishlistPrototype(@RequestIdentifier final WishlistIdentifier wishlistIdentifier,
								 @ResourceRepository final Repository<WishlistEntity, WishlistIdentifier> repository) {
		this.wishlistIdentifier = wishlistIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<WishlistEntity> onRead() {
		return repository.findOne(wishlistIdentifier);
	}
}
