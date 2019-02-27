/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.payment.transaction.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;

/**
 * Implementation of the {@link CaptureTransactionRequest}.
 */
public class CaptureTransactionRequestImpl extends PaymentTransactionRequestImpl 
			implements CaptureTransactionRequest {
	
	private String requestToken;
	private MoneyDto money;
	private String authorizationCode;

	@Override
	public String getRequestToken() {
		return requestToken;
	}

	@Override
	public void setRequestToken(final String requestToken) {
		this.requestToken = requestToken;
	}

	@Override
	public MoneyDto getMoney() {
		return money;
	}

	@Override
	public void setMoney(final MoneyDto money) {
		this.money = money;
	}
	
	@Override
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	@Override
	public void setAuthorizationCode(final String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
