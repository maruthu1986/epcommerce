/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.relationship.linkto;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.ApplyCouponFormFromCouponinfoRelationship;
import com.elasticpath.rest.definition.coupons.ApplyCouponToOrderFormIdentifier;
import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Relationship of CouponInfo to the CouponForm (which is applied to order).
 */
public class ApplyCouponFormFromCouponinfoRelationshipImpl implements ApplyCouponFormFromCouponinfoRelationship.LinkTo {

	private final CouponinfoIdentifier couponinfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param couponinfoIdentifier coupon info identifier
	 */
	@Inject
	public ApplyCouponFormFromCouponinfoRelationshipImpl(@RequestIdentifier final CouponinfoIdentifier couponinfoIdentifier) {
		this.couponinfoIdentifier = couponinfoIdentifier;
	}

	@Override
	public Observable<ApplyCouponToOrderFormIdentifier> onLinkTo() {
		return Observable.just(ApplyCouponToOrderFormIdentifier.builder()
				.withOrder(couponinfoIdentifier.getOrder()).build());
	}
}
