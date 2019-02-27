/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Factory for creating {@link OrderPaymentMatcher}s for integration tests.
 */
public class OrderPaymentMatcherFactory {

	private OrderPaymentMatcherFactory() {
		// Prohibit instances of this class being created.
	}

	/**
	 * Gets a successful token capture matcher.
	 *
	 * @return the successful token capture
	 */
	public static OrderPaymentMatcher createSuccessfulTokenCapture() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.CAPTURE_TRANSACTION)
				.build();
	}

	/**
	 * Gets a successful token authorization matcher.
	 *
	 * @return the successful token authorization
	 */
	public static OrderPaymentMatcher createSuccessfulTokenAuthorization() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)
				.build();
	}
	
	/**
	 * Gets a successful token credit matcher.
	 *
	 * @return the successful token credit
	 */
	public static OrderPaymentMatcher createSuccessfulTokenCredit() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.CREDIT_TRANSACTION)
				.build();
	}

	/**
	 * Gets a successful credit card authorization matcher.
	 *
	 * @return the successful credit card authorization matcher
	 */
	public static OrderPaymentMatcher createSuccessfulCreditCardAuthorization() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.CREDITCARD_DIRECT_POST)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)
				.build();
	}

	/**
	 * Gets a failed credit card authorization matcher.
	 *
	 * @return the failed credit card authorization matcher
	 */
	public static OrderPaymentMatcher createFailedCreditCardAuthorization() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.FAILED)
				.withType(PaymentType.CREDITCARD_DIRECT_POST)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)
				.build();
	}

	/**
	 * Gets a successful credit card capture matcher.
	 *
	 * @return the successful credit card capture matcher
	 */
	public static OrderPaymentMatcher createSuccessfulCreditCardCapture() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.CREDITCARD_DIRECT_POST)
				.withTransaction(OrderPayment.CAPTURE_TRANSACTION)
				.build();
	}

	/**
	 * Gets a successful gift certificate capture matcher.
	 *
	 * @return the successful gift certificate capture
	 */
	public static OrderPaymentMatcher createSuccessfulGiftCertificateCapture() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.GIFT_CERTIFICATE)
				.withTransaction(OrderPayment.CAPTURE_TRANSACTION)
				.build();
	}
	
	/**
	 * Gets a successful gift certificate authorization matcher.
	 *
	 * @return the successful gift certificate authorization
	 */
	public static OrderPaymentMatcher createSuccessfulGiftCertificateAuthorization() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.APPROVED)
				.withType(PaymentType.GIFT_CERTIFICATE)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)
				.build();
	}

	/**
	 * Gets a failed token capture matcher.
	 *
	 * @return the failed token capture
	 */
	public static OrderPaymentMatcher createFailedTokenAuthorization() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.FAILED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.AUTHORIZATION_TRANSACTION)
				.build();
	}

	/**
	 * Gets a failed token capture matcher.
	 *
	 * @return the failed token capture
	 */
	public static OrderPaymentMatcher createFailedTokenCapture() {
		return OrderPaymentMatcher.builder()
				.withStatus(OrderPaymentStatus.FAILED)
				.withType(PaymentType.PAYMENT_TOKEN)
				.withTransaction(OrderPayment.CAPTURE_TRANSACTION)
				.build();
	}
}
