/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read shipment line item.
 */
public class ReadLineItemForShipmentPrototype implements ShipmentLineItemResource.Read {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;
	private final Repository<ShipmentLineItemEntity, ShipmentLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemIdentifier	identifier
	 * @param repository					repository
	 */
	@Inject
	public ReadLineItemForShipmentPrototype(@RequestIdentifier final ShipmentLineItemIdentifier shipmentLineItemIdentifier,
											@ResourceRepository final Repository<ShipmentLineItemEntity, ShipmentLineItemIdentifier> repository) {
		this.shipmentLineItemIdentifier = shipmentLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<ShipmentLineItemEntity> onRead() {
		return repository.findOne(shipmentLineItemIdentifier);
	}
}
