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
 * Purchase billing address to purchase link.
 */
public class PurchaseBillingAddressToPurchaseRelationshipImpl implements BillingAddressForPurchaseRelationship.LinkFrom {

	private final PurchaseBillingaddressIdentifier purchaseBillingaddressIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseBillingaddressIdentifier purchaseBillingaddressIdentifier
	 */
	@Inject
	public PurchaseBillingAddressToPurchaseRelationshipImpl(
			@RequestIdentifier final PurchaseBillingaddressIdentifier purchaseBillingaddressIdentifier) {
		this.purchaseBillingaddressIdentifier = purchaseBillingaddressIdentifier;
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkFrom() {
		return Observable.just(purchaseBillingaddressIdentifier.getPurchase());
	}
}
