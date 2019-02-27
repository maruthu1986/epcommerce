/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.totals.ShipmentTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalForShipmentRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Shipment total to shipment link.
 */
public class TotalToShipmentRelationshipImpl implements TotalForShipmentRelationship.LinkFrom {

	private final ShipmentTotalIdentifier shipmentTotalIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentTotalIdentifier shipmentTotalIdentifier
	 */
	@Inject
	public TotalToShipmentRelationshipImpl(@RequestIdentifier final ShipmentTotalIdentifier shipmentTotalIdentifier) {
		this.shipmentTotalIdentifier = shipmentTotalIdentifier;
	}

	@Override
	public Observable<ShipmentIdentifier> onLinkFrom() {
		return Observable.just(shipmentTotalIdentifier.getShipment());
	}
}
