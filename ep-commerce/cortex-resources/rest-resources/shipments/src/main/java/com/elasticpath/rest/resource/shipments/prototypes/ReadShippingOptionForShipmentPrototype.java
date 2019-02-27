/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.PurchaseShipmentShippingOptionIdentifier;
import com.elasticpath.rest.definition.shipments.PurchaseShipmentShippingOptionResource;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read shipping option for shipment.
 */
public class ReadShippingOptionForShipmentPrototype implements PurchaseShipmentShippingOptionResource.Read {

	private final ShipmentIdentifier shipmentIdentifier;
	private final Repository<ShippingOptionEntity, ShipmentIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseShipmentShippingOptionIdentifier		identifier
	 * @param repository									repository
	 */
	@Inject
	public ReadShippingOptionForShipmentPrototype(
			@RequestIdentifier final PurchaseShipmentShippingOptionIdentifier purchaseShipmentShippingOptionIdentifier,
			@ResourceRepository final Repository<ShippingOptionEntity, ShipmentIdentifier> repository) {
		this.shipmentIdentifier = purchaseShipmentShippingOptionIdentifier.getShipment();
		this.repository = repository;
	}

	@Override
	public Single<ShippingOptionEntity> onRead() {
		return repository.findOne(shipmentIdentifier);
	}
}
