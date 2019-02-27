/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO of a default payment method.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class DefaultPaymentMethodDTO implements Dto {
	private static final long serialVersionUID = 1L;

	@XmlElementRefs({
			@XmlElementRef(type = PaymentTokenDto.class),
			@XmlElementRef(type = CreditCardDTO.class)
	})
	private PaymentMethodDto defaultPaymentMethod;

	public PaymentMethodDto getPaymentMethod() {
		return defaultPaymentMethod;
	}

	public void setPaymentMethod(final PaymentMethodDto defaultPaymentMethod) {
		this.defaultPaymentMethod = defaultPaymentMethod;
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
