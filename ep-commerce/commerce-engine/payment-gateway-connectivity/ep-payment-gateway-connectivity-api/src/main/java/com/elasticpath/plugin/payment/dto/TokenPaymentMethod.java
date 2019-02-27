/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.payment.dto;

/**
 * Represents a token payment method.
 */
public interface TokenPaymentMethod extends PaymentMethod {
	/**
	 * Gets the token value as a String.
	 *
	 * @return the token value
	 */
	String getValue();

	/**
	 * Sets the token value as a String.
	 *
	 * @param value the token value
	 */
	void setValue(String value);
}
