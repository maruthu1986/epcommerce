/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeansIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeansResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Purchase Paymentmeans prototype for Read operation.
 */
public class ReadPurchasePaymentmeansPrototype implements PurchasePaymentmeansResource.Read {

	private final PurchasePaymentmeansIdentifier purchasePaymentmeansIdentifier;

	private final LinksRepository<PurchasePaymentmeansIdentifier, PurchasePaymentmeanIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param purchasePaymentmeansIdentifier payment means identifier
	 * @param repository links repository
	 */
	@Inject
	public ReadPurchasePaymentmeansPrototype(
			@RequestIdentifier final PurchasePaymentmeansIdentifier purchasePaymentmeansIdentifier,
			@ResourceRepository final LinksRepository<PurchasePaymentmeansIdentifier, PurchasePaymentmeanIdentifier> repository) {
		this.purchasePaymentmeansIdentifier = purchasePaymentmeansIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<PurchasePaymentmeanIdentifier> onRead() {
		return repository.getElements(purchasePaymentmeansIdentifier);
	}
}
