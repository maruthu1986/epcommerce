/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.totals.ShipmentTotalIdentifier;
import com.elasticpath.rest.definition.totals.ShipmentTotalResource;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Shipment Total prototype for Read operation.
 */
public class ReadShipmentTotalPrototype implements ShipmentTotalResource.Read {

	private final ShipmentTotalIdentifier shipmentTotalIdentifier;

	private final Repository<TotalEntity, ShipmentTotalIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentTotalIdentifier shipmentTotalIdentifier
	 * @param repository              repository
	 */
	@Inject
	public ReadShipmentTotalPrototype(@RequestIdentifier final ShipmentTotalIdentifier shipmentTotalIdentifier,
									  @ResourceRepository final Repository<TotalEntity, ShipmentTotalIdentifier> repository) {
		this.shipmentTotalIdentifier = shipmentTotalIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TotalEntity> onRead() {
		return repository.findOne(shipmentTotalIdentifier);
	}
}
