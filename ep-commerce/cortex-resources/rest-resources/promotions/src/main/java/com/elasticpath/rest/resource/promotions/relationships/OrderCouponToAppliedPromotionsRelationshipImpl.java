/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForOrderCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionForOrderCouponRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Adds a coupon link to promotions.
 */
public class OrderCouponToAppliedPromotionsRelationshipImpl implements PromotionForOrderCouponRelationship.LinkTo {

	private final OrderCouponIdentifier orderCouponIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderCouponIdentifier	identifier
	 */
	@Inject
	public OrderCouponToAppliedPromotionsRelationshipImpl(@RequestIdentifier final OrderCouponIdentifier orderCouponIdentifier) {
		this.orderCouponIdentifier = orderCouponIdentifier;
	}

	@Override
	public Observable<AppliedPromotionsForOrderCouponIdentifier> onLinkTo() {
		return Observable.just(AppliedPromotionsForOrderCouponIdentifier.builder()
				.withOrderCoupon(orderCouponIdentifier)
				.build());
	}
}
