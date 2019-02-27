/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.DefaultCartIdentifier;
import com.elasticpath.rest.definition.carts.DefaultCartResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read default cart.
 */
public class ReadDefaultCartPrototype implements DefaultCartResource.Read {

	private final DefaultCartIdentifier defaultCartIdentifier;

	private final AliasRepository<DefaultCartIdentifier, CartIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param defaultCartIdentifier	defaultCartIdentifier
	 * @param repository			repository
	 */
	@Inject
	public ReadDefaultCartPrototype(@RequestIdentifier final DefaultCartIdentifier defaultCartIdentifier,
									@ResourceRepository final AliasRepository<DefaultCartIdentifier, CartIdentifier> repository) {
		this.defaultCartIdentifier = defaultCartIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<CartIdentifier> onRead() {
		return repository.resolve(defaultCartIdentifier);
	}
}