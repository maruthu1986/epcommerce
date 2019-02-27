/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.payment.transaction.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * Implementation of the {@link CaptureTransactionResponse}.
 */
public class CaptureTransactionResponseImpl extends PaymentTransactionResponseImpl
		implements CaptureTransactionResponse {

	private MoneyDto money;

	/**
	 * No-args constructor.
	 */
	public CaptureTransactionResponseImpl() {
		//Empty constructor.
	}

	/**
	 * Constructor.
	 *
	 * @param money the {@link com.elasticpath.plugin.payment.dto.MoneyDto} to set on this {@link CaptureTransactionResponseImpl}
	 */
	public CaptureTransactionResponseImpl(final MoneyDto money) {
		this.money = money;
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
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
