/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsForLineItemRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a list link in shipment line item.
 */
public class ShipmentLineItemsToLineItemRelationshipImpl implements ShipmentLineItemsForLineItemRelationship.LinkTo {

	private final ShipmentLineItemsIdentifier shipmentLineItemsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemIdentifier	identifier
	 */
	@Inject
	public ShipmentLineItemsToLineItemRelationshipImpl(@RequestIdentifier final ShipmentLineItemIdentifier shipmentLineItemIdentifier) {
		this.shipmentLineItemsIdentifier = shipmentLineItemIdentifier.getShipmentLineItems();
	}

	@Override
	public Observable<ShipmentLineItemsIdentifier> onLinkTo() {
		return Observable.just(shipmentLineItemsIdentifier);
	}
}
