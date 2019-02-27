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
 * Taxes for Shipment Line Item Relationship.
 */
public class TaxesForShipmentLineItemRelationshipImpl implements TaxesForShipmentLineItemRelationship.LinkTo {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemIdentifier order identifier
	 */
	@Inject
	public TaxesForShipmentLineItemRelationshipImpl(@RequestIdentifier final ShipmentLineItemIdentifier shipmentLineItemIdentifier) {
		this.shipmentLineItemIdentifier = shipmentLineItemIdentifier;
	}

	@Override
	public Observable<ShipmentLineItemTaxIdentifier> onLinkTo() {
		return Observable.just(ShipmentLineItemTaxIdentifier.builder()
				.withShipmentLineItem(shipmentLineItemIdentifier).build());
	}
}
