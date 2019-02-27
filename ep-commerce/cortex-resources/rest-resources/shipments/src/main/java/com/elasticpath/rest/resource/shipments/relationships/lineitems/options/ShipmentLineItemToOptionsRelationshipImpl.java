/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.relationships.lineitems.options;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipments.OptionsForShipmentLineItemRelationship;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Adds a options link in shipment lineitem.
 */
public class ShipmentLineItemToOptionsRelationshipImpl implements OptionsForShipmentLineItemRelationship.LinkTo {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;
	private final LinksRepository<ShipmentLineItemIdentifier, ShipmentLineItemOptionsIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemIdentifier	identifier
	 * @param repository					repository
	 */
	@Inject
	public ShipmentLineItemToOptionsRelationshipImpl(
			@RequestIdentifier final ShipmentLineItemIdentifier shipmentLineItemIdentifier,
			@ResourceRepository final LinksRepository<ShipmentLineItemIdentifier, ShipmentLineItemOptionsIdentifier> repository) {
		this.shipmentLineItemIdentifier = shipmentLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<ShipmentLineItemOptionsIdentifier> onLinkTo() {
		return repository.getElements(shipmentLineItemIdentifier);
	}
}
