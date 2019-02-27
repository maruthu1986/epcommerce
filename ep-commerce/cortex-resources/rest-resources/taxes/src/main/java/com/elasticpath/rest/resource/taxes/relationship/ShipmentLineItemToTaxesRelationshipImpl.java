/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.taxes.relationship;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.taxes.ShipmentLineItemTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesForShipmentLineItemRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Shipment Line Item to Taxes Relationship.
 */
public class ShipmentLineItemToTaxesRelationshipImpl implements TaxesForShipmentLineItemRelationship.LinkFrom {

	private final ShipmentLineItemTaxIdentifier shipmentLineItemTaxIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemTaxIdentifier shipmentLineItemTaxIdentifier
	 */
	@Inject
	public ShipmentLineItemToTaxesRelationshipImpl(@RequestIdentifier final ShipmentLineItemTaxIdentifier shipmentLineItemTaxIdentifier) {
		this.shipmentLineItemTaxIdentifier = shipmentLineItemTaxIdentifier;
	}

	@Override
	public Observable<ShipmentLineItemIdentifier> onLinkFrom() {
		return Observable.just(shipmentLineItemTaxIdentifier.getShipmentLineItem());
	}
}
