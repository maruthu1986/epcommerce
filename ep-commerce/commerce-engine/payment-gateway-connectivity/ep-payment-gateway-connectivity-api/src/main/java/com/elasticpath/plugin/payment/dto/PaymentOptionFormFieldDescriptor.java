/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.dto;

import java.util.Map;

/**
 * Represents the data needed to construct the html input field within the form that will post directly to the payment
 * gateway.
 */
public class PaymentOptionFormFieldDescriptor {

	private String fieldId;
	private PaymentOptionFormFieldTypeEnum type;
	private String fieldFormat;
	private String defaultValue;
	private boolean autoCompleteAllowed;
	private boolean required;
	private Integer minLength;
	private Integer maxLength;
	private Map<String, String> optionsMap;

	/**
	 * The id to use for the field inside the html form.
	 *
	 * @return the field id
	 */
	public String getFieldId() {
		return fieldId;
	}

	/**
	 * The id to use for the field inside the html form.
	 *
	 * @param fieldId the field id
	 */
	void setFieldId(final String fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * The HTML input field type.
	 *
	 * @return the field type
	 */
	public PaymentOptionFormFieldTypeEnum getType() {
		return type;
	}

	/**
	 * The HTML input field type.
	 *
	 * @param type the field type
	 */
	void setType(final PaymentOptionFormFieldTypeEnum type) {
		this.type = type;
	}

	/**
	 * The format to use for certain special form field types.
	 * For SPECIAL_EXPIRY, uses a subset of SimpleDateFormat patterns. Specifically, MM for month,
	 * YYYY for 4-digit year, YY for 2-digit year.
	 *
	 * @return the field format
	 */
	public String getFieldFormat() {
		return fieldFormat;
	}

	/**
	 * The format to use for certain special form field types.
	 *
	 * @param fieldFormat the field format
	 */
	void setFieldFormat(final String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	/**
	 * A default value for the field (or the label).
	 *
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * A default value for the field (or the label).
	 *
	 * @param defaultValue the default value
	 */
	void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Indicates whether autocomplete is allowed for the field.  Defaults to true.
	 *
	 * @return false if autocomplete is not allowed
	 */
	public boolean isAutoCompleteAllowed() {
		return autoCompleteAllowed;
	}

	/**
	 * Indicates whether autocomplete is allowed for the field.
	 *
	 * @param autoCompleteAllowed false if autocomplete is not allowed
	 */
	void setAutoCompleteAllowed(final boolean autoCompleteAllowed) {
		this.autoCompleteAllowed = autoCompleteAllowed;
	}

	/**
	 * Indicates whether population of the field is required or not.
	 *
	 * @return true if value is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Indicates whether population of the field is required or not.
	 *
	 * @param required true if the value is required
	 */
	void setRequired(final boolean required) {
		this.required = required;
	}

	/**
	 * Indicates the minimum number of characters that are required.
	 *
	 * @return the minimum number of characters required
	 */
	public Integer getMinLength() {
		return minLength;
	}

	/**
	 * Indicates the minimum number of characters that are required.
	 *
	 * @param minLength the minimum number of characters required
	 */
	void setMinLength(final Integer minLength) {
		this.minLength = minLength;
	}

	/**
	 * Indicates the maximum number of characters allowed.
	 *
	 * @return the maximum number of characters allowed
	 */
	public Integer getMaxLength() {
		return maxLength;
	}

	/**
	 * Indicates the maximum number of characters allowed.
	 *
	 * @param maxLength the maximum number of characters allowed
	 */
	void setMaxLength(final Integer maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * For "select" type input fields, this represents the list of options available.
	 * The map key represents the option value, and the map value represents the option display text.
	 *
	 * @return map of display text to option value (payment gateway value)
	 */
	public Map<String, String> getOptionsMap() {
		return optionsMap;
	}

	/**
	 * For "select" type input fields, this represents the list of options available.
	 * The map key represents the option value, and the map value represents the option display text.
	 *
	 * @param optionsMap map of display text to option value (payment gateway value)
	 */
	void setOptionsMap(final Map<String, String> optionsMap) {
		this.optionsMap = optionsMap;
	}
}
