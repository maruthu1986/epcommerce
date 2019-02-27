/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.relationship.linkto;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.coupons.CouponFromCouponinfoRelationship;
import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Coupon from Couponinfo relationship.
 */
public class CouponFromCouponinfoRelationshipImpl implements CouponFromCouponinfoRelationship.LinkTo {

	private final CouponinfoIdentifier couponinfoIdentifier;
	private final LinksRepository<CouponinfoIdentifier, OrderCouponIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param couponinfoIdentifier coupon info identifier
	 * @param repository repo
	 */
	@Inject
	public CouponFromCouponinfoRelationshipImpl(@RequestIdentifier final CouponinfoIdentifier couponinfoIdentifier,
												@ResourceRepository final LinksRepository<CouponinfoIdentifier, OrderCouponIdentifier> repository) {

		this.couponinfoIdentifier = couponinfoIdentifier;
		this.repository = repository;
	}

	@Override
	public Observable<OrderCouponIdentifier> onLinkTo() {
		return repository.getElements(couponinfoIdentifier);
	}
}
