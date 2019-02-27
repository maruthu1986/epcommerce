/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.discounts.DiscountForPurchaseIdentifier;
import com.elasticpath.rest.definition.discounts.DiscountToPurchaseRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Discount from Purchase link.
 */
public class DiscountFromPurchaseRelationshipImpl implements DiscountToPurchaseRelationship.LinkFrom {

	private final PurchaseIdentifier purchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseIdentifier	purchaseIdentifier
	 */
	@Inject
	public DiscountFromPurchaseRelationshipImpl(@RequestIdentifier final PurchaseIdentifier purchaseIdentifier) {
		this.purchaseIdentifier = purchaseIdentifier;
	}

	@Override
	public Observable<DiscountForPurchaseIdentifier> onLinkFrom() {
		return Observable.just(DiscountForPurchaseIdentifier.builder()
				.withPurchase(purchaseIdentifier)
				.build());
	}
}
