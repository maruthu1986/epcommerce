/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer.builder;

import com.elasticpath.common.dto.customer.CreditCardDTO;

/**
 * {@link CreditCardDTO} builder.
 *
 * @param <E> Subtype of {@link CreditCardDTO} used to build and create
 */
public abstract class AbstractCreditCardDTOBuilder<E extends CreditCardDTO> {
	private String cardType;
	private String expiryYear;
	private String expiryMonth;
	private String guid;
	private String cardHolderName;
	private String cardNumber;
	private String startYear;
	private String startMonth;
	private Integer issueNumber;

	/**
	 * Sets card type.
	 *
	 * @param cardType the card type
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder}
	 */
	public AbstractCreditCardDTOBuilder<E> withCardType(final String cardType) {
		this.cardType = cardType;
		return this;
	}

	/**
	 * Sets expiry year.
	 *
	 * @param expiryYear the expiry year
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder}
	 */
	public AbstractCreditCardDTOBuilder<E> withExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
		return this;
	}

	/**
	 * Sets the expiry month.
	 *
	 * @param expiryMonth the expiry month
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder<E>}
	 */
	public AbstractCreditCardDTOBuilder<E> withExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
		return this;
	}

	/**
	 * Sets the guid.
	 *
	 * @param guid the guid
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder<E>}
	 */
	public AbstractCreditCardDTOBuilder<E> withGuid(final String guid) {
		this.guid = guid;
		return this;
	}

	/**
	 * Sets the cardholder name.
	 *
	 * @param cardHolderName the card holder name
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder<E>}
	 */
	public AbstractCreditCardDTOBuilder<E> withCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
		return this;
	}

	/**
	 * Sets the card number.
	 *
	 * @param cardNumber the card number
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder<E>}
	 */
	public AbstractCreditCardDTOBuilder<E> withCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
		return this;
	}

	/**
	 * Sets the start year.
	 *
	 * @param startYear the start year
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder<E>}
	 */
	public AbstractCreditCardDTOBuilder<E> withStartYear(final String startYear) {
		this.startYear = startYear;
		return this;
	}

	/**
	 * Sets the start month.
	 *
	 * @param startMonth the start month
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder<E>}
	 */
	public AbstractCreditCardDTOBuilder<E> withStartMonth(final String startMonth) {
		this.startMonth = startMonth;
		return this;
	}

	/**
	 * Sets the issue number.
	 *
	 * @param issueNumber the issue number
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder<E>}
	 */
	public AbstractCreditCardDTOBuilder<E> withIssueNumber(final Integer issueNumber) {
		this.issueNumber = issueNumber;
		return this;
	}

	/**
	 * Builds the {@link com.elasticpath.common.dto.customer.CreditCardDTO}.
	 *
	 * @return this {@link com.elasticpath.common.dto.customer.builder.AbstractCreditCardDTOBuilder}
	 */
	public E build() {
		E creditCardDTO = create();
		creditCardDTO.setCardType(cardType);
		creditCardDTO.setExpiryYear(expiryYear);
		creditCardDTO.setExpiryMonth(expiryMonth);
		creditCardDTO.setGuid(guid);
		creditCardDTO.setCardHolderName(cardHolderName);
		creditCardDTO.setCardNumber(cardNumber);
		creditCardDTO.setStartYear(startYear);
		creditCardDTO.setStartMonth(startMonth);
		creditCardDTO.setIssueNumber(issueNumber);
		return creditCardDTO;
	}

	/**
	 * Creates a new instance of a the {@link CreditCardDTO} for building.
	 *
	 * @return new instance of {@link CreditCardDTO}
	 */
	public abstract E create();
}
