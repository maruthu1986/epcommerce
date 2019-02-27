/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.plugin.payment.exceptions;

/**
 * Collection of StructuredErrorMessage message ids for the payment domain.
 * These message ids should be used for localization of error messages on client side.
 */
public final class PaymentMessageIds {
	/**
	 * Payment failed - generic error.
	 */
	public static final String PAYMENT_FAILED = "payment.failed";
	/**
	 * Payment failed - declined by payment processor.
	 */
	public static final String PAYMENT_DECLINED = "payment.declined";
	/**
	 * Payment failed - insufficient funds or daily limit exceeded.
	 */
	public static final String PAYMENT_INSUFFICIENT_FUNDS = "payment.insufficient.funds";
	/**
	 * Payment failed - payment method has expired.
	 */
	public static final String PAYMENT_METHOD_EXPIRED = "payment.method.expired";
	/**
	 * Payment failed - shipping address is not defined.
	 */
	public static final String PAYMENT_METHOD_INVALID = "payment.method.invalid";
	/**
	 * Payment failed - currency not supported.
	 */
	public static final String PAYMENT_CURRENCY_NOT_SUPPORTED = "payment.currency.not.supported";

	private PaymentMessageIds() {
		// private constructor to ensure class can't be instantiated
	}
}
