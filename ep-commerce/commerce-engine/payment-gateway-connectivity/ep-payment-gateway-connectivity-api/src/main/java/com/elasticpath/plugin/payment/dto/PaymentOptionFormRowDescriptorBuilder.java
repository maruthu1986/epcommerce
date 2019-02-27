/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builder for PaymentOptionFormRowDescriptor. 
 */
public class PaymentOptionFormRowDescriptorBuilder {
	private final String messageKey;
	private final List<PaymentOptionFormFieldDescriptor> visibleFields = new ArrayList<>();

	/**
	 * Construct PaymentOptionFormRowDescriptorBuilder with messageKey for the row description.
	 * @param messageKey for the row description
	 */
	public PaymentOptionFormRowDescriptorBuilder(final String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * Add a visible label to the row.
	 * @param label label for the row
	 * @return builder
	 */
	public PaymentOptionFormRowDescriptorBuilder withLabelField(final String label) {
		PaymentOptionFormFieldDescriptor paymentOptionFormFieldDescriptor = instantiatePaymentOptionFormFieldDescriptor();
		paymentOptionFormFieldDescriptor.setType(PaymentOptionFormFieldTypeEnum.LABEL);
		paymentOptionFormFieldDescriptor.setDefaultValue(label);
		this.visibleFields.add(paymentOptionFormFieldDescriptor);
		return this;
	}

	/**
	 * Add a visible text field to the row.
	 * @param fieldId id for the field
	 * @param allowAutoComplete if autocomplete is allowed for the field
	 * @param isRequired ensure that the user enters a value for this field
	 * @return builder
	 */
	public PaymentOptionFormRowDescriptorBuilder withTextField(final String fieldId, final boolean allowAutoComplete, final boolean isRequired) {
		PaymentOptionFormFieldDescriptor paymentOptionFormFieldDescriptor = instantiatePaymentOptionFormFieldDescriptor();
		paymentOptionFormFieldDescriptor.setType(PaymentOptionFormFieldTypeEnum.TEXT);
		paymentOptionFormFieldDescriptor.setFieldId(fieldId);
		paymentOptionFormFieldDescriptor.setAutoCompleteAllowed(allowAutoComplete);
		paymentOptionFormFieldDescriptor.setRequired(isRequired);
		this.visibleFields.add(paymentOptionFormFieldDescriptor);
		return this;
	}

	/**
	 * Add a visible text field to the row.
	 * @param fieldId id for the field
	 * @param allowAutoComplete if autocomplete is allowed for the field
	 * @param isRequired ensure that the user enters a value for this field
	 * @param maxLength the maximum allowed length for the field value
	 * @return builder
	 */
	public PaymentOptionFormRowDescriptorBuilder withTextField(final String fieldId, final boolean allowAutoComplete, final boolean isRequired,
			final int maxLength) {
		PaymentOptionFormFieldDescriptor paymentOptionFormFieldDescriptor = instantiatePaymentOptionFormFieldDescriptor();
		paymentOptionFormFieldDescriptor.setType(PaymentOptionFormFieldTypeEnum.TEXT);
		paymentOptionFormFieldDescriptor.setFieldId(fieldId);
		paymentOptionFormFieldDescriptor.setAutoCompleteAllowed(allowAutoComplete);
		paymentOptionFormFieldDescriptor.setRequired(isRequired);
		paymentOptionFormFieldDescriptor.setMaxLength(maxLength);
		this.visibleFields.add(paymentOptionFormFieldDescriptor);
		return this;
	}

	/**
	 * Add a visible text field to the row.
	 * @param fieldId id for the field
	 * @param allowAutoComplete if autocomplete is allowed for the field
	 * @param isRequired ensure that the user enters a value for this field
	 * @param minLength the minimum allowed length for the field value
	 * @param maxLength the maximum allowed length for the field value
	 * @return builder
	 */
	public PaymentOptionFormRowDescriptorBuilder withTextField(final String fieldId, final boolean allowAutoComplete, final boolean isRequired,
			final int minLength, final int maxLength) {
		PaymentOptionFormFieldDescriptor paymentOptionFormFieldDescriptor = instantiatePaymentOptionFormFieldDescriptor();
		paymentOptionFormFieldDescriptor.setType(PaymentOptionFormFieldTypeEnum.TEXT);
		paymentOptionFormFieldDescriptor.setFieldId(fieldId);
		paymentOptionFormFieldDescriptor.setAutoCompleteAllowed(allowAutoComplete);
		paymentOptionFormFieldDescriptor.setRequired(isRequired);
		paymentOptionFormFieldDescriptor.setMinLength(minLength);
		paymentOptionFormFieldDescriptor.setMaxLength(maxLength);
		this.visibleFields.add(paymentOptionFormFieldDescriptor);
		return this;
	}

	/**
	 * Add a select box (combo box) to the row.
	 * @param fieldId id for the field
	 * @param isRequired ensure that the user enters a value for this field
	 * @param optionsMap the options to display in the select box
	 * @return builder
	 */
	public PaymentOptionFormRowDescriptorBuilder withSelectField(final String fieldId, final boolean isRequired,
			final Map<String, String> optionsMap) {
		PaymentOptionFormFieldDescriptor paymentOptionFormFieldDescriptor = instantiatePaymentOptionFormFieldDescriptor();
		paymentOptionFormFieldDescriptor.setType(PaymentOptionFormFieldTypeEnum.SELECT);
		paymentOptionFormFieldDescriptor.setFieldId(fieldId);
		paymentOptionFormFieldDescriptor.setRequired(isRequired);
		paymentOptionFormFieldDescriptor.setOptionsMap(optionsMap);
		this.visibleFields.add(paymentOptionFormFieldDescriptor);
		return this;
	}

	/**
	 * Add a special field type to the row.
	 * @param fieldId id for the field
	 * @param isRequired ensure that the user enters a value for this field
	 * @param fieldTypeEnum special field type
	 * @param fieldFormat for expiry date fields
	 * @return builder
	 */
	public PaymentOptionFormRowDescriptorBuilder withSpecialField(final String fieldId, final boolean isRequired,
			final PaymentOptionFormFieldTypeEnum fieldTypeEnum, final String fieldFormat) {
		PaymentOptionFormFieldDescriptor paymentOptionFormFieldDescriptor = instantiatePaymentOptionFormFieldDescriptor();
		paymentOptionFormFieldDescriptor.setType(fieldTypeEnum);
		paymentOptionFormFieldDescriptor.setFieldId(fieldId);
		paymentOptionFormFieldDescriptor.setRequired(isRequired);
		paymentOptionFormFieldDescriptor.setFieldFormat(fieldFormat);
		this.visibleFields.add(paymentOptionFormFieldDescriptor);
		return this;
	}

	/**
	 * Build the PaymentOptionFormRowDescriptor.
	 * @return PaymentOptionFormRowDescriptor
	 */
	public PaymentOptionFormRowDescriptor build() {
		PaymentOptionFormRowDescriptor result = instantiatePaymentOptionFormRowDescriptor();
		result.setMessageKey(messageKey);
		result.getVisibleFields().addAll(visibleFields);
		return result;
	}

	/**
	 * Instantiate a new PaymentOptionFormRowDescriptor.
	 * @return new PaymentOptionFormRowDescriptor
	 */
	protected PaymentOptionFormRowDescriptor instantiatePaymentOptionFormRowDescriptor() {
		return new PaymentOptionFormRowDescriptor();
	}

	/**
	 * Instantiate a new PaymentOptionFormFieldDescriptor.
	 * @return new PaymentOptionFormFieldDescriptor
	 */
	protected PaymentOptionFormFieldDescriptor instantiatePaymentOptionFormFieldDescriptor() {
		return new PaymentOptionFormFieldDescriptor();
	}
}
