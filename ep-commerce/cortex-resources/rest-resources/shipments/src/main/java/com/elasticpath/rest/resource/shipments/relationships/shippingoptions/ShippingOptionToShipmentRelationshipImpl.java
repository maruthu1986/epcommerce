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
 * Adds a shipment link in shippingoption.
 */
public class ShippingOptionToShipmentRelationshipImpl implements ShippingOptionForPurchaseRelationship.LinkFrom {

	private final ShipmentIdentifier shipmentIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseShipmentShippingOptionIdentifier	identifier.
	 */
	@Inject
	public ShippingOptionToShipmentRelationshipImpl(
			@RequestIdentifier final PurchaseShipmentShippingOptionIdentifier purchaseShipmentShippingOptionIdentifier) {
		this.shipmentIdentifier = purchaseShipmentShippingOptionIdentifier.getShipment();
	}

	@Override
	public Observable<ShipmentIdentifier> onLinkFrom() {
		return Observable.just(shipmentIdentifier);
	}
}
