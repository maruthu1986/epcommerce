/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.addresses;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.DestinationAddressForPurchaseRelationship;
import com.elasticpath.rest.definition.shipments.PurchaseShipmentShippingAddressIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a destination link in shipment.
 */
public class ShipmentToDestinationRelationshipImpl implements DestinationAddressForPurchaseRelationship.LinkTo {

	private final ShipmentIdentifier shipmentIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentIdentifier	identifier
	 */
	@Inject
	public ShipmentToDestinationRelationshipImpl(@RequestIdentifier final ShipmentIdentifier shipmentIdentifier) {
		this.shipmentIdentifier = shipmentIdentifier;
	}

	@Override
	public Observable<PurchaseShipmentShippingAddressIdentifier> onLinkTo() {
		return Observable.just(PurchaseShipmentShippingAddressIdentifier.builder()
				.withShipment(shipmentIdentifier)
				.build());
	}
}
