/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems.options;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.OptionsForShipmentLineItemRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a lineitem link in shipment lineitem options.
 */
public class OptionsToShipmentLineItemRelationshipImpl implements OptionsForShipmentLineItemRelationship.LinkFrom {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionsIdentifier		identifier
	 */
	@Inject
	public OptionsToShipmentLineItemRelationshipImpl(@RequestIdentifier final ShipmentLineItemOptionsIdentifier shipmentLineItemOptionsIdentifier) {
		this.shipmentLineItemIdentifier = shipmentLineItemOptionsIdentifier.getShipmentLineItem();
	}

	@Override
	public Observable<ShipmentLineItemIdentifier> onLinkFrom() {
		return Observable.just(shipmentLineItemIdentifier);
	}
}
