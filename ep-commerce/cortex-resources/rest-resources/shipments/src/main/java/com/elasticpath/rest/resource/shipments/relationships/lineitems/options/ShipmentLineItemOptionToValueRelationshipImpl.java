/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems.options;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueIdentifier;
import com.elasticpath.rest.definition.shipments.ValueForShipmentLineItemOptionRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Adds value link in shipment line item.
 */
public class ShipmentLineItemOptionToValueRelationshipImpl implements ValueForShipmentLineItemOptionRelationship.LinkTo {

	private final ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier;
	private final LinksRepository<ShipmentLineItemOptionIdentifier, ShipmentLineItemOptionValueIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionIdentifier	identifier
	 * @param repository						repository
	 */
	@Inject
	public ShipmentLineItemOptionToValueRelationshipImpl(
			@RequestIdentifier final ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier,
			@ResourceRepository final LinksRepository<ShipmentLineItemOptionIdentifier, ShipmentLineItemOptionValueIdentifier> repository) {
		this.shipmentLineItemOptionIdentifier = shipmentLineItemOptionIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<ShipmentLineItemOptionValueIdentifier> onLinkTo() {
		return repository.getElements(shipmentLineItemOptionIdentifier);
	}
}
