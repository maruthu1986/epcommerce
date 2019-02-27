/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.transaction;

/**
 * Represents a request for a payment token.
 */
public interface TokenAcquireTransactionRequest extends PaymentTransactionRequest {
	/**
	 * Get the currency code of the payment method being tokenized.
	 *
	 * @return ISO currency code
	 */
	String getCurrencyCode();

	/**
	 * Sets the currency code of the payment method being tokenized.
	 *
	 * @param currencyCode ISO currency code
	 */
	void setCurrencyCode(String currencyCode);

	/**
	 * Gets the IP address where the request originated.
	 *
	 * @return IP address
	 */
	String getIpAddress();

	/**
	 * Sets the IP address where the request originated.
	 *
	 * @param ipAddress IP address
	 */
	void setIpAddress(String ipAddress);

	/**
	 * Gets the customer's email address.
	 *
	 * @return customer's email address
	 */
	String getEmail();

	/**
	 * Sets the customer's email address.
	 *
	 * @param email customer's email address
	 */
	void setEmail(String email);
}
