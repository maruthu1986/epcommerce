/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.totals.ShipmentLineItemTotalIdentifier;
import com.elasticpath.rest.definition.totals.ShipmentLineItemTotalResource;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Shipment Line Item Total prototype for Read operation.
 */
public class ReadShipmentLineItemTotalPrototype implements ShipmentLineItemTotalResource.Read {

	private final ShipmentLineItemTotalIdentifier shipmentLineItemTotalIdentifier;

	private final Repository<TotalEntity, ShipmentLineItemTotalIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemTotalIdentifier shipmentLineItemTotalIdentifier
	 * @param repository                      repository
	 */
	@Inject
	public ReadShipmentLineItemTotalPrototype(@RequestIdentifier final ShipmentLineItemTotalIdentifier shipmentLineItemTotalIdentifier,
											  @ResourceRepository final Repository<TotalEntity, ShipmentLineItemTotalIdentifier> repository) {
		this.shipmentLineItemTotalIdentifier = shipmentLineItemTotalIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TotalEntity> onRead() {
		return repository.findOne(shipmentLineItemTotalIdentifier);
	}
}
