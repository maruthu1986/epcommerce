/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.totals.CartTotalIdentifier;
import com.elasticpath.rest.definition.totals.CartTotalResource;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Cart Total prototype for Read operation.
 */
public class ReadCartTotalPrototype implements CartTotalResource.Read {

	private final CartTotalIdentifier cartTotalIdentifier;

	private final Repository<TotalEntity, CartTotalIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param cartTotalIdentifier cart total identifier
	 * @param repository          repository
	 */
	@Inject
	public ReadCartTotalPrototype(@RequestIdentifier final CartTotalIdentifier cartTotalIdentifier,
								  @ResourceRepository final Repository<TotalEntity, CartTotalIdentifier> repository) {
		this.cartTotalIdentifier = cartTotalIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TotalEntity> onRead() {
		return repository.findOne(cartTotalIdentifier);
	}
}
