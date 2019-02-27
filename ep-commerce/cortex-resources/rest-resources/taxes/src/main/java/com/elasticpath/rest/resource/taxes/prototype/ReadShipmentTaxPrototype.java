/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.taxes.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.taxes.ShipmentTaxIdentifier;
import com.elasticpath.rest.definition.taxes.ShipmentTaxResource;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read Shipment Tax Prototype.
 */
public class ReadShipmentTaxPrototype implements ShipmentTaxResource.Read {

	private final ShipmentTaxIdentifier shipmentTaxIdentifier;

	private final Repository<TaxesEntity, ShipmentTaxIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentTaxIdentifier shipment tax identifier
	 * @param repository          repository
	 */
	@Inject
	public ReadShipmentTaxPrototype(@RequestIdentifier final ShipmentTaxIdentifier shipmentTaxIdentifier,
									@ResourceRepository final Repository<TaxesEntity, ShipmentTaxIdentifier> repository) {
		this.shipmentTaxIdentifier = shipmentTaxIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TaxesEntity> onRead() {
		return repository.findOne(shipmentTaxIdentifier);
	}
}
