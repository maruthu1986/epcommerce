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
 * Shipment line item to Shipment line item total link.
 */
public class ShipmentLineItemToTotalRelationshipImpl implements TotalForShipmentLineItemRelationship.LinkTo {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemIdentifier shipmentLineItemIdentifier
	 */
	@Inject
	public ShipmentLineItemToTotalRelationshipImpl(@RequestIdentifier final ShipmentLineItemIdentifier shipmentLineItemIdentifier) {
		this.shipmentLineItemIdentifier = shipmentLineItemIdentifier;
	}

	@Override
	public Observable<ShipmentLineItemTotalIdentifier> onLinkTo() {
		return Observable.just(ShipmentLineItemTotalIdentifier.builder()
				.withShipmentLineItem(shipmentLineItemIdentifier)
				.build());
	}
}
