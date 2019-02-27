/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read option for lineitem.
 */
public class ReadOptionForLineItemPrototype implements ShipmentLineItemOptionResource.Read {

	private final ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier;
	private final Repository<ShipmentLineItemOptionEntity, ShipmentLineItemOptionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionIdentifier	identifier
	 * @param repository						repository
	 */
	@Inject
	public ReadOptionForLineItemPrototype(
			@RequestIdentifier final ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier,
			@ResourceRepository final Repository<ShipmentLineItemOptionEntity, ShipmentLineItemOptionIdentifier> repository) {
		this.shipmentLineItemOptionIdentifier = shipmentLineItemOptionIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<ShipmentLineItemOptionEntity> onRead() {
		return repository.findOne(shipmentLineItemOptionIdentifier);
	}
}
