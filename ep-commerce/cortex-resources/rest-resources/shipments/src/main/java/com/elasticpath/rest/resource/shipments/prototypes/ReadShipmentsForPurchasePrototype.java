/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read shipments for purchase.
 */
public class ReadShipmentsForPurchasePrototype implements ShipmentsResource.Read {

	private final PurchaseIdentifier purchaseIdentifier;
	private final LinksRepository<PurchaseIdentifier, ShipmentIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shipmentsIdentifier	identifier
	 * @param repository			repository
	 */
	@Inject
	public ReadShipmentsForPurchasePrototype(@RequestIdentifier final ShipmentsIdentifier shipmentsIdentifier,
											 @ResourceRepository final LinksRepository<PurchaseIdentifier, ShipmentIdentifier> repository) {
		this.purchaseIdentifier = shipmentsIdentifier.getPurchase();
		this.repository = repository;
	}

	@Override
	public Observable<ShipmentIdentifier> onRead() {
		return repository.getElements(purchaseIdentifier);
	}
}
