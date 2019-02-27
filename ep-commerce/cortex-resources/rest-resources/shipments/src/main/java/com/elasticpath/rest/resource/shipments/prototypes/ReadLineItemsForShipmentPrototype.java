/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read lineitems for shipment.
 */
public class ReadLineItemsForShipmentPrototype implements ShipmentLineItemsResource.Read {

	private final ShipmentIdentifier shipmentIdentifier;
	private final LinksRepository<ShipmentIdentifier, ShipmentLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemsIdentifier	identifier
	 * @param repository					repository
	 */
	@Inject
	public ReadLineItemsForShipmentPrototype(@RequestIdentifier final ShipmentLineItemsIdentifier shipmentLineItemsIdentifier,
											 @ResourceRepository final LinksRepository<ShipmentIdentifier, ShipmentLineItemIdentifier> repository) {
		this.shipmentIdentifier = shipmentLineItemsIdentifier.getShipment();
		this.repository = repository;
	}

	@Override
	public Observable<ShipmentLineItemIdentifier> onRead() {
		return repository.getElements(shipmentIdentifier);
	}
}
