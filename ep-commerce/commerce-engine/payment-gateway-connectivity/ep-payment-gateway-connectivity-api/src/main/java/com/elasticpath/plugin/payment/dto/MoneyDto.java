/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.payment.dto;

import java.math.BigDecimal;

/**
 * Represents a specific monetary amount, with currency.
 */
public interface MoneyDto {

	/**
	 * Get the amount of this payment.
	 *
	 * @return the amount as a BigDecimal
	 */
	BigDecimal getAmount();

	/**
	 * Set the amount of this payment.
	 *
	 * @param amount the amount as a BigDecimal
	 */
	void setAmount(BigDecimal amount);

	/**
	 * Get the currency code (e.g. CAD or USD).
	 *
	 * @return the currency code
	 */
	String getCurrencyCode();

	/**
	 * Set the currency code.
	 *
	 * @param currencyCode the currency code code
	 */
	void setCurrencyCode(String currencyCode);
}
