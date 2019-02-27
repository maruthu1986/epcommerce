/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Legacy JAXB DTO of a customer's credit card.
 * Refer to the generic collection of payment methods on {@link CustomerDTO} for the appropriate
 * way of adding credit cards. Use this legacy DTO if you need RETAIN_COLLECTION support.
 * Remember that the card # may not actually be in here.
 */
@XmlRootElement(name = LegacyCreditCardDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
@Deprecated
public class LegacyCreditCardDTO extends CreditCardDTO {
	/** XML root element name. */
	public static final String ROOT_ELEMENT = "card";

	private static final long serialVersionUID = 1L;

}
