/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read options for lineitem.
 */
public class ReadOptionsForLineItemPrototype implements ShipmentLineItemOptionsResource.Read {

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier;
	private final LinksRepository<ShipmentLineItemIdentifier, ShipmentLineItemOptionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionsIdentifier		identifier
	 * @param repository							repository
	 */
	@Inject
	public ReadOptionsForLineItemPrototype(
			@RequestIdentifier final ShipmentLineItemOptionsIdentifier shipmentLineItemOptionsIdentifier,
			@ResourceRepository final LinksRepository<ShipmentLineItemIdentifier, ShipmentLineItemOptionIdentifier> repository) {
		this.shipmentLineItemIdentifier = shipmentLineItemOptionsIdentifier.getShipmentLineItem();
		this.repository = repository;
	}

	@Override
	public Observable<ShipmentLineItemOptionIdentifier> onRead() {
		return repository.getElements(shipmentLineItemIdentifier);
	}
}
