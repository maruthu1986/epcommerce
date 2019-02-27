package com.elasticpath.selenium.domainobjects;

/**
 * Cart Item Modifier Group Field.
 */
public class CartItemModiferGroupField {
	private String fieldCode;
	private String fieldName;
	private String fieldType;
	private String shortTextSize;
	private String optionValue;
	private String optionName;
	private boolean isRequired;

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(final String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(final String fieldType) {
		this.fieldType = fieldType;
	}

	public String getShortTextSize() {
		return shortTextSize;
	}

	public void setShortTextSize(final String shortTextSize) {
		this.shortTextSize = shortTextSize;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(final String optionValue) {
		this.optionValue = optionValue;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(final String optionName) {
		this.optionName = optionName;
	}

	public boolean isFieldRequired() {
		return isRequired;
	}

	public void setRequired(final boolean required) {
		isRequired = required;
	}
}
