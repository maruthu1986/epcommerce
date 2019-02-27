/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;
import com.elasticpath.validation.impl.ConstraintViolationTransformerImpl;
import com.elasticpath.validation.service.impl.CartItemModifierFieldValidationServiceImpl;

/**
 * Test class for {@link CartItemModifierFieldValidationServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartItemModifierFieldValidationServiceImplTest {

	@InjectMocks
	private CartItemModifierFieldValidationServiceImpl fixture;

	@Mock
	private CartItemModifierField referentField;

	private static final int MIN_SIZE = 0;
	private static final int SHORT_MAX_SIZE = 5;
	private static final int MEDIUM_MAX_SIZE = 10;

	/**
	 * A decimal field should give decimal constraint violation when given a non-decimal value.
	 */
	@Test
	public void shouldHaveDecimalConstraintViolation() {
		Map<String, String> idMap = new HashMap<>();
		idMap.put("{field.invalid.decimal.format}", "field.invalid.decimal.format");

		ConstraintViolationTransformerImpl transformer = new ConstraintViolationTransformerImpl();
		transformer.setIdMap(idMap);
		fixture.setConstraintViolationTransformer(transformer);

		String decimalFieldName = "amount";
		String fieldValue = "123abc";

		Map<String, String> itemsToValidate = new HashMap<>();
		itemsToValidate.put(decimalFieldName, fieldValue);

		Set<CartItemModifierField> referentFields = new HashSet<>();

		when(referentField.getCode()).thenReturn(decimalFieldName);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.DECIMAL);
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(MEDIUM_MAX_SIZE);

		referentFields.add(referentField);

		List<StructuredErrorMessage> actualStructuredErrorMessages = fixture.validate(itemsToValidate, referentFields);

		assertEquals(1, actualStructuredErrorMessages.size());

		StructuredErrorMessage structuredErrorMessage = actualStructuredErrorMessages.iterator().next();

		String expectedDebugMessage = String.format("'%s' value '%s' must be a decimal.", decimalFieldName, fieldValue);

		assertEquals(decimalFieldName, structuredErrorMessage.getData().get("field-name"));
		assertEquals(expectedDebugMessage, structuredErrorMessage.getDebugMessage());
		assertEquals("field.invalid.decimal.format", structuredErrorMessage.getMessageId());
	}

	/**
	 * Verifies that the invalid field size constraint matches the expected constraint.
	 */
	@Test
	public void verifyInvalidFieldSizeInConstraintViolation() {
		Map<String, String> idMap = new HashMap<>();
		idMap.put("{field.invalid.size}", "field.invalid.size");

		ConstraintViolationTransformerImpl transformer = new ConstraintViolationTransformerImpl();
		transformer.setIdMap(idMap);
		fixture.setConstraintViolationTransformer(transformer);

		String shortTextFieldName = "short_text";
		String fieldValue = "123456";

		Map<String, String> itemsToValidate = new HashMap<>();
		itemsToValidate.put(shortTextFieldName, fieldValue);

		Set<CartItemModifierField> referentFields = new HashSet<>();

		when(referentField.getCode()).thenReturn(shortTextFieldName);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.SHORT_TEXT);
		when(referentField.isRequired()).thenReturn(false);
		when(referentField.getMaxSize()).thenReturn(SHORT_MAX_SIZE);

		referentFields.add(referentField);

		List<StructuredErrorMessage> actualStructuredErrorMessages = fixture.validate(itemsToValidate, referentFields);

		assertEquals(1, actualStructuredErrorMessages.size());

		StructuredErrorMessage structuredErrorMessage = actualStructuredErrorMessages.iterator().next();

		String expectedDebugMessage = String.format("'%s' value must contain between %d and %d characters.",
				shortTextFieldName, MIN_SIZE, SHORT_MAX_SIZE);

		assertEquals(shortTextFieldName, structuredErrorMessage.getData().get("field-name"));
		assertEquals(expectedDebugMessage, structuredErrorMessage.getDebugMessage());
		assertEquals("field.invalid.size", structuredErrorMessage.getMessageId());

	}

	/**
	 * Verifies that the field required constraint matches the expected constraint.
	 */
	@Test
	public void verifyFieldRequiredViolationInConstraintViolation() {
		Map<String, String> idMap = new HashMap<>();
		idMap.put("{field.required}", "field.required");

		ConstraintViolationTransformerImpl transformer = new ConstraintViolationTransformerImpl();
		transformer.setIdMap(idMap);
		fixture.setConstraintViolationTransformer(transformer);

		String messageFieldName = "message";

		Map<String, String> itemsToValidate = new HashMap<>();
		itemsToValidate.put(messageFieldName, "");

		Set<CartItemModifierField> referentFields = new HashSet<>();

		when(referentField.getCode()).thenReturn(messageFieldName);
		when(referentField.getFieldType()).thenReturn(CartItemModifierType.SHORT_TEXT);
		when(referentField.isRequired()).thenReturn(true);
		when(referentField.getMaxSize()).thenReturn(SHORT_MAX_SIZE);

		referentFields.add(referentField);

		List<StructuredErrorMessage> actualStructuredErrorMessages = fixture.validate(itemsToValidate, referentFields);

		assertEquals(1, actualStructuredErrorMessages.size());

		StructuredErrorMessage structuredErrorMessage = actualStructuredErrorMessages.iterator().next();

		String expectedDebugMessage = String.format("'%s' value is required.", messageFieldName);

		assertEquals(messageFieldName, structuredErrorMessage.getData().get("field-name"));
		assertEquals(expectedDebugMessage, structuredErrorMessage.getDebugMessage());
		assertEquals("field.required", structuredErrorMessage.getMessageId());
	}
}