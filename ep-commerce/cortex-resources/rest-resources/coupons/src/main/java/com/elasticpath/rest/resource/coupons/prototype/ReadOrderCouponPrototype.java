/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.coupons.OrderCouponResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read operation for the OrderCoupon.
 */
public class ReadOrderCouponPrototype implements OrderCouponResource.Read {

	private final OrderCouponIdentifier orderCouponIdentifier;
	private final Repository<CouponEntity, OrderCouponIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param orderCouponIdentifier order coupon identifier
	 * @param repository repo
	 */
	@Inject
	public ReadOrderCouponPrototype(@RequestIdentifier final OrderCouponIdentifier orderCouponIdentifier,
									@ResourceRepository final Repository<CouponEntity, OrderCouponIdentifier> repository) {
		this.orderCouponIdentifier = orderCouponIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<CouponEntity> onRead() {
		return repository.findOne(orderCouponIdentifier);
	}
}
