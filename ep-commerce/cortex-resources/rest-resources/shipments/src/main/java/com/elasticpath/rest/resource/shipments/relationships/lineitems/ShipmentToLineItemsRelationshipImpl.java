/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.LineItemsForShipmentRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a lineitems like in shipment.
 */
public class ShipmentToLineItemsRelationshipImpl implements LineItemsForShipmentRelationship.LinkTo {

	private final ShipmentIdentifier shipmentIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentIdentifier	identifier
	 */
	@Inject
	public ShipmentToLineItemsRelationshipImpl(@RequestIdentifier final ShipmentIdentifier shipmentIdentifier) {
		this.shipmentIdentifier = shipmentIdentifier;
	}

	@Override
	public Observable<ShipmentLineItemsIdentifier> onLinkTo() {
		return Observable.just(ShipmentLineItemsIdentifier.builder()
				.withShipment(shipmentIdentifier)
				.build());
	}
}
