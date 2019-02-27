/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.transaction.impl;

import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionResponse;

/**
 * Represents the response from a token acquire request.
 */
public class TokenAcquireTransactionResponseImpl implements TokenAcquireTransactionResponse {
	private String paymentToken;
	private String displayValue;

	@Override
	public String getPaymentToken() {
		return paymentToken;
	}

	@Override
	public void setPaymentToken(final String paymentToken) {
		this.paymentToken = paymentToken;
	}

	@Override
	public String getDisplayValue() {
		return displayValue;
	}

	@Override
	public void setDisplayValue(final String displayValue) {
		this.displayValue = displayValue;
	}
}
