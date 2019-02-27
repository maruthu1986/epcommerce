/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype for reading a price for an item.
 */
public class ReadItemPriceProtoype implements PriceForItemResource.Read {

	private final Repository<ItemPriceEntity, PriceForItemIdentifier> repository;
	private final PriceForItemIdentifier priceForItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param repository             repository
	 * @param priceForItemIdentifier identifier
	 */
	@Inject
	public ReadItemPriceProtoype(@ResourceRepository final Repository<ItemPriceEntity, PriceForItemIdentifier> repository, @RequestIdentifier final
	PriceForItemIdentifier priceForItemIdentifier) {
		this.repository = repository;
		this.priceForItemIdentifier = priceForItemIdentifier;
	}

	@Override
	public Single<ItemPriceEntity> onRead() {
		return repository.findOne(priceForItemIdentifier);
	}
}
