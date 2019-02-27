/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO of a customer's credit card. Remember that the card # may not actually be in here.
 *
 * NOTE: The default credit card boolean value on this DTO is ignored. To set the default,
 * set the default payment method on the {@link DefaultPaymentMethodDTO} on the {@link CustomerDTO}.
 * Moving the default credit card boolean value on to the {@link LegacyCreditCardDTO} was attempted
 * but the following error results from the xsd schema validation:
 * An all model group must appear in a particle with {min occurs} = {max occurs} = 1,
 * and that particle must be part of a pair which constitutes the {content type} of a complex type definition.
 */
@XmlRootElement(name = CreditCardDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
@Deprecated
public class CreditCardDTO extends PaymentMethodDto implements Comparable<CreditCardDTO>, Dto {
	/** XML root element name. */
	public static final String ROOT_ELEMENT = "credit_card";

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "type")
	private String cardType;

	@XmlAttribute(name = "expiry_year")
	private String expiryYear;

	@XmlAttribute(name = "expiry_month")
	private String expiryMonth;

	@XmlAttribute(name = "guid")
	private String guid;

	@XmlElement(name = "holder_name")
	private String cardHolderName;

	@XmlElement(name = "card_number")
	private String cardNumber;

	@XmlElement(name = "start_year")
	private String startYear;

	@XmlElement(name = "start_month")
	private String startMonth;

	@XmlElement(name = "issue_number")
	private Integer issueNumber;

	@XmlElement(name = "default_card")
	private Boolean defaultCard;

	/**
	 * This is a legacy element to allow backwards compatibility with old version of the import/export Customer XML file.
	 * Billing address was unused and has been removed from credit cards.
	 */
	@SuppressWarnings("PMD.UnusedPrivateField")
	@XmlElement(name = "billing_address_guid")
	private String billingAddressGuid;
	
	public String getCardType() {
		return cardType;
	}

	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(final String holderName) {
		this.cardHolderName = holderName;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(final String startYear) {
		this.startYear = startYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(final String startMonth) {
		this.startMonth = startMonth;
	}

	public Integer getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(final Integer issueNumber) {
		this.issueNumber = issueNumber;
	}

	public Boolean isDefaultCard() {
		return defaultCard;
	}

	public void setDefaultCard(final Boolean defaultCard) {
		this.defaultCard = defaultCard;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Compares CreditCardDTOs via their GUIDs.
	 *
	 * @param otherCreditCardDTO The other CreditCardDTO.
	 * @return Negative int if this DTO's GUID is less than the given DTO's GUID, zero if they are the same, etc.
	 */
	public int compareTo(final CreditCardDTO otherCreditCardDTO) {
		if (otherCreditCardDTO == null) {
			throw new IllegalArgumentException();
		}
		return getGuid().compareTo(otherCreditCardDTO.getGuid());
	}

	@Override
	public boolean equals(final Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
