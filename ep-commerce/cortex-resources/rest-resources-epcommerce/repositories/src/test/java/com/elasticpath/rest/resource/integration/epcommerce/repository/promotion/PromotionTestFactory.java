/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildCartIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildItemIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildLineItemIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildOrderIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildPurchaseIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.COUPON_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.ORDER_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PROMOTION_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PROMOTION_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SHIPMENT_DETAILS_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SHIPPING_OPTION_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;

import com.elasticpath.rest.definition.coupons.OrderCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponIdentifier;
import com.elasticpath.rest.definition.coupons.PurchaseCouponListIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForCartLineItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForOrderCouponIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseCouponIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseIdentifier;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionIdentifier;
import com.elasticpath.rest.definition.promotions.PurchasePromotionIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Factory methods for building identifiers.
 */
public final class PromotionTestFactory {

	private PromotionTestFactory() {

	}

	/**
	 * Builds an AppliedPromotionsForCartLineItemIdentifier with test data.
	 *
	 * @return AppliedPromotionsForCartLineItemIdentifier
	 */
	public static AppliedPromotionsForCartLineItemIdentifier buildAppliedPromotionsForCartLineItemIdentifier() {
		return AppliedPromotionsForCartLineItemIdentifier.builder()
				.withLineItem(buildLineItemIdentifier(SCOPE, CART_ID, LINE_ITEM_ID))
				.build();
	}

	/**
	 * Builds an AppliedPromotionsForCartIdentifier with test data.
	 *
	 * @return AppliedPromotionsForCartIdentifier
	 */
	public static AppliedPromotionsForCartIdentifier buildAppliedPromotionsForCartIdentifier() {
		return AppliedPromotionsForCartIdentifier.builder()
				.withCart(buildCartIdentifier(SCOPE, CART_ID))
				.build();
	}

	/**
	 * Builds an AppliedPromotionsForItemIdentifier with test data.
	 *
	 * @return AppliedPromotionsForItemIdentifier
	 */
	public static AppliedPromotionsForItemIdentifier buildAppliedPromotionsForItemIdentifier() {
		return AppliedPromotionsForItemIdentifier.builder()
				.withItem(buildItemIdentifier(SCOPE, SKU_CODE))
				.build();
	}


	/**
	 * Builds an OrderCouponIdentifier with test data.
	 *
	 * @return OrderCouponIdentifier
	 */
	public static OrderCouponIdentifier buildOrderCouponIdentifier() {
		return OrderCouponIdentifier.builder()
				.withCouponId(StringIdentifier.of(COUPON_CODE))
				.withOrder(buildOrderIdentifier(SCOPE, ORDER_ID))
				.build();
	}

	/**
	 * Builds an AppliedPromotionsForOrderCouponIdentifier with test data.
	 *
	 * @return AppliedPromotionsForOrderCouponIdentifier
	 */
	public static AppliedPromotionsForOrderCouponIdentifier buildAppliedPromotionsForOrderCouponIdentifier() {
		return AppliedPromotionsForOrderCouponIdentifier.builder()
				.withOrderCoupon(buildOrderCouponIdentifier())
				.build();
	}

	/**
	 * Builds a PurchaseCouponListIdentifier with test data.
	 *
	 * @return PurchaseCouponListIdentifier
	 */
	public static PurchaseCouponListIdentifier buildPurchaseCouponListIdentifier() {
		return PurchaseCouponListIdentifier.builder()
				.withPurchase(buildPurchaseIdentifier(SCOPE, PURCHASE_ID))
				.build();
	}

	/**
	 * Builds a PurchaseCouponIdentifier with test data.
	 *
	 * @return PurchaseCouponIdentifier
	 */
	public static PurchaseCouponIdentifier buildPurchaseCouponIdentifier() {
		return PurchaseCouponIdentifier.builder()
				.withCouponId(StringIdentifier.of(COUPON_CODE))
				.withPurchaseCouponList(buildPurchaseCouponListIdentifier())
				.build();
	}

