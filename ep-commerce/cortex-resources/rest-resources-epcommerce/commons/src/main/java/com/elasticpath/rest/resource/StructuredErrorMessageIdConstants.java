/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource;

/**
 * Messages class for structured error message IDs.
 */
public final class StructuredErrorMessageIdConstants {

	/**
	 * Structured error message ID indicating missing billing address.
	 */
	public static final String ID_NEED_BILLING_ADDRESS = "need.billing.address";

	/**
	 * Structured error message ID indicating missing email address.
	 */
	public static final String NEED_EMAIL = "need.email";

	/**
	 * Structured error message ID indicating missing payment method.
	 */
	public static final String NEED_PAYMENT_METHOD = "need.payment.method";

	/**
	 * Structured error message ID indicating missing shipment details.
	 */
	public static final String NEED_SHIPMENT_DETAILS = "need.shipment.details";

	/**
	 * Structured error message ID indicating invalid integer field.
	 */
	public static final String INVALID_INTEGER_FIELD = "field.invalid.integer.format";

	/**
	 * Structured error message ID indicating invalid minimum value field.
	 */
	public static final String INVALID_MINIMUM_VALUE_FIELD = "field.invalid.minimum.value";

	/**
	 * Structured error message ID indicating cart item is not available.
	 */
	public static final String CART_ITEM_NOT_AVAILABLE = "cart.item.not.available";

	/**
	 * Structured error message ID indicating the cart is not purchasable.
	 */
	public static final String CART_NOT_PURCHASABLE = "purchase.cart.not.purchasable";

	/**
	 * Structured error message ID indicating the addresses data policy is not accepted.
	 */
	public static final String NEED_DATA_POLICY_CONSENT = "need.datapolicy.consent";
}
