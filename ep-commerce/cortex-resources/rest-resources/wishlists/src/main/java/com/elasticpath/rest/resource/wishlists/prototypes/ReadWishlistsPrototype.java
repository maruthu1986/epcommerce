/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.wishlists.WishlistEntity;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsResource;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Read wishlists.
 */
public class ReadWishlistsPrototype implements WishlistsResource.Read {

	private final IdentifierPart<String> scope;

	private final Repository<WishlistEntity, WishlistIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param scope      scope
	 * @param repository repository
	 */
	@Inject
	public ReadWishlistsPrototype(@UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope,
								  @ResourceRepository final Repository<WishlistEntity, WishlistIdentifier> repository) {
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Observable<WishlistIdentifier> onRead() {
		return repository.findAll(scope);
	}
}
