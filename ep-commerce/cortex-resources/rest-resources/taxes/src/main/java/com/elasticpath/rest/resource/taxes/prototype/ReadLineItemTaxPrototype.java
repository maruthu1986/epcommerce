/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.taxes.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.taxes.ShipmentLineItemTaxIdentifier;
import com.elasticpath.rest.definition.taxes.ShipmentLineItemTaxResource;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read Line Item Tax Prototype.
 */
public class ReadLineItemTaxPrototype implements ShipmentLineItemTaxResource.Read {

	private final ShipmentLineItemTaxIdentifier shipmentLineItemTaxIdentifier;

	private final Repository<TaxesEntity, ShipmentLineItemTaxIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemTaxIdentifier shipment tax identifier
	 * @param repository          repository
	 */
	@Inject
	public ReadLineItemTaxPrototype(@RequestIdentifier final ShipmentLineItemTaxIdentifier shipmentLineItemTaxIdentifier,
									@ResourceRepository final Repository<TaxesEntity, ShipmentLineItemTaxIdentifier> repository) {
		this.shipmentLineItemTaxIdentifier = shipmentLineItemTaxIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<TaxesEntity> onRead() {
		return repository.findOne(shipmentLineItemTaxIdentifier);
	}
}
