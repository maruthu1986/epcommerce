/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionForPurchaseCouponRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a promotions link in coupon.
 */
public class PurchaseCouponToPromotionsRelationshipImpl implements PromotionForPurchaseCouponRelationship.LinkTo {

	private final PurchaseCouponIdentifier purchaseCouponIdentifier;

	/**
	 * Constructor.
	 *
	 * @param purchaseCouponIdentifier	identifier
	 */
	@Inject
	public PurchaseCouponToPromotionsRelationshipImpl(@RequestIdentifier final PurchaseCouponIdentifier purchaseCouponIdentifier) {
		this.purchaseCouponIdentifier = purchaseCouponIdentifier;
	}

	@Override
	public Observable<AppliedPromotionsForPurchaseCouponIdentifier> onLinkTo() {
		return Observable.just(AppliedPromotionsForPurchaseCouponIdentifier.builder()
				.withPurchaseCoupon(purchaseCouponIdentifier)
				.build());
	}
}
