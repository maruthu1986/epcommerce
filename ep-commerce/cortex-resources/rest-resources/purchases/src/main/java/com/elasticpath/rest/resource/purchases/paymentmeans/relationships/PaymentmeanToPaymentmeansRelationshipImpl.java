/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.PaymentmeansForPaymentmeanRelationship;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeansIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Paymentmean to paymentmeans list link.
 */
public class PaymentmeanToPaymentmeansRelationshipImpl implements PaymentmeansForPaymentmeanRelationship.LinkTo {

	private final PurchasePaymentmeanIdentifier purchasePaymentmeanIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchasePaymentmeanIdentifier purchasePaymentmeanIdentifier
	 */
	@Inject
	public PaymentmeanToPaymentmeansRelationshipImpl(@RequestIdentifier final PurchasePaymentmeanIdentifier purchasePaymentmeanIdentifier) {
		this.purchasePaymentmeanIdentifier = purchasePaymentmeanIdentifier;
	}

	@Override
	public Observable<PurchasePaymentmeansIdentifier> onLinkTo() {
		return Observable.just(purchasePaymentmeanIdentifier.getPurchasePaymentmeans());
	}
}
