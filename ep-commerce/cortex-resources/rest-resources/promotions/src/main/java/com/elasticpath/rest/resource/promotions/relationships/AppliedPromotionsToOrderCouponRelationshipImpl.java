/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.relationships;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForOrderCouponIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionForOrderCouponRelationship;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Adds a coupon link in promotions.
 */
public class AppliedPromotionsToOrderCouponRelationshipImpl implements PromotionForOrderCouponRelationship.LinkFrom {

	private final AppliedPromotionsForOrderCouponIdentifier appliedPromotionsForOrderCouponIdentifier;

	/**
	 * Constructor.
	 *
	 * @param appliedPromotionsForOrderCouponIdentifier	identifier
	 */
	@Inject
	public AppliedPromotionsToOrderCouponRelationshipImpl(@RequestIdentifier final AppliedPromotionsForOrderCouponIdentifier
														   appliedPromotionsForOrderCouponIdentifier) {
		this.appliedPromotionsForOrderCouponIdentifier = appliedPromotionsForOrderCouponIdentifier;
	}

	@Override
	public Observable<OrderCouponIdentifier> onLinkFrom() {
		OrderCouponIdentifier orderCouponIdentifier = appliedPromotionsForOrderCouponIdentifier.getOrderCoupon();
		IdentifierPart<String> couponId = orderCouponIdentifier.getCouponId();
		OrderIdentifier orderIdentifier = orderCouponIdentifier.getOrder();
		return Observable.just(OrderCouponIdentifier.builder()
				.withCouponId(couponId)
				.withOrder(orderIdentifier)
				.build());
	}
}
