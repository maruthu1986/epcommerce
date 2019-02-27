/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read line items.
 */
public class ReadLineItemsPrototype implements WishlistLineItemsResource.Read {

	private final LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> repository;
	private final WishlistLineItemsIdentifier wishlistLineItemsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param wishlistLineItemsIdentifier line items identifier
	 * @param repository         repository
	 */
	@Inject
	public ReadLineItemsPrototype(@RequestIdentifier final WishlistLineItemsIdentifier wishlistLineItemsIdentifier,
								  @ResourceRepository final LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> repository) {
		this.wishlistLineItemsIdentifier = wishlistLineItemsIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<WishlistLineItemIdentifier> onRead() {
		return repository.getElements(wishlistLineItemsIdentifier.getWishlist());
	}
}
