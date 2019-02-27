/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.util;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;

/**
 * A wrapper class for {@link com.elasticpath.domain.cartmodifier.CartItemModifierField} used
 * in dynamic validation.
 */
public class DynamicCartItemModifierField {
	private final String valueToValidate;
	private final String fieldName;
	private final CartItemModifierField referentField;
	private final String[] validOptions;

	/**
	 * Custom constructor.
	 *
	 * @param fieldName       the name of the field being validated.
	 * @param valueToValidate field value to validate.
	 * @param referentField   referent field.
	 */
	public DynamicCartItemModifierField(final String fieldName,
			final String valueToValidate,
			final CartItemModifierField referentField) {

		this.fieldName = fieldName;
		this.valueToValidate = valueToValidate;
		this.referentField = referentField;
		this.validOptions = getValidFieldOptions(referentField.getCartItemModifierFieldOptions());
	}

	private String[] getValidFieldOptions(final Set<CartItemModifierFieldOption> validFieldOptions) {
		return validFieldOptions
				.stream()
				.map(CartItemModifierFieldOption::getValue)
				.collect(Collectors.toList())
				.toArray(new String[]{});
	}

	/**
	 * In case of {@link com.elasticpath.domain.cartmodifier.CartItemModifierType#PICK_MULTI_OPTION}
	 * return zero or more invalid options or null in all other cases/types.
	 *
	 * @return null - if field doesn't have options or validated value is null/empty;
	 * String array with zero or more invalid options.
	 */
	public String[] getInvalidOptions() {
		return MultiOptionHandler.getInvalidOptions(valueToValidate, validOptions);
	}

	/**
	 * Returns the name of the field being validated.
	 * E.g. email
	 *
	 * @return field name.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Get valid field options, if any.
	 * Applicable to {@link com.elasticpath.domain.cartmodifier.CartItemModifierType#PICK_SINGLE_OPTION} and
	 * {@link com.elasticpath.domain.cartmodifier.CartItemModifierType#PICK_MULTI_OPTION} field types.
	 *
	 * @return an array of valid options, if available or empty array.
	 */
	public Optional<String[]> getValidOptions() {
		return Optional.ofNullable(validOptions);
	}

	public CartItemModifierField getReferentField() {
		return referentField;
	}
}
