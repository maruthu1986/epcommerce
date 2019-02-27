/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.taxes.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.taxes.ShipmentTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesForShipmentRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Taxes for Shipment Relationship.
 */
public class TaxesForShipmentRelationshipImpl implements TaxesForShipmentRelationship.LinkTo {

	private final ShipmentIdentifier shipmentIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentIdentifier shipment identifier
	 */
	@Inject
	public TaxesForShipmentRelationshipImpl(@RequestIdentifier final ShipmentIdentifier shipmentIdentifier) {
		this.shipmentIdentifier = shipmentIdentifier;
	}

	@Override
	public Observable<ShipmentTaxIdentifier> onLinkTo() {
		return Observable.just(ShipmentTaxIdentifier.builder()
				.withShipment(shipmentIdentifier)
				.build());
	}
}
