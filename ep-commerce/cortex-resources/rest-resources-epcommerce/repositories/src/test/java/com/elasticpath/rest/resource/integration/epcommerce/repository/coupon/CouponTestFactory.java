/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildOrderIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildPurchaseIdentifier;

import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.coupons.CouponinfoIdentifier;
import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Factory methods for building identifiers.
 */
public final class CouponTestFactory {

	private CouponTestFactory() {
	}

	/**
	 * Creates a coupon info identifier with a linked order identifier with the given order id and scope.
	 *
	 * @param orderId the order id
	 * @param scope   the scope
	 * @return the coupon info identifier.
	 */
	public static CouponinfoIdentifier buildCouponinfoIdentifier(final String scope, final String orderId) {
		return CouponinfoIdentifier.builder()
				.withOrder(buildOrderIdentifier(scope, orderId))
				.build();
	}

	/**
	 * Creates an order coupon identifier with the given coupon id and a linked order identifier with the provided order id and scope.
	 *
	 * @param couponCode the coupon id
	 * @param orderId    the order id
	 * @param scope      the scope
	 * @return the order coupon identifier.
	 */
	public static OrderCouponIdentifier buildOrderCouponIdentifier(final String couponCode, final String orderId, final String scope) {
		return OrderCouponIdentifier.builder()
				.withCouponId(StringIdentifier.of(couponCode))
				.withOrder(buildOrderIdentifier(scope, orderId))
				.build();
	}

	/**
	 * Creates a coupon entity with the input data.
	 *
	 * @param couponCode the coupon code.
	 * @param parentId   the coupon's parent id.
	 * @param parentType the parent's type.
	 * @return the coupon entity.
	 */
	public static CouponEntity buildCouponEntity(final String couponCode, final String parentId, final String parentType) {
		return CouponEntity.builder()
				.withCode(couponCode)
				.withCouponId(couponCode)
				.withParentId(parentId)
				.withParentType(parentType)
				.build();
	}

	/**
	 * Creates a purchase coupon list identifier, linked to a purchase identifier with the provided scope and purchase id.
	 *
	 * @param scope      the scope
	 * @param purchaseId the purchase id
	 * @return the purchase coupon list identifier.
	 */
	public static PurchaseCouponListIdentifier buildPurchaseCouponListIdentifier(final String scope, final String purchaseId) {
		return PurchaseCouponListIdentifier.builder()
				.withPurchase(buildPurchaseIdentifier(scope, purchaseId))
				.build();
	}
}
