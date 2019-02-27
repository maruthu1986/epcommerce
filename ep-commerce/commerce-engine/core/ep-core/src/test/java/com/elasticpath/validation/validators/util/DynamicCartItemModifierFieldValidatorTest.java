/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.validation.validators.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;

/**
 * Test class for {@link DynamicCartItemModifierFieldValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DynamicCartItemModifierFieldValidatorTest {

	@InjectMocks
	private final DynamicCartItemModifierFieldValidator validatorWithNoSuppression = new DynamicCartItemModifierFieldValidator(false);

	@InjectMocks
	private final DynamicCartItemModifierFieldValidator validatorWithSuppression = new DynamicCartItemModifierFieldValidator(true);

	@Mock
	private CartItemModifierField referentField;

	@Mock
	private CartItemModifierFieldOption cartItemModifierFieldOption1;

	@Mock
	private CartItemModifierFieldOption cartItemModifierFieldOption2;

	private static final int MIN_SIZE = 0;
	private static final int SHORT_MAX_SIZE = 5;
	private static final int MEDIUM_MAX_SIZE = 10;
	private static final int LONG_MAX_SIZE = 20;

	/**
	 * A required field should give a constraint violation when given an empty value.
	 */
	@Test
	public void shouldHaveRequiredConstraintViolation() {
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(MEDIUM_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.SHORT_TEXT);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(new HashSet<>());

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField("sender name", "", referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithNoSuppression.validate(fieldToValidate);

		assertEquals(1, actualConstraintViolations.size());

		ConstraintViolation<DynamicCartItemModifierField> constraintViolation = actualConstraintViolations.iterator().next();

		assertEquals("sender name", constraintViolation.getRootBean().getFieldName());
		assertEquals("'sender name' value is required.", constraintViolation.getMessage());
		assertEquals("{field.required}", constraintViolation.getMessageTemplate());
	}

	/**
	 * A required field should give no constraint violation when given an empty value with require field suppression.
	 */
	@Test
	public void shouldHaveNoRequiredConstraintViolationWithRequireSuppression() {
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(MEDIUM_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.SHORT_TEXT);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(new HashSet<>());

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField("sender name", "", referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithSuppression.validate(fieldToValidate);

		assertEquals(0, actualConstraintViolations.size());
	}

	/**
	 * A field with max length of 5 should give length constraint violation when given a value with length of 6.
	 */
	@Test
	public void shouldHaveLengthConstraintViolation() {
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(SHORT_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.SHORT_TEXT);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(new HashSet<>());

		String fieldName = "message";

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField(fieldName, "123ABC", referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithNoSuppression.validate(fieldToValidate);

		assertEquals(1, actualConstraintViolations.size());

		ConstraintViolation<DynamicCartItemModifierField> constraintViolation = actualConstraintViolations.iterator().next();

		String expectedMessage = String.format("'%s' value must contain between %d and %d characters.", fieldName, MIN_SIZE, SHORT_MAX_SIZE);

		assertEquals(fieldName, constraintViolation.getRootBean().getFieldName());
		assertEquals(expectedMessage, constraintViolation.getMessage());
		assertEquals("{field.invalid.size}", constraintViolation.getMessageTemplate());
	}

	/**
	 * A decimal field should give decimal constraint violation when given a non-decimal value.
	 */
	@Test
	public void shouldHaveDecimalConstraintViolation() {
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(SHORT_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.DECIMAL);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(new HashSet<>());

		String fieldName = "id";
		String fieldValue = "12ABC";

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField(fieldName, fieldValue, referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithNoSuppression.validate(fieldToValidate);

		assertEquals(1, actualConstraintViolations.size());

		ConstraintViolation<DynamicCartItemModifierField> constraintViolation = actualConstraintViolations.iterator().next();

		String expectedMessage = String.format("'%s' value '%s' must be a decimal.", fieldName, fieldValue);

		assertEquals(fieldName, constraintViolation.getRootBean().getFieldName());
		assertEquals(expectedMessage, constraintViolation.getMessage());
		assertEquals("{field.invalid.decimal.format}", constraintViolation.getMessageTemplate());
	}

	/**
	 * A multi option field should give constraint violation when given an invalid value.
	 */
	@Test
	public void shouldHaveMultiOptionConstraintViolation() {
		when(cartItemModifierFieldOption1.getValue()).thenReturn("monthly");
		when(cartItemModifierFieldOption2.getValue()).thenReturn("yearly");

		Set<CartItemModifierFieldOption> cartItemModifierFieldOptions = new HashSet<>();
		cartItemModifierFieldOptions.add(cartItemModifierFieldOption1);
		cartItemModifierFieldOptions.add(cartItemModifierFieldOption2);

		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(LONG_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.PICK_MULTI_OPTION);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(cartItemModifierFieldOptions);

		String fieldName = "payment";
		String fieldValue = "12345";

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField(fieldName, fieldValue, referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithNoSuppression.validate(fieldToValidate);

		assertEquals(1, actualConstraintViolations.size());

		ConstraintViolation<DynamicCartItemModifierField> constraintViolation = actualConstraintViolations.iterator().next();

		String expectedMessage = String.format("'%s' value '%s' must match a valid option.", fieldName, fieldValue);

		assertEquals(fieldName, constraintViolation.getRootBean().getFieldName());
		assertEquals(expectedMessage, constraintViolation.getMessage());
		assertEquals("{field.invalid.option.value}", constraintViolation.getMessageTemplate());
	}

	@Test
	public void shouldHaveNoConstraintViolations() {
		when(cartItemModifierFieldOption1.getValue()).thenReturn("monthly");
		when(cartItemModifierFieldOption2.getValue()).thenReturn("yearly");

		Set<CartItemModifierFieldOption> cartItemModifierFieldOptions = new HashSet<>();
		cartItemModifierFieldOptions.add(cartItemModifierFieldOption1);
		cartItemModifierFieldOptions.add(cartItemModifierFieldOption2);

		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(LONG_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.PICK_SINGLE_OPTION);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(cartItemModifierFieldOptions);

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField("payment", "yearly", referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithNoSuppression.validate(fieldToValidate);

		assertEquals(0, actualConstraintViolations.size());
	}

	/**
	 * An integer field with max length of 4 should give length and integer constraint violations
	 * when given a decimal value with length of 7.
	 */
	@Test
	public void shouldHaveLengthAndIntegerConstraintViolation() {
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(SHORT_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.INTEGER);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(new HashSet<>());

		String fieldName = "amount";
		String fieldValue = "14.1512";

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField(fieldName, fieldValue, referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithNoSuppression.validate(fieldToValidate);

		assertEquals(2, actualConstraintViolations.size());

		String expectedMessage1 = String.format("'%s' value '%s' must be an integer.", fieldName, fieldValue);
		String expectedMessage2 = String.format("'%s' value must contain between %d and %d characters.", fieldName, MIN_SIZE, SHORT_MAX_SIZE);

		String[] expectedConstraintMessages = {expectedMessage1, expectedMessage2};
		String[] expectedConstraintMessageTemplate = {"{field.invalid.integer.format}", "{field.invalid.size}"};

		containsExpectedConstraints(actualConstraintViolations, fieldToValidate.getFieldName(),
				expectedConstraintMessages, expectedConstraintMessageTemplate);
	}

	/**
	 * A Date field with max length of 10 should give length and date constraint violation when given a full DateTime.
	 */
	@Test
	public void shouldHaveLengthAndDateConstraintViolation() {
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(MEDIUM_MAX_SIZE);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.DATE);
		when(referentField.getCartItemModifierFieldOptions()).thenReturn(new HashSet<>());

		String fieldName = "date";
		String fieldValue = "2016-08-18T10:15:30+04:00";

		DynamicCartItemModifierField fieldToValidate = new DynamicCartItemModifierField(fieldName, fieldValue, referentField);

		Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations = validatorWithNoSuppression.validate(fieldToValidate);

		assertEquals(2, actualConstraintViolations.size());

		String expectedMessage1 = String.format("'%s' value '%s' must be in ISO8601 date format (YYYY-MM-DD).", fieldName, fieldValue);
		String expectedMessage2 = String.format("'%s' value must contain between %d and %d characters.", fieldName, MIN_SIZE, MEDIUM_MAX_SIZE);

		String[] expectedConstraintMessages = {expectedMessage1, expectedMessage2};
		String[] expectedConstraintMessageTemplate = {"{field.invalid.date.format}", "{field.invalid.size}"};

		containsExpectedConstraints(actualConstraintViolations, fieldToValidate.getFieldName(),
				expectedConstraintMessages, expectedConstraintMessageTemplate);
	}

	private void containsExpectedConstraints(final Set<ConstraintViolation<DynamicCartItemModifierField>> actualConstraintViolations,
			final String fieldName,
			final String[] expectedConstraintMessages,
			final String[] expectedConstraintMessageTemplate) {

		for (ConstraintViolation<DynamicCartItemModifierField> constraintViolation : actualConstraintViolations) {
			assertEquals(fieldName, constraintViolation.getRootBean().getFieldName());
			assertTrue(ArrayUtils.contains(expectedConstraintMessages, constraintViolation.getMessage()));
			assertTrue(ArrayUtils.contains(expectedConstraintMessageTemplate, constraintViolation.getMessageTemplate()));
		}
	}
}