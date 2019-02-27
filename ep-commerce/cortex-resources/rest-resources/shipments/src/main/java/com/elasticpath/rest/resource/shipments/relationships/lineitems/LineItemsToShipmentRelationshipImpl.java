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
 * Adds a shipment link in lineitems.
 */
public class LineItemsToShipmentRelationshipImpl implements LineItemsForShipmentRelationship.LinkFrom {

	private final ShipmentIdentifier shipmentIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemsIdentifier	identifier
	 */
	@Inject
	public LineItemsToShipmentRelationshipImpl(@RequestIdentifier final ShipmentLineItemsIdentifier shipmentLineItemsIdentifier) {
		this.shipmentIdentifier = shipmentLineItemsIdentifier.getShipment();
	}

	@Override
	public Observable<ShipmentIdentifier> onLinkFrom() {
		return Observable.just(shipmentIdentifier);
	}
}
