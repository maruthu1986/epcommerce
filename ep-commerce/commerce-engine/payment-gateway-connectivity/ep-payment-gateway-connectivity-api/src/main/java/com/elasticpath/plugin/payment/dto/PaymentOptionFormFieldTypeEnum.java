/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.dto;

/**
 * Enum for defining the type of payment form field.
 */
public enum PaymentOptionFormFieldTypeEnum {
	/** Text input field. */
	TEXT,

	/** Numeric-only input field. */
	NUMBER,

	/** Checkbox input field. */
	CHECKBOX,

	/** Radio input field. */
	RADIO,

	/** Selection field. */
	SELECT,

	/** Label (read-only). */
	LABEL,

	/** Special field: Card type. */
	SPECIAL_CARD_TYPE,

	/**
	 * Special field: Expiry date.
	 * Expiry date format is defined by fieldFormat field.
	 */
	SPECIAL_EXPIRY
}
