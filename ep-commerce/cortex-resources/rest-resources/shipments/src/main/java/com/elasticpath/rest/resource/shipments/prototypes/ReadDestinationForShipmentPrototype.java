/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.PurchaseShipmentShippingAddressIdentifier;
import com.elasticpath.rest.definition.shipments.PurchaseShipmentShippingAddressResource;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read destination of shipment.
 */
public class ReadDestinationForShipmentPrototype implements PurchaseShipmentShippingAddressResource.Read {

	private final ShipmentIdentifier shipmentIdentifier;
	private final Repository<AddressEntity, ShipmentIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseShipmentShippingAddressIdentifier		identifier
	 * @param repository 									repository
	 */
	@Inject
	public ReadDestinationForShipmentPrototype(
			@RequestIdentifier final PurchaseShipmentShippingAddressIdentifier purchaseShipmentShippingAddressIdentifier,
			@ResourceRepository final Repository<AddressEntity, ShipmentIdentifier> repository) {
		this.shipmentIdentifier = purchaseShipmentShippingAddressIdentifier.getShipment();
		this.repository = repository;
	}

	@Override
	public Single<AddressEntity> onRead() {
		return repository.findOne(shipmentIdentifier);
	}
}
