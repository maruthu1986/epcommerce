/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemEntity;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read line item.
 */
public class ReadLineItemPrototype implements WishlistLineItemResource.Read {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	private final Repository<WishlistLineItemEntity, WishlistLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemIdentifier wishlistLineItemIdentifier
	 * @param repository                 repository
	 */
	@Inject
	public ReadLineItemPrototype(@RequestIdentifier final WishlistLineItemIdentifier wishlistLineItemIdentifier,
								 @ResourceRepository final Repository<WishlistLineItemEntity, WishlistLineItemIdentifier> repository) {
		this.wishlistLineItemIdentifier = wishlistLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<WishlistLineItemEntity> onRead() {
		return repository.findOne(wishlistLineItemIdentifier);
	}
}
