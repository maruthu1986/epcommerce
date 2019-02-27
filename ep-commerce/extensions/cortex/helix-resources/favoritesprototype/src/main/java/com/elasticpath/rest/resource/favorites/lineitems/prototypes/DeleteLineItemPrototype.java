/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.lineitems.prototypes;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemEntity;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Completable;

import javax.inject.Inject;

/**
 * Delete line item.
 */
public class DeleteLineItemPrototype implements FavoriteLineItemResource.Delete {

	private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

	private final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param favoriteLineItemIdentifier favoriteLineItemIdentifier
	 * @param repository                 repository
	 */
	@Inject
	public DeleteLineItemPrototype(@RequestIdentifier final FavoriteLineItemIdentifier favoriteLineItemIdentifier,
								   @ResourceRepository final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository) {
		this.favoriteLineItemIdentifier = favoriteLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onDelete() {
		return repository.delete(favoriteLineItemIdentifier);
	}
}
