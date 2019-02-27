/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase Line Item prototype for Read operation.
 */
public class ReadPurchaseLineItemPrototype implements PurchaseLineItemResource.Read {

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier;

	private final Repository<PurchaseLineItemEntity, PurchaseLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemIdentifier purchaseLineItemIdentifier
	 * @param repository                 repository
	 */
	@Inject
	public ReadPurchaseLineItemPrototype(
			@RequestIdentifier final PurchaseLineItemIdentifier purchaseLineItemIdentifier,
			@ResourceRepository final Repository<PurchaseLineItemEntity, PurchaseLineItemIdentifier> repository) {
		this.purchaseLineItemIdentifier = purchaseLineItemIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PurchaseLineItemEntity> onRead() {
		return repository.findOne(purchaseLineItemIdentifier);
	}
}
