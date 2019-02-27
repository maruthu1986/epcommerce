/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceForCartLineItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForCartLineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading the price of a cart lineitem.
 */
public class ReadPriceForCartLineItemPrototype implements PriceForCartLineItemResource.Read {

	private final Repository<CartLineItemPriceEntity, PriceForCartLineItemIdentifier> repository;
	private final PriceForCartLineItemIdentifier priceForCartLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param repository						repository
	 * @param priceForCartLineItemIdentifier	priceForCartLineItemIdentifier
	 */
	@Inject
	public ReadPriceForCartLineItemPrototype(@ResourceRepository final Repository<CartLineItemPriceEntity, PriceForCartLineItemIdentifier> repository,
											 @RequestIdentifier final PriceForCartLineItemIdentifier priceForCartLineItemIdentifier) {
		this.repository = repository;
		this.priceForCartLineItemIdentifier = priceForCartLineItemIdentifier;
	}

	@Override
	public Single<CartLineItemPriceEntity> onRead() {
		return repository.findOne(priceForCartLineItemIdentifier);
	}
}
