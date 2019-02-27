/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase prototype for Read operation.
 */
public class ReadPurchasePrototype implements PurchaseResource.Read {

	private final PurchaseIdentifier purchaseIdentifier;

	private final Repository<PurchaseEntity, PurchaseIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseIdentifier purchaseIdentifier
	 * @param repository         repository
	 */
	@Inject
	public ReadPurchasePrototype(
			@RequestIdentifier final PurchaseIdentifier purchaseIdentifier,
			@ResourceRepository final Repository<PurchaseEntity, PurchaseIdentifier> repository) {
		this.purchaseIdentifier = purchaseIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PurchaseEntity> onRead() {
		return repository.findOne(purchaseIdentifier);
	}
}
