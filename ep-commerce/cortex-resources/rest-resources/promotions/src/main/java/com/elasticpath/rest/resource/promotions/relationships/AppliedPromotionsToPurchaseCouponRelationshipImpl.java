/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionForPurchaseCouponRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a coupon link in promotions.
 */
public class AppliedPromotionsToPurchaseCouponRelationshipImpl implements PromotionForPurchaseCouponRelationship.LinkFrom {

	private final AppliedPromotionsForPurchaseCouponIdentifier appliedPromotionsForPurchaseCouponIdentifier;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForPurchaseCouponIdentifier	identifier
	 */
	@Inject
	public AppliedPromotionsToPurchaseCouponRelationshipImpl(@RequestIdentifier final AppliedPromotionsForPurchaseCouponIdentifier
																  appliedPromotionsForPurchaseCouponIdentifier) {
		this.appliedPromotionsForPurchaseCouponIdentifier = appliedPromotionsForPurchaseCouponIdentifier;
	}

	@Override
	public Observable<PurchaseCouponIdentifier> onLinkFrom() {
		PurchaseCouponIdentifier purchaseCouponIdentifier = appliedPromotionsForPurchaseCouponIdentifier.getPurchaseCoupon();
		IdentifierPart<String> couponId = purchaseCouponIdentifier.getCouponId();
		PurchaseCouponListIdentifier purchaseCouponListIdentifier =  purchaseCouponIdentifier.getPurchaseCouponList();
		return Observable.just(PurchaseCouponIdentifier.builder()
				.withCouponId(couponId)
				.withPurchaseCouponList(purchaseCouponListIdentifier)
				.build());
	}
}
