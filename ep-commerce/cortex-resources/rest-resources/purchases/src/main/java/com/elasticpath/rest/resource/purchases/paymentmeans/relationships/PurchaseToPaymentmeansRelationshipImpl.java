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
 * Purchase to payment means link.
 */
public class PurchaseToPaymentmeansRelationshipImpl implements PaymentmeansForPurchaseRelationship.LinkTo {

	private final PurchaseIdentifier purchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseIdentifier purchaseIdentifier
	 */
	@Inject
	public PurchaseToPaymentmeansRelationshipImpl(@RequestIdentifier final PurchaseIdentifier purchaseIdentifier) {
		this.purchaseIdentifier = purchaseIdentifier;
	}

	@Override
	public Observable<PurchasePaymentmeansIdentifier> onLinkTo() {
		return Observable.just(PurchasePaymentmeansIdentifier.builder()
				.withPurchase(purchaseIdentifier)
				.build());
	}
}
