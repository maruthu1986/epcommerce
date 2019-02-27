/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.prototype;

import javax.inject.Inject;

import io.reactivex.Completable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.coupons.OrderCouponResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Delete coupon for the order.
 */
public class DeleteOrderCouponPrototype implements OrderCouponResource.Delete {

	private final OrderCouponIdentifier orderCouponIdentifier;
	private final Repository<CouponEntity, OrderCouponIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param orderCouponIdentifier order coupon identifier
	 * @param repository repo
	 */
	@Inject
	public DeleteOrderCouponPrototype(@RequestIdentifier final OrderCouponIdentifier orderCouponIdentifier,
									  @ResourceRepository final Repository<CouponEntity, OrderCouponIdentifier> repository) {
		this.orderCouponIdentifier = orderCouponIdentifier;
		this.repository = repository;
	}

	@Override
	public Completable onDelete() {
		return repository.delete(orderCouponIdentifier);
	}
}
