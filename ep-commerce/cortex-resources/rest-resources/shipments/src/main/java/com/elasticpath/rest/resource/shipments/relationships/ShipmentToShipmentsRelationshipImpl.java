/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsForShipmentRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a shipments link in shipment.
 */
public class ShipmentToShipmentsRelationshipImpl implements ShipmentsForShipmentRelationship.LinkTo {

	private final ShipmentsIdentifier shipmentsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentIdentifier	identifier
	 */
	@Inject
	public ShipmentToShipmentsRelationshipImpl(@RequestIdentifier final ShipmentIdentifier shipmentIdentifier) {
		this.shipmentsIdentifier = shipmentIdentifier.getShipments();
	}

	@Override
	public Observable<ShipmentsIdentifier> onLinkTo() {
		return Observable.just(shipmentsIdentifier);
	}
}
