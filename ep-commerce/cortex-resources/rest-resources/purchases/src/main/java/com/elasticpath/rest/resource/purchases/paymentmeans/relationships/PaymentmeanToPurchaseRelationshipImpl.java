/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.PurchaseForPaymentmeanRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Paymentmean to purchase link.
 */
public class PaymentmeanToPurchaseRelationshipImpl implements PurchaseForPaymentmeanRelationship.LinkTo {

	private final PurchasePaymentmeanIdentifier purchasePaymentmeanIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchasePaymentmeanIdentifier purchasePaymentmeanIdentifier
	 */
	@Inject
	public PaymentmeanToPurchaseRelationshipImpl(@RequestIdentifier final PurchasePaymentmeanIdentifier purchasePaymentmeanIdentifier) {
		this.purchasePaymentmeanIdentifier = purchasePaymentmeanIdentifier;
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkTo() {
		return Observable.just(purchasePaymentmeanIdentifier.getPurchasePaymentmeans().getPurchase());
	}
}
