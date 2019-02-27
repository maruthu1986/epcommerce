/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.PaymentmeansForPurchaseRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeansIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Payment means to purchase link.
 */
public class PaymentmeansToPurchaseRelationshipImpl implements PaymentmeansForPurchaseRelationship.LinkFrom {

	private final PurchasePaymentmeansIdentifier purchasePaymentmeansIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchasePaymentmeansIdentifier purchasePaymentmeansIdentifier
	 */
	@Inject
	public PaymentmeansToPurchaseRelationshipImpl(@RequestIdentifier final PurchasePaymentmeansIdentifier purchasePaymentmeansIdentifier) {
		this.purchasePaymentmeansIdentifier = purchasePaymentmeansIdentifier;
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkFrom() {
		return Observable.just(purchasePaymentmeansIdentifier.getPurchase());
	}
}
