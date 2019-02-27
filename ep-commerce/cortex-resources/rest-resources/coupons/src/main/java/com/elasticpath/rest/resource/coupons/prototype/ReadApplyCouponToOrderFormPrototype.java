/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.prototype;

import io.reactivex.Single;

import com.elasticpath.rest.definition.coupons.ApplyCouponToOrderFormResource;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Read operation for the CouponForm.
 */
public class ReadApplyCouponToOrderFormPrototype implements ApplyCouponToOrderFormResource.Read {

	@Override
	public Single<CouponEntity> onRead() {
		return Single.just(CouponEntity.builder()
				.withParentId("")
				.withCouponId("")
				.withCode("")
				.withParentType("").build());
	}
}
