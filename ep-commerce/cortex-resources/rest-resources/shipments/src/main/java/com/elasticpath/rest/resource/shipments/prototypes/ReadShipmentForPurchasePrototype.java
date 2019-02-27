/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read shipment for purchase.
 */
public class ReadShipmentForPurchasePrototype implements ShipmentResource.Read {

	private final Repository<ShipmentEntity, ShipmentIdentifier> repository;
	private final ShipmentIdentifier shipmentIdentifier;
	/**
	 * Constructor.
	 *
	 * @param shipmentIdentifier	identifier
	 * @param repository			repository
	 */
	@Inject
	public ReadShipmentForPurchasePrototype(@ResourceRepository final Repository<ShipmentEntity, ShipmentIdentifier> repository,
											@RequestIdentifier final ShipmentIdentifier shipmentIdentifier) {
		this.repository = repository;
		this.shipmentIdentifier = shipmentIdentifier;
	}

	@Override
	public Single<ShipmentEntity> onRead() {
		return repository.findOne(shipmentIdentifier);
	}
}
