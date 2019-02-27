/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.relationship.linkto;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.CouponsAppliedToPurchaseRelationship;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Coupons applied to Purchase relationship.
 */
public class CouponsAppliedToPurchaseRelationshipImpl implements CouponsAppliedToPurchaseRelationship.LinkTo {

	private final PurchaseIdentifier purchaseIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseIdentifier purchase identifier
	 */
	@Inject
	public CouponsAppliedToPurchaseRelationshipImpl(@RequestIdentifier final PurchaseIdentifier purchaseIdentifier) {
		this.purchaseIdentifier = purchaseIdentifier;
	}

	@Override
	public Observable<PurchaseCouponListIdentifier> onLinkTo() {
		return Observable.just(PurchaseCouponListIdentifier.builder()
				.withPurchase(purchaseIdentifier).build());
	}
}
