/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.relationship.linkfrom;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.definition.coupons.CouponsAppliedToOrderRelationship;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Reverse relationship form CouponInfo to the Order.
 */
public class CouponsToOrderRelationshipImpl implements CouponsAppliedToOrderRelationship.LinkFrom {

	private final CouponinfoIdentifier couponinfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param couponinfoIdentifier coupon info identifier
	 */
	@Inject
	public CouponsToOrderRelationshipImpl(@RequestIdentifier final CouponinfoIdentifier couponinfoIdentifier) {
		this.couponinfoIdentifier = couponinfoIdentifier;
	}

	@Override
	public Observable<OrderIdentifier> onLinkFrom() {
		return Observable.just(couponinfoIdentifier.getOrder());
	}
}
