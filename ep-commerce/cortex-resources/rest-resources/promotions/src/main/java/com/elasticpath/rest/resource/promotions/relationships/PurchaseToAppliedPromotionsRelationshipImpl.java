/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseRelationship;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a promotion link in purchase.
 */
public class PurchaseToAppliedPromotionsRelationshipImpl implements AppliedPromotionsForPurchaseRelationship.LinkTo {

	private final PurchaseIdentifier purchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseIdentifier	identifier
	 */
	@Inject
	public PurchaseToAppliedPromotionsRelationshipImpl(@RequestIdentifier final PurchaseIdentifier purchaseIdentifier) {
		this.purchaseIdentifier = purchaseIdentifier;
	}

	@Override
	public Observable<AppliedPromotionsForPurchaseIdentifier> onLinkTo() {
		return Observable.just(AppliedPromotionsForPurchaseIdentifier.builder()
				.withPurchase(purchaseIdentifier)
				.build());
	}
}
