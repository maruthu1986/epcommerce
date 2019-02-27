/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer.builder;

import com.elasticpath.common.dto.customer.PaymentTokenDto;

/**
 * {@link PaymentTokenDto} builder.
 */
public class PaymentTokenDTOBuilder {
	private String paymentTokenDisplayValue;
	private String paymentTokenValue;

	/**
	 * Sets the payment token display value.
	 *
	 * @param paymentTokenDisplayValue the payment token display value
	 * @return this {@link PaymentTokenDTOBuilder}
	 */
	public PaymentTokenDTOBuilder withPaymentTokenDisplayValue(final String paymentTokenDisplayValue) {
		this.paymentTokenDisplayValue = paymentTokenDisplayValue;
		return this;
	}

	/**
	 * Sets the payment token value.
	 *
	 * @param paymentTokenValue the payment token value
	 * @return this {@link PaymentTokenDTOBuilder}
	 */
	public PaymentTokenDTOBuilder withPaymentTokenValue(final String paymentTokenValue) {
		this.paymentTokenValue = paymentTokenValue;
		return this;
	}

	/**
	 * Builds the {@link PaymentTokenDto}.
	 *
	 * @return the {@link PaymentTokenDto}
	 */
	public PaymentTokenDto build() {
		PaymentTokenDto paymentTokenDto = new PaymentTokenDto();
		paymentTokenDto.setPaymentTokenDisplayValue(paymentTokenDisplayValue);
		paymentTokenDto.setPaymentTokenValue(paymentTokenValue);
		return paymentTokenDto;
	}
}
