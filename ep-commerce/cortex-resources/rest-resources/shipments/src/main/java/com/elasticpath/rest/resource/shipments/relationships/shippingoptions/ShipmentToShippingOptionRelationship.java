/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.shippingoptions;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.PurchaseShipmentShippingOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShippingOptionForPurchaseRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a shippingoption link in shipment.
 */
public class ShipmentToShippingOptionRelationship implements ShippingOptionForPurchaseRelationship.LinkTo {

	private final ShipmentIdentifier shipmentIdentifier;

	/**
	 * Cosntructor.
	 *
	 * @param shipmentIdentifier	identifier
	 */
	@Inject
	public ShipmentToShippingOptionRelationship(@RequestIdentifier final ShipmentIdentifier shipmentIdentifier) {
		this.shipmentIdentifier = shipmentIdentifier;
	}

	@Override
	public Observable<PurchaseShipmentShippingOptionIdentifier> onLinkTo() {
		return Observable.just(PurchaseShipmentShippingOptionIdentifier.builder()
				.withShipment(shipmentIdentifier)
				.build());
	}
}
