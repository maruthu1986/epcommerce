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
 * Discount to Purchase link.
 */
public class DiscountToPurchaseRelationshipImpl implements DiscountToPurchaseRelationship.LinkTo {

	private final DiscountForPurchaseIdentifier discountForPurchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param discountForPurchaseIdentifier	discountForPurchaseIdentifier
	 */
	@Inject
	public DiscountToPurchaseRelationshipImpl(@RequestIdentifier final DiscountForPurchaseIdentifier discountForPurchaseIdentifier) {
		this.discountForPurchaseIdentifier = discountForPurchaseIdentifier;
	}

	@Override
	public Observable<PurchaseIdentifier> onLinkTo() {
		return Observable.just(discountForPurchaseIdentifier.getPurchase());
	}
}
