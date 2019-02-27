/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemResource;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Prototype that implements reading the shipment price of a lineitem.
 */
public class ReadPriceForShipmentLineItemPrototype implements PriceForShipmentLineItemResource.Read {

	private final Repository<ShipmentLineItemPriceEntity, PriceForShipmentLineItemIdentifier> repository;
	private final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param repository							repository
	 * @param priceForShipmentLineItemIdentifier	priceForShipmentLineItemIdentifier
	 */
	@Inject
	public ReadPriceForShipmentLineItemPrototype(
			@ResourceRepository final Repository<ShipmentLineItemPriceEntity, PriceForShipmentLineItemIdentifier> repository,
			@RequestIdentifier final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier) {
		this.repository = repository;
		this.priceForShipmentLineItemIdentifier = priceForShipmentLineItemIdentifier;
	}

	@Override
	public Single<ShipmentLineItemPriceEntity> onRead() {
		return repository.findOne(priceForShipmentLineItemIdentifier);
	}

}
