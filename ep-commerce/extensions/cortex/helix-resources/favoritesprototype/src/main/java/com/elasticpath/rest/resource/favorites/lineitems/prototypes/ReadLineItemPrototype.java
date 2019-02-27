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
import io.reactivex.Single;

import javax.inject.Inject;

/**
 * Read line item.
 */
public class ReadLineItemPrototype implements FavoriteLineItemResource.Read {

	private final FavoriteLineItemIdentifier favoriteLineItemIdentifier;

	private final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param favoriteLineItemIdentifier favoriteLineItemIdentifier
	 * @param repository                 repository
	 */
	@Inject
	public ReadLineItemPrototype(@RequestIdentifier final FavoriteLineItemIdentifier favoriteLineItemIdentifier,
								 @ResourceRepository final Repository<FavoriteLineItemEntity, FavoriteLineItemIdentifier> repository) {
		this.favoriteLineItemIdentifier = favoriteLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<FavoriteLineItemEntity> onRead() {
		return repository.findOne(favoriteLineItemIdentifier);
	}
}
