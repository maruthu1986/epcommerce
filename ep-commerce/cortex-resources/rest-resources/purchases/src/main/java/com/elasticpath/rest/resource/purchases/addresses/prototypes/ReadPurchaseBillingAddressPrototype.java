/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.addresses.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.purchases.PurchaseBillingaddressIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseBillingaddressResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase billing address prototype for Read operation.
 */
public class ReadPurchaseBillingAddressPrototype implements PurchaseBillingaddressResource.Read {

	private final PurchaseBillingaddressIdentifier purchaseBillingaddressIdentifier;

	private final Repository<AddressEntity, PurchaseBillingaddressIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchaseBillingaddressIdentifier purchaseBillingaddressIdentifier
	 * @param repository                       repository
	 */
	@Inject
	public ReadPurchaseBillingAddressPrototype(
			@RequestIdentifier final PurchaseBillingaddressIdentifier purchaseBillingaddressIdentifier,
			@ResourceRepository final Repository<AddressEntity, PurchaseBillingaddressIdentifier> repository) {
		this.purchaseBillingaddressIdentifier = purchaseBillingaddressIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<AddressEntity> onRead() {
		return repository.findOne(purchaseBillingaddressIdentifier);
	}
}
