/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.validation.impl.ConstraintViolationTransformerImpl;

/**
 * Tests for {@link ConstraintViolationTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConstraintViolationTransformerImplTest {

	public static final String JAVAX_VALIDATION_CONSTRAINTS_NOT_NULL_MESSAGE = "{javax.validation.constraints.NotNull.message}";
	public static final String JAVAX_VALIDATION_CONSTRAINTS_SIZE_MESSAGE = "{javax.validation.constraints.Size.message}";
	private final Map<String, String> mappings = Maps.newHashMap();
	private final Set<ConstraintViolation<Object>> constraintsSet = Sets.newHashSet();
	private final Map<String, Object> attributes = Maps.newHashMap();

	private static final String DOMAIN_FIELD_NAME = "firstname";
	private static final String MESSAGE = "cannot be empty";
	private String domainFieldValue = "";
	private static final String PAYLOAD_KEY = "payload";
	private static final String PAYLOAD_VALUE = "payload value";

	@Mock
	Path path;

	@Mock
	ConstraintViolation<Object> constraintViolation;

	@Mock
	ConstraintDescriptor<?> constraintDescriptor;

	@InjectMocks
	ConstraintViolationTransformerImpl constraintViolationTransformer;

	@Before
	public void initialize() {
		Map<String, String> idMap = new HashMap<>();
		idMap.put(JAVAX_VALIDATION_CONSTRAINTS_NOT_NULL_MESSAGE, "field.required");
		idMap.put(JAVAX_VALIDATION_CONSTRAINTS_SIZE_MESSAGE, "field.invalid.size");
		constraintViolationTransformer.setIdMap(idMap);

		when(constraintViolation.getPropertyPath()).thenReturn(path);
		Mockito.<ConstraintDescriptor<?>>when(constraintViolation.getConstraintDescriptor()).thenReturn(constraintDescriptor);
		when(constraintDescriptor.getAttributes()).thenReturn(attributes);

		constraintsSet.add(constraintViolation);
		when(constraintViolation.getMessage()).thenReturn(MESSAGE);
	}

	@Test
	public void testDataContainsFieldnameKey() {
		mappings.put(DOMAIN_FIELD_NAME, "John");
		attributes.put(PAYLOAD_KEY, PAYLOAD_VALUE);

		when(constraintViolation.getMessageTemplate()).thenReturn(JAVAX_VALIDATION_CONSTRAINTS_NOT_NULL_MESSAGE);
		when(path.toString()).thenReturn(DOMAIN_FIELD_NAME);
		when(constraintViolation.getLeafBean()).thenReturn("value");
		when(constraintViolation.getInvalidValue()).thenReturn("value");

		List<StructuredErrorMessage> result = constraintViolationTransformer.transform(constraintsSet);

		// The data field should contain a "field-name" key.
		assertTrue(containsKey(result, "field-name"));
	}

	@Test
	public void testDataContainsInvalidValueProperty() {
		domainFieldValue = "!@#$%";
		mappings.put(DOMAIN_FIELD_NAME, domainFieldValue);
		attributes.put(PAYLOAD_KEY, PAYLOAD_VALUE);

		when(constraintViolation.getMessageTemplate()).thenReturn(JAVAX_VALIDATION_CONSTRAINTS_SIZE_MESSAGE);
		when(path.toString()).thenReturn(DOMAIN_FIELD_NAME);
		when(constraintViolation.getMessage()).thenReturn(MESSAGE);
		when(constraintViolation.getLeafBean()).thenReturn("leafBean");
		when(constraintViolation.getInvalidValue()).thenReturn(domainFieldValue);

		List<StructuredErrorMessage> result = constraintViolationTransformer.transform(constraintsSet);

		// The data field should contain an "invalid-value" key with a value the same as the one set for the map.
		assertTrue(containsKey(result, "invalid-value"));
		assertTrue(containsValue(result, domainFieldValue));
	}

	@Test
	public void testNonBlacklistedAttributeIsPresentOnData() {
		domainFieldValue = "!@#$%";
		mappings.put(DOMAIN_FIELD_NAME, domainFieldValue);
		String attributeValue = "this should show on data.";

		attributes.put("attribute", attributeValue);
		attributes.put(PAYLOAD_KEY, attributeValue);
		attributes.put("groups", attributeValue);
		attributes.put("message", attributeValue);

		when(constraintViolation.getMessageTemplate()).thenReturn(JAVAX_VALIDATION_CONSTRAINTS_SIZE_MESSAGE);
		when(path.toString()).thenReturn(DOMAIN_FIELD_NAME);

		when(constraintViolation.getLeafBean()).thenReturn(domainFieldValue);
		when(constraintViolation.getInvalidValue()).thenReturn(domainFieldValue);

		List<StructuredErrorMessage> result = constraintViolationTransformer.transform(constraintsSet);

		// Key/value pairs outside of the following keys (payload, groups, message) should be in the result.
		assertTrue(containsValue(result, attributeValue));
		assertFalse(containsKey(result, PAYLOAD_KEY));
		assertFalse(containsKey(result, "groups"));
		assertFalse(containsKey(result, "message"));
	}

	@Test
	public void testWhereMessageTemplateAndMessageShouldShouldReturnEmptyListIfTheyAreEqual() {
		mappings.put(DOMAIN_FIELD_NAME, "John");
		attributes.put(PAYLOAD_KEY, PAYLOAD_VALUE);

		when(constraintViolation.getMessageTemplate()).thenReturn(JAVAX_VALIDATION_CONSTRAINTS_NOT_NULL_MESSAGE);
		when(constraintViolation.getMessage()).thenReturn(JAVAX_VALIDATION_CONSTRAINTS_NOT_NULL_MESSAGE);

		List<StructuredErrorMessage> result = constraintViolationTransformer.transform(constraintsSet);

		assertThat("result should be empty", result, IsEmptyCollection.empty());
	}

	@Test
	public void testFieldNameAttributeKeyShouldNotBePresentInData() {
		domainFieldValue = "";
		mappings.put(DOMAIN_FIELD_NAME, domainFieldValue);
		attributes.put("fieldName", "firstname");

		when(constraintViolation.getMessageTemplate()).thenReturn("{field.required}");
		when(path.toString()).thenReturn(DOMAIN_FIELD_NAME);
		when(constraintViolation.getLeafBean()).thenReturn(domainFieldValue);
		when(constraintViolation.getInvalidValue()).thenReturn(domainFieldValue);

		List<StructuredErrorMessage> result = constraintViolationTransformer.transform(constraintsSet);

		assertFalse("result should not contain fieldName key", containsKey(result, "fieldName"));
	}

	private boolean containsValue(final List<StructuredErrorMessage> result, final String value) {
		return result.stream().anyMatch(structuredErrorMessage -> structuredErrorMessage.getData().containsValue(value));
	}

	private boolean containsKey(final List<StructuredErrorMessage> result, final String key) {
		return result.stream().anyMatch(structuredErrorMessage -> structuredErrorMessage.getData().containsKey(key));
	}
}
