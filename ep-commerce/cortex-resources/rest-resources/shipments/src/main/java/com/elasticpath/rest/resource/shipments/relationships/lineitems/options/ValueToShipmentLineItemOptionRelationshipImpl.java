/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems.options;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueIdentifier;
import com.elasticpath.rest.definition.shipments.ValueForShipmentLineItemOptionRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a lineitem link in value.
 */
public class ValueToShipmentLineItemOptionRelationshipImpl implements ValueForShipmentLineItemOptionRelationship.LinkFrom {

	private final ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionValueIdentifier		identifier.
	 */
	@Inject
	public ValueToShipmentLineItemOptionRelationshipImpl(
			@RequestIdentifier final ShipmentLineItemOptionValueIdentifier shipmentLineItemOptionValueIdentifier) {
		this.shipmentLineItemOptionIdentifier = shipmentLineItemOptionValueIdentifier.getShipmentLineItemOption();
	}

	@Override
	public Observable<ShipmentLineItemOptionIdentifier> onLinkFrom() {
		return Observable.just(shipmentLineItemOptionIdentifier);
	}
}