	/**
	 * Builds an AppliedPromotionsForPurchaseIdentifier with test data.
	 *
	 * @return AppliedPromotionsForPurchaseIdentifier
	 */
	public static AppliedPromotionsForPurchaseIdentifier buildAppliedPromotionsForPurchaseIdentifier() {
		return AppliedPromotionsForPurchaseIdentifier.builder()
				.withPurchase(buildPurchaseIdentifier(SCOPE, PURCHASE_ID))
				.build();
	}

	/**
	 * Builds an AppliedPromotionsForPurchaseCouponIdentifier with test data.
	 *
	 * @return AppliedPromotionsForPurchaseCouponIdentifier
	 */
	public static AppliedPromotionsForPurchaseCouponIdentifier buildAppliedPromotionsForPurchaseCouponIdentifier() {
		return AppliedPromotionsForPurchaseCouponIdentifier.builder()
				.withPurchaseCoupon(buildPurchaseCouponIdentifier())
				.build();
	}

	/**
	 * Builds a ShippingOptionIdentifier with test data.
	 *
	 * @return ShippingOptionIdentifier
	 */
	public static ShippingOptionIdentifier buildShippingOptionIdentifier() {
		return ShippingOptionIdentifier.builder()
				.withShipmentDetailsId(CompositeIdentifier.of(SHIPMENT_DETAILS_ID))
				.withScope(SCOPE_IDENTIFIER_PART)
				.withShippingOptionId(StringIdentifier.of(SHIPPING_OPTION_ID))
				.build();
	}

	/**
	 * Builds an AppliedPromotionsForShippingOptionIdentifier with test data.
	 *
	 * @return AppliedPromotionsForShippingOptionIdentifier
	 */
	public static AppliedPromotionsForShippingOptionIdentifier buildAppliedPromotionsForShippingOptionIdentifier() {
		return AppliedPromotionsForShippingOptionIdentifier.builder()
				.withShippingOption(buildShippingOptionIdentifier())
				.build();
	}

	/**
	 * Builds a PossiblePromotionsForCartIdentifier with test data.
	 *
	 * @return PossiblePromotionsForCartIdentifier
	 */
	public static PossiblePromotionsForCartIdentifier buildPossiblePromotionsForCartIdentifier() {
		return PossiblePromotionsForCartIdentifier.builder()
				.withCart(buildCartIdentifier(SCOPE, CART_ID))
				.build();
	}

	/**
	 * Builds a PossiblePromotionsForItemIdentifier with test data.
	 *
	 * @return PossiblePromotionsForItemIdentifier
	 */
	public static PossiblePromotionsForItemIdentifier buildPossiblePromotionsForItemIdentifier() {
		return PossiblePromotionsForItemIdentifier.builder()
				.withItem(buildItemIdentifier(SCOPE, SKU_CODE))
				.build();
	}

	/**
	 * Builds a PromotionIdentifier with test data.
	 *
	 * @return PromotionIdentifier
	 */
	public static PromotionIdentifier buildPromotionIdentifier() {
		return PromotionIdentifier.builder()
				.withPromotionId(StringIdentifier.of(PROMOTION_ID))
				.withScope(SCOPE_IDENTIFIER_PART)
				.build();
	}

	/**
	 * Builds a PurchaseCouponPromotionIdentifier with test data.
	 *
	 * @return PurchaseCouponPromotionIdentifier
	 */
	public static PurchaseCouponPromotionIdentifier buildPurchaseCouponPromotionIdentifier() {
		return PurchaseCouponPromotionIdentifier.builder()
				.withPurchaseCoupon(buildPurchaseCouponIdentifier())
				.withPromotionId(PROMOTION_IDENTIFIER_PART)
				.build();
	}

	/**
	 * Builds a PurchasePromotionIdentifier with test data.
	 *
	 * @return PurchasePromotionIdentifier
	 */
	public static PurchasePromotionIdentifier buildPurchasePromotionIdentifier() {
		return PurchasePromotionIdentifier.builder()
				.withPromotionId(PROMOTION_IDENTIFIER_PART)
				.withPurchase(buildPurchaseIdentifier(SCOPE, PURCHASE_ID))
				.build();
	}
}
