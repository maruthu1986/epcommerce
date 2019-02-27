/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.prices.PriceForItemdefinitionIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemdefinitionResource;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements retrieving the price of an itemdefinition.
 */
public class ReadPriceForItemDefinitionPrototype implements PriceForItemdefinitionResource.Read {

	private final Repository<PriceRangeEntity, PriceForItemdefinitionIdentifier> repository;
	private final PriceForItemdefinitionIdentifier priceForItemdefinitionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param repository						repository
	 * @param priceForItemdefinitionIdentifier	priceForItemdefinitionIdentifier
	 */
	@Inject
	public ReadPriceForItemDefinitionPrototype(@ResourceRepository final Repository<PriceRangeEntity, PriceForItemdefinitionIdentifier> repository,
											   @RequestIdentifier final PriceForItemdefinitionIdentifier priceForItemdefinitionIdentifier) {
		this.repository = repository;
		this.priceForItemdefinitionIdentifier = priceForItemdefinitionIdentifier;
	}

	@Override
	public Single<PriceRangeEntity> onRead() {
		return repository.findOne(priceForItemdefinitionIdentifier);
	}
}
