/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentForShipmentLineItemRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a shipment link in line item.
 */
public class LineItemToShipmentRelationshipImpl implements ShipmentForShipmentLineItemRelationship.LinkTo {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemIdentifier	identifier
	 */
	@Inject
	public LineItemToShipmentRelationshipImpl(@RequestIdentifier final ShipmentLineItemIdentifier shipmentLineItemIdentifier) {
		this.shipmentLineItemIdentifier = shipmentLineItemIdentifier;
	}

	@Override
	public Observable<ShipmentIdentifier> onLinkTo() {
		return Observable.just(shipmentLineItemIdentifier.getShipmentLineItems().getShipment());
	}
}
