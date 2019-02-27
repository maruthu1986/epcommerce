/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.plugin.payment;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Indicates the type of data contained in an order payment or order payment template.
 */
public class PaymentType extends AbstractExtensibleEnum<PaymentType> implements Comparable<PaymentType> {

	private static final long serialVersionUID = 50002L;

	/**
	 * PayPal Express payment type ordinal.
	 *
	 * @deprecated Use HOSTED_PAGE_PAYPAL_EXPRESS_ORDINAL instead.
	 */
	@Deprecated
	public static final int PAYPAL_EXPRESS_ORDINAL = 2;

	/**
	 * PayPal Express payment type.
	 *
	 * @deprecated Use HOSTED_PAGE_PAYPAL_EXPRESS instead.
	 */
	@Deprecated
	public static final PaymentType PAYPAL_EXPRESS = new PaymentType(PAYPAL_EXPRESS_ORDINAL, "PAYPAL_EXPRESS",
			"paymentType.payPalExpress", PaymentGatewayType.PAYPAL_EXPRESS);

	/**
	 * Gift Certificate payment type ordinal.
	 */
	public static final int GIFT_CERTIFICATE_ORDINAL = 3;

	/**
	 * Gift Certificate payment type.
	 */
	public static final PaymentType GIFT_CERTIFICATE = new PaymentType(GIFT_CERTIFICATE_ORDINAL, "GIFT_CERTIFICATE",
			"paymentType.giftCertificate", PaymentGatewayType.GIFT_CERTIFICATE);

	/**
	 * Return and exchanges payment type ordinal.
	 */
	public static final int RETURN_AND_EXCHANGE_ORDINAL = 5;

	/**
	 * Return and exchanges payment type.
	 */
	public static final PaymentType RETURN_AND_EXCHANGE = new PaymentType(RETURN_AND_EXCHANGE_ORDINAL, "RETURN_AND_EXCHANGE",
			"paymentType.returnAndExchange", PaymentGatewayType.RETURN_AND_EXCHANGE);

	/**
	 * Payment token payment type ordinal.
	 */
	public static final int PAYMENT_TOKEN_ORDINAL = 6;

	/**
	 * Payment token payment type.
	 */
	public static final PaymentType PAYMENT_TOKEN = new PaymentType(PAYMENT_TOKEN_ORDINAL, "PAYMENT_TOKEN",
			"paymentType.payment_token", PaymentGatewayType.CREDITCARD);

	/**
	 * Hosted page payment type ordinal.
	 */
	public static final int HOSTED_PAGE_ORDINAL = 7;

	/**
	 * Hosted page payment type.
	 */
	public static final PaymentType HOSTED_PAGE = new PaymentType(HOSTED_PAGE_ORDINAL, "HOSTED_PAGE",
			"paymentType.hostedPage", PaymentGatewayType.HOSTED_PAGE);

	/**
	 * Credit card direct post payment type ordinal.
	 */
	public static final int CREDITCARD_DIRECT_POST_ORDINAL = 8;

	/**
	 * Credit card direct post payment type.
	 */
	public static final PaymentType CREDITCARD_DIRECT_POST = new PaymentType(CREDITCARD_DIRECT_POST_ORDINAL, "CREDITCARD_DIRECT_POST",
			"paymentType.creditCardDirectPost", PaymentGatewayType.CREDITCARD);

	private final String propertyKey;
	private final PaymentGatewayType paymentGatewayType;

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal of the payment type.
	 * @param name the name of the payment type.
	 * @param propertyKey the property key of the payment type
	 * @param paymentGatewayType the payment gateway type to use for this payment type
	 */
	protected PaymentType(final int ordinal, final String name, final String propertyKey, final PaymentGatewayType paymentGatewayType) {
		super(ordinal, name, PaymentType.class);
		this.propertyKey = propertyKey;
		this.paymentGatewayType = paymentGatewayType;
	}

	/**
	 * Compares this enum with the specified object for order.
	 * @param other other payment type
	 * @return  a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(final PaymentType other) {
		return this.getOrdinal() - other.getOrdinal();
	}

	/**
	 * PaymentType from string value.
	 * @param name the name of the paymentType
	 * @return the paymentType from name.
	 */
	public static PaymentType valueOf(final String name) {
		return valueOf(name, PaymentType.class);
	}

	/**
	 * all the available payment types.
	 * @return all the available payment types.
	 */
	public static Collection<PaymentType> values() {
		return values(PaymentType.class);
	}

	@Override
	protected Class<PaymentType> getEnumType() {
		return PaymentType.class;
	}

	/**
	 * Get the localization property key.
	 *
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}

	/**
	 * The payment gateway type to use for processing order payments of this type.
	 * @return the payment gateway type
	 */
	public PaymentGatewayType getPaymentGatewayType() {
		return paymentGatewayType;
	}
}
