/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.transaction;

/**
 * Represents the response from a token acquire request.
 */
public interface TokenAcquireTransactionResponse {
	/**
	 * Gets the payment token.
	 *
	 * @return payment token
	 */
	String getPaymentToken();

	/**
	 * Sets the payment token.
	 *
	 * @param paymentToken payment token
	 */
	void setPaymentToken(String paymentToken);

	/**
	 * Gets a display value that allows the customer to identify the payment method for future transactions.
	 *
	 * @return display value
	 */
	String getDisplayValue();

	/**
	 * Sets a display value that allows the customer to identify the payment method for future transactions.
	 *
	 * @param displayValue display value
	 */
	void setDisplayValue(String displayValue);
}
