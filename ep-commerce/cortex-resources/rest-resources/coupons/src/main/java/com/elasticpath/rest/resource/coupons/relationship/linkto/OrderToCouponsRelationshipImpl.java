/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.relationship.linkto;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.definition.coupons.CouponsAppliedToOrderRelationship;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Order to Coupons relationship.
 */
public class OrderToCouponsRelationshipImpl implements CouponsAppliedToOrderRelationship.LinkTo {

	private final OrderIdentifier orderIdentifier;

	/**
	 * Constructor.
	 *
	 * @param orderIdentifier orderIdentifier
	 */
	@Inject
	public OrderToCouponsRelationshipImpl(@RequestIdentifier final OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	@Override
	public Observable<CouponinfoIdentifier> onLinkTo() {
		return Observable.just(CouponinfoIdentifier.builder()
				.withOrder(orderIdentifier).build());
	}
}
