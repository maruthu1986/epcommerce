/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.plugin.payment;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Indicates the type of payment gateway. Only one payment gateway of each type can be selected on a store.
 */
public class PaymentGatewayType extends AbstractExtensibleEnum<PaymentGatewayType> implements Comparable<PaymentGatewayType> {
	private static final long serialVersionUID = 50001L;

	/**
	 * Credit card payment gateway type.
	 */
	public static final PaymentGatewayType CREDITCARD = new PaymentGatewayType(1, "CREDITCARD");

	/**
	 * PayPal Express payment gateway type.
	 *
	 * @deprecated Use HOSTED_PAGE_PAYPAL_EXPRESS instead.
	 */
	@Deprecated
	public static final PaymentGatewayType PAYPAL_EXPRESS = new PaymentGatewayType(2, "PAYPAL_EXPRESS");

	/**
	 * Gift Certificate payment gateway type.
	 */
	public static final PaymentGatewayType GIFT_CERTIFICATE = new PaymentGatewayType(3, "GIFT_CERTIFICATE");

	/**
	 * Return and exchanges payment gateway type.
	 */
	public static final PaymentGatewayType RETURN_AND_EXCHANGE = new PaymentGatewayType(5, "RETURN_AND_EXCHANGE");

	/**
	 * Hosted PayPal Express payment gateway type.
	 */
	public static final PaymentGatewayType HOSTED_PAGE = new PaymentGatewayType(6, "HOSTED_PAGE");

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal of the payment gateway type.
	 * @param name the name of the payment gateway type.
	 */
	protected PaymentGatewayType(final int ordinal, final String name) {
		super(ordinal, name, PaymentGatewayType.class);
	}

	/**
	 * Compares this enum with the specified object for order.
	 * @param other other payment gateway type
	 * @return  a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(final PaymentGatewayType other) {
		return this.getOrdinal() - other.getOrdinal();
	}

	/**
	 * PaymentGatewayType from string value.
	 * @param name the name of the PaymentGatewayType
	 * @return the PaymentGatewayType from name.
	 */
	public static PaymentGatewayType valueOf(final String name) {
		return valueOf(name, PaymentGatewayType.class);
	}

	/**
	 * All the available payment gateway types.
	 * @return all the available payment gateway types.
	 */
	public static Collection<PaymentGatewayType> values() {
		return values(PaymentGatewayType.class);
	}

	@Override
	protected Class<PaymentGatewayType> getEnumType() {
		return PaymentGatewayType.class;
	}
}
