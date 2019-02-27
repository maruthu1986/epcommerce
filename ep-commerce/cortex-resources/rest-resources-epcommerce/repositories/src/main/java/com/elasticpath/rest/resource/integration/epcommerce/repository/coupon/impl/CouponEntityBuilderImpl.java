/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponEntityBuilder;

/**
 * Implementation of coupon entity builder.
 */
@Component
public class CouponEntityBuilderImpl implements CouponEntityBuilder {

	@Override
	public Single<CouponEntity> build(final Coupon coupon, final String type, final String parentId) {
		return Single.just(CouponEntity.builder()
				.withParentType(type)
				.withParentId(parentId)
				.withCode(coupon.getCouponCode())
				.withCouponId(coupon.getCouponCode())
				.build());
	}

	@Override
	public Single<CouponEntity> build(final AppliedCoupon appliedCoupon, final String type, final String parentId) {
		return Single.just(CouponEntity.builder()
				.withParentType(type)
				.withParentId(parentId)
				.withCode(appliedCoupon.getCouponCode())
				.withCouponId(appliedCoupon.getCouponCode())
				.build());
	}
}
