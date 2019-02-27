/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.coupons.ApplyCouponToOrderFormIdentifier;
import com.elasticpath.rest.definition.coupons.ApplyCouponToOrderFormResource;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Submit operation for the coupon applied to the order.
 */
public class ApplyCouponToOrderFormPrototype implements ApplyCouponToOrderFormResource.SubmitWithResult {

	private final CouponEntity couponEntity;
	private final ApplyCouponToOrderFormIdentifier formIdentifier;
	private final Repository<CouponEntity, OrderCouponIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param couponEntity coupon entity
	 * @param formIdentifier form identifier
	 * @param repository repo
	 */
	@Inject
	public ApplyCouponToOrderFormPrototype(
			@RequestForm final CouponEntity couponEntity,
			@RequestIdentifier final ApplyCouponToOrderFormIdentifier formIdentifier,
			@ResourceRepository final Repository<CouponEntity, OrderCouponIdentifier> repository) {

		this.couponEntity = couponEntity;
		this.formIdentifier = formIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<SubmitResult<OrderCouponIdentifier>> onSubmitWithResult() {
		IdentifierPart<String> storeCode = StringIdentifier.of(formIdentifier.getOrder().getScope().getValue());
		String orderId = formIdentifier.getOrder().getOrderId().getValue();

		//Mark coupon entity as a coupon for order, by specifying parent type and id
		CouponEntity couponEntityWithOrder = CouponEntity.builderFrom(couponEntity)
				.withParentType(OrdersMediaTypes.ORDER.id())
				.withParentId(orderId).build();

		return repository.submit(couponEntityWithOrder, storeCode);
	}
}
