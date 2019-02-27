/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.prototypes;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Completable;

import javax.inject.Inject;

/**
 * Delete line items.
 */
public class DeleteLineItemsPrototype implements FavoriteLineItemsResource.Delete {

	private final FavoriteLineItemsIdentifier favoriteLineItemsIdentifier;
	private final LinksRepository<FavoriteIdentifier, FavoriteLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param favoriteLineItemsIdentifier line items identifier
	 * @param repository         repository
	 */
	@Inject
	public DeleteLineItemsPrototype(@RequestIdentifier final FavoriteLineItemsIdentifier favoriteLineItemsIdentifier,
									@ResourceRepository final LinksRepository<FavoriteIdentifier, FavoriteLineItemIdentifier> repository) {
		this.favoriteLineItemsIdentifier = favoriteLineItemsIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onDelete() {
		return repository.deleteAll(favoriteLineItemsIdentifier.getFavorite());
	}
}
