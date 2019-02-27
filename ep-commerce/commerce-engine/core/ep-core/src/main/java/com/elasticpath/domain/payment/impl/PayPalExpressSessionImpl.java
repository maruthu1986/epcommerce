/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import com.elasticpath.domain.payment.PayPalExpressSession;

/**
 * Default implementation of <code>PayPalExpressSession</code>.
 *
 * @deprecated Use DirectPostPaypalExpressPaymentGatewayPluginImpl (which doesn't require this class) instead.
 */
@Deprecated
public class PayPalExpressSessionImpl implements PayPalExpressSession {
	private String token;

	private String emailId;

	private int status;

	/**
	 * Create a new PayPal EC session object with the specified token.
	 * @param token the token id returned from PayPal
	 */
	public PayPalExpressSessionImpl(final String token) {
		super();
		this.token = token;
	}

	/**
	 * @return the emailId
	 */
	@Override
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId the emailId to set
	 */
	@Override
	public void setEmailId(final String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the status
	 */
	@Override
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	@Override
	public void setStatus(final int status) {
		this.status = status;
	}

	/**
	 * @return the token
	 */
	@Override
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	@Override
	public void setToken(final String token) {
		this.token = token;
	}
	
	/**
	 * Clear PayPal EC session information.
	 * Does not alter the currrent "status".
	 */
	@Override
	public void clearSessionInformation() {
		setToken(null);
		setEmailId(null);
	}

}
