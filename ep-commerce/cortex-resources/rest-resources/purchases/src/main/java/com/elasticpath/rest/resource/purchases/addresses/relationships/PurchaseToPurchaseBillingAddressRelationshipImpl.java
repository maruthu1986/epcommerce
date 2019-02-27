/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.addresses.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.purchases.BillingAddressForPurchaseRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseBillingaddressIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Purchase to purchase billing address link.
 */
public class PurchaseToPurchaseBillingAddressRelationshipImpl implements BillingAddressForPurchaseRelationship.LinkTo {

	private final PurchaseIdentifier purchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseIdentifier purchaseIdentifier
	 */
	@Inject
	public PurchaseToPurchaseBillingAddressRelationshipImpl(@RequestIdentifier final PurchaseIdentifier purchaseIdentifier) {
		this.purchaseIdentifier = purchaseIdentifier;
	}

	@Override
	public Observable<PurchaseBillingaddressIdentifier> onLinkTo() {
		return Observable.just(PurchaseBillingaddressIdentifier.builder()
				.withPurchase(purchaseIdentifier)
				.build());
	}
}
