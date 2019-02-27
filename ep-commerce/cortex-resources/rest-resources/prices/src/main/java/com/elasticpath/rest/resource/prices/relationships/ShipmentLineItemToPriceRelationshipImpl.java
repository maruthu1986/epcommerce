/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.prices.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a link from lineitem to shipment price.
 */
public class ShipmentLineItemToPriceRelationshipImpl implements PriceForShipmentLineItemRelationship.LinkFrom {

	private final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier;

	/**
	 * Constructor.
	 *
	 * @param priceForShipmentLineItemIdentifier	identifier
	 */
	@Inject
	public ShipmentLineItemToPriceRelationshipImpl(@RequestIdentifier final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier) {
		this.priceForShipmentLineItemIdentifier = priceForShipmentLineItemIdentifier;
	}

	@Override
	public Observable<ShipmentLineItemIdentifier> onLinkFrom() {
		ShipmentLineItemsIdentifier shipmentLineItemsIdentifier = priceForShipmentLineItemIdentifier.getShipmentLineItem().getShipmentLineItems();
		IdentifierPart<String> shipmentLineItemIdentifier =  priceForShipmentLineItemIdentifier.getShipmentLineItem().getShipmentLineItemId();
		return Observable.just(ShipmentLineItemIdentifier.builder()
				.withShipmentLineItems(shipmentLineItemsIdentifier)
				.withShipmentLineItemId(shipmentLineItemIdentifier)
				.build());
	}
}
