/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.wishlists.MoveToCartFormIdentifier;
import com.elasticpath.rest.definition.wishlists.MoveToCartFormResource;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemEntity;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Move to cart form.
 */
public class ReadMoveToCartFormPrototype implements MoveToCartFormResource.Read {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier;

	private final Repository<WishlistLineItemEntity, WishlistLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param moveToCartFormIdentifier moveToCartFormIdentifier
	 * @param repository               repository
	 */
	@Inject
	public ReadMoveToCartFormPrototype(@RequestIdentifier final MoveToCartFormIdentifier moveToCartFormIdentifier,
									   @ResourceRepository final Repository<WishlistLineItemEntity, WishlistLineItemIdentifier> repository) {
		this.wishlistLineItemIdentifier = moveToCartFormIdentifier.getWishlistLineItem();
		this.repository = repository;
	}

	@Override
	public Single<LineItemEntity> onRead() {
		return repository.findOne(wishlistLineItemIdentifier)
				.map(entity -> LineItemEntity.builder()
						.withQuantity(0)
						.withConfiguration(entity.getConfiguration())
						.build());
	}
}
