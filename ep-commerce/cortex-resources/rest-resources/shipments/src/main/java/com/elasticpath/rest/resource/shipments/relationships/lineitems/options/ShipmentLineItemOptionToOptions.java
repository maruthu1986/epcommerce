/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems.options;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.OptionsForShipmentLineItemOptionRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds an options link in shipment line item option.
 */
public class ShipmentLineItemOptionToOptions implements OptionsForShipmentLineItemOptionRelationship.LinkTo {

	private final ShipmentLineItemOptionsIdentifier shipmentLineItemOptionsIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionIdentifier	identifier
	 */
	@Inject
	public ShipmentLineItemOptionToOptions(@RequestIdentifier final ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier) {
		this.shipmentLineItemOptionsIdentifier = shipmentLineItemOptionIdentifier.getShipmentLineItemOptions();
	}

	@Override
	public Observable<ShipmentLineItemOptionsIdentifier> onLinkTo() {
		return Observable.just(shipmentLineItemOptionsIdentifier);
	}
}
