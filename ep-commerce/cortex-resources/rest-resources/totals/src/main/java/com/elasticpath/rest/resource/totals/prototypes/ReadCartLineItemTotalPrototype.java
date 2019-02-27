/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.totals.CartLineItemTotalIdentifier;
import com.elasticpath.rest.definition.totals.CartLineItemTotalResource;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Cart Line Item Total prototype for Read operation.
 */
public class ReadCartLineItemTotalPrototype implements CartLineItemTotalResource.Read {

	private final CartLineItemTotalIdentifier cartLineItemTotalIdentifier;

	private final Repository<TotalEntity, CartLineItemTotalIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param cartLineItemTotalIdentifier cart total identifier
	 * @param repository                  repository
	 */
	@Inject
	public ReadCartLineItemTotalPrototype(@RequestIdentifier final CartLineItemTotalIdentifier cartLineItemTotalIdentifier,
										  @ResourceRepository final Repository<TotalEntity, CartLineItemTotalIdentifier> repository) {
		this.cartLineItemTotalIdentifier = cartLineItemTotalIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TotalEntity> onRead() {
		return repository.findOne(cartLineItemTotalIdentifier);
	}
}
