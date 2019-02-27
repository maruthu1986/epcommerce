/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Completable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Delete line items.
 */
public class DeleteLineItemsPrototype implements WishlistLineItemsResource.Delete {

	private final WishlistLineItemsIdentifier wishlistLineItemsIdentifier;
	private final LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemsIdentifier line items identifier
	 * @param repository         repository
	 */
	@Inject
	public DeleteLineItemsPrototype(@RequestIdentifier final WishlistLineItemsIdentifier wishlistLineItemsIdentifier,
									@ResourceRepository final LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> repository) {
		this.wishlistLineItemsIdentifier = wishlistLineItemsIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onDelete() {
		return repository.deleteAll(wishlistLineItemsIdentifier.getWishlist());
	}
}
