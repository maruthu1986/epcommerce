/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase Paymentmean prototype for Read operation.
 */
public class ReadPurchasePaymentmeanPrototype implements PurchasePaymentmeanResource.Read {

	private final PurchasePaymentmeanIdentifier purchasePaymentmeanIdentifier;

	private final Repository<PaymentMeansEntity, PurchasePaymentmeanIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchasePaymentmeanIdentifier purchasePaymentmeanIdentifier
	 * @param repository                    repository
	 */
	@Inject
	public ReadPurchasePaymentmeanPrototype(
			@RequestIdentifier final PurchasePaymentmeanIdentifier purchasePaymentmeanIdentifier,
			@ResourceRepository final Repository<PaymentMeansEntity, PurchasePaymentmeanIdentifier> repository) {
		this.purchasePaymentmeanIdentifier = purchasePaymentmeanIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<PaymentMeansEntity> onRead() {
		return repository.findOne(purchasePaymentmeanIdentifier);
	}
}
