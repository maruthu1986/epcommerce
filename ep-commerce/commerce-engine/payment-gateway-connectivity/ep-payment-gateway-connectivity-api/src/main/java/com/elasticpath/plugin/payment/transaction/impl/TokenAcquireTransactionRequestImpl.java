/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.transaction.impl;

import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionRequest;

/**
 * Represents a request for a payment token.
 */
public class TokenAcquireTransactionRequestImpl implements TokenAcquireTransactionRequest {
	private String referenceId;
	private String currencyCode;
	private String ipAddress;
	private String email;

	@Override
	public String getReferenceId() {
		return referenceId;
	}

	@Override
	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public String getCurrencyCode() {
		return currencyCode;
	}

	@Override
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(final String email) {
		this.email = email;
	}
}
