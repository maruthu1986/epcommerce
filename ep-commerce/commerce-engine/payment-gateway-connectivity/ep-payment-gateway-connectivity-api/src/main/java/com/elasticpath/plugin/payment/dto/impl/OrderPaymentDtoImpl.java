/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.plugin.payment.dto.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.OrderPaymentDto;

/**
 * Implementation of {@link OrderPaymentDto}. Used in Payment Gateways.
 */
public class OrderPaymentDtoImpl implements OrderPaymentDto {
	
	private BigDecimal amount;

	private String authorizationCode;

	private String referenceId;

	private String requestToken;

	private String currencyCode;

	private String email;

	private String token;

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String getReferenceId() {
		return referenceId;
	}

	@Override
	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public String getRequestToken() {
		return requestToken;
	}

	@Override
	public void setRequestToken(final String requestToken) {
		this.requestToken = requestToken;
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
	public String getCurrencyCode() {
		return currencyCode;
	}

	@Override
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public void setValue(final String token) {
		this.token = token;
	}

	@Override
	public String getValue() {
		return token;
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
