/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a link from shipment lineitem to price.
 */
public class PriceToShipmentLineItemRelationshipImpl implements PriceForShipmentLineItemRelationship.LinkTo {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemIdentifier	shipmentLineItemIdentifier
	 */
	@Inject
	public PriceToShipmentLineItemRelationshipImpl(@RequestIdentifier final ShipmentLineItemIdentifier shipmentLineItemIdentifier) {
		this.shipmentLineItemIdentifier = shipmentLineItemIdentifier;
	}

	@Override
	public Observable<PriceForShipmentLineItemIdentifier> onLinkTo() {
		return Observable.just(PriceForShipmentLineItemIdentifier.builder()
				.withShipmentLineItem(shipmentLineItemIdentifier)
				.build());
	}
}
