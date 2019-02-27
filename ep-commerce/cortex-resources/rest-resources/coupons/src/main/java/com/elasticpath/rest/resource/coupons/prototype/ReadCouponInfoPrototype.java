/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.definition.coupons.CouponinfoResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Read operation for CouponInfo.
 */
public class ReadCouponInfoPrototype implements CouponinfoResource.Read {

	private static final String COUPON_INFO_NAME = "coupon-info";
	private final CouponinfoIdentifier couponinfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param couponinfoIdentifier coupon info identifier
	 */
	@Inject
	public ReadCouponInfoPrototype(@RequestIdentifier final CouponinfoIdentifier couponinfoIdentifier) {
		this.couponinfoIdentifier = couponinfoIdentifier;
	}

	@Override
	public Single<InfoEntity> onRead() {
		String orderId = couponinfoIdentifier.getOrder().getOrderId().getValue();

		return Single.just(InfoEntity.builder()
				.withName(COUPON_INFO_NAME)
				.withInfoId(orderId)
				.build());
	}
}
