/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.prototypes;


import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.CartResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Carts prototype for read operations.
 */
public class ReadCartPrototype implements CartResource.Read {

	private final CartIdentifier cartIdentifier;

	private final Repository<CartEntity, CartIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param cartIdentifier carIdentifier
	 * @param repository     repository
	 */
	@Inject
	public ReadCartPrototype(@RequestIdentifier final CartIdentifier cartIdentifier,
							 @ResourceRepository final Repository<CartEntity, CartIdentifier> repository) {
		this.cartIdentifier = cartIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<CartEntity> onRead() {
		return repository.findOne(cartIdentifier);
	}
}
