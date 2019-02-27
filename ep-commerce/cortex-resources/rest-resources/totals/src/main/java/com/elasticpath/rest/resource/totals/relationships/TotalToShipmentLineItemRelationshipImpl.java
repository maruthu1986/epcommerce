/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.totals.ShipmentLineItemTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalForShipmentLineItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Shipment line item total to shipment line item link.
 */
public class TotalToShipmentLineItemRelationshipImpl implements TotalForShipmentLineItemRelationship.LinkFrom {

	private final ShipmentLineItemTotalIdentifier shipmentLineItemTotalIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemTotalIdentifier shipmentLineItemTotalIdentifier
	 */
	@Inject
	public TotalToShipmentLineItemRelationshipImpl(@RequestIdentifier final ShipmentLineItemTotalIdentifier shipmentLineItemTotalIdentifier) {
		this.shipmentLineItemTotalIdentifier = shipmentLineItemTotalIdentifier;
	}

	@Override
	public Observable<ShipmentLineItemIdentifier> onLinkFrom() {
		return Observable.just(shipmentLineItemTotalIdentifier.getShipmentLineItem());
	}
}
