/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * JAXB DTO of a customer's payment token.
 */
@XmlRootElement(name = PaymentTokenDto.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class PaymentTokenDto extends PaymentMethodDto {
	private static final long serialVersionUID = 1L;

	/** Root XML element name. */
	public static final String ROOT_ELEMENT = "payment_token";

	@XmlElement(name = "payment_token_display_value")
	private String paymentTokenDisplayValue;

	@XmlElement(name = "payment_token_value")
	private String paymentTokenValue;

	public String getPaymentTokenDisplayValue() {
		return paymentTokenDisplayValue;
	}

	public void setPaymentTokenDisplayValue(final String paymentTokenDisplayValue) {
		this.paymentTokenDisplayValue = paymentTokenDisplayValue;
	}

	public String getPaymentTokenValue() {
		return paymentTokenValue;
	}

	public void setPaymentTokenValue(final String paymentTokenValue) {
		this.paymentTokenValue = paymentTokenValue;
	}

	@Override
	public boolean equals(final Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
