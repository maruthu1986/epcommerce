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
 * Shipment to Taxes Relationship.
 */
public class ShipmentToTaxesRelationshipImpl implements TaxesForShipmentRelationship.LinkFrom {

	private final ShipmentTaxIdentifier shipmentTaxIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentTaxIdentifier shipmentTaxIdentifier
	 */
	@Inject
	public ShipmentToTaxesRelationshipImpl(@RequestIdentifier final ShipmentTaxIdentifier shipmentTaxIdentifier) {
		this.shipmentTaxIdentifier = shipmentTaxIdentifier;
	}

	@Override
	public Observable<ShipmentIdentifier> onLinkFrom() {
		return Observable.just(shipmentTaxIdentifier.getShipment());
	}
}
