/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon;

import io.reactivex.Single;

import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.rest.definition.coupons.CouponEntity;

/**
 * Universal builder for coupon entity.
 */
public interface CouponEntityBuilder {

	/**
	 * Builds coupon entity from coupon.
	 *
	 * @param coupon coupon
	 * @param type parent type
	 * @param parentId purchase / order id
	 * @return coupon entity
	 */
	Single<CouponEntity> build(Coupon coupon, String type, String parentId);

	/**
	 * Builds coupon entity from applied coupon.
	 *
	 * @param appliedCoupon applied coupon
	 * @param type parent type
	 * @param parentId purchase / order id
	 * @return coupon entity
	 */
	Single<CouponEntity> build(AppliedCoupon appliedCoupon, String type, String parentId);
}