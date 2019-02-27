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
 * Adds a shipment link in destination.
 */
public class DestinationToShipmentRelationshipImpl implements DestinationAddressForPurchaseRelationship.LinkFrom {

	private final PurchaseShipmentShippingAddressIdentifier purchaseShipmentShippingAddressIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseShipmentShippingAddressIdentifier		identifier
	 */
	@Inject
	public DestinationToShipmentRelationshipImpl(
			@RequestIdentifier final PurchaseShipmentShippingAddressIdentifier purchaseShipmentShippingAddressIdentifier) {
		this.purchaseShipmentShippingAddressIdentifier = purchaseShipmentShippingAddressIdentifier;
	}

	@Override
	public Observable<ShipmentIdentifier> onLinkFrom() {
		return Observable.just(purchaseShipmentShippingAddressIdentifier.getShipment());
	}
}
