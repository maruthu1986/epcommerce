/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read value of shipment line item option.
 */
public class ReadValueForShipmentLineItemOptionPrototype implements ShipmentLineItemOptionValueResource.Read {

	private final ShipmentLineItemOptionValueIdentifier shipmentLineItemOptionValueIdentifier;
	private final Repository<ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionValueIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionValueIdentifier		identifier
	 * @param repository								repository
	 */
	@Inject
	public ReadValueForShipmentLineItemOptionPrototype(
			@RequestIdentifier final ShipmentLineItemOptionValueIdentifier shipmentLineItemOptionValueIdentifier,
			@ResourceRepository final Repository<ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionValueIdentifier> repository) {
		this.shipmentLineItemOptionValueIdentifier = shipmentLineItemOptionValueIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<ShipmentLineItemOptionValueEntity> onRead() {
		return repository.findOne(shipmentLineItemOptionValueIdentifier);
	}
}
