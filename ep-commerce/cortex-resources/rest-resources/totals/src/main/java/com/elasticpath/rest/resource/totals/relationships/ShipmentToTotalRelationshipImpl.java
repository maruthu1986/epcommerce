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
 * Order to order total link.
 */
public class ShipmentToTotalRelationshipImpl implements TotalForShipmentRelationship.LinkTo {

	private final ShipmentIdentifier shipmentIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentIdentifier shipmentIdentifier
	 */
	@Inject
	public ShipmentToTotalRelationshipImpl(@RequestIdentifier final ShipmentIdentifier shipmentIdentifier) {
		this.shipmentIdentifier = shipmentIdentifier;
	}

	@Override
	public Observable<ShipmentTotalIdentifier> onLinkTo() {
		return Observable.just(ShipmentTotalIdentifier.builder()
				.withShipment(shipmentIdentifier)
				.build());
	}
}
