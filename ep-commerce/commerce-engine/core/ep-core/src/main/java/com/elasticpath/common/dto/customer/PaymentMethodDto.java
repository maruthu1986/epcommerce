/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO of a generic payment method.
 */
@XmlSeeAlso({PaymentTokenDto.class, CreditCardDTO.class})
public class PaymentMethodDto implements Dto {
	private static final long serialVersionUID = 1L;
}
