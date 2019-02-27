/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.util.date.DateUtil;

/**
 * Test class for {@link AttributeValueTransformerImpl}.
 */
public class AttributeValueTransformerImplTest {

	private static final String EXPECTED_INTEGER_STRING_VALUE = "10345";
	private static final String SHORT_TEXT_MULTIVALUE_VALUE = "property1, property2, property3";
	private static final String NAME = "NAME";
	private static final String KEY = "KEY";
	private static final double EXPECTED_DOUBLE = 7.40;
	private static final String EXPECTED_DOUBLE_STRING_VALUE = "7.40";

	private final AttributeValueTransformer attributeValueTransformer = new AttributeValueTransformerImpl();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		attributeValueTransformer.transformToDomain(null);
	}

	/**
	 * Test attribute value with file as unsupported attribute type.
	 */
	@Test
	public void testFileAsUnsupportedAttributeType() {
		AttributeValue attributeValue = createAttributeValue(null, null, false, AttributeType.FILE, null);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(null, detailsEntity);
	}

	/**
	 * Test attribute value with image as unsupported attribute type.
	 */
	@Test
	public void testImageAsUnsupportedAttributeType() {
		AttributeValue attributeValue = createAttributeValue(null, null, false, AttributeType.IMAGE, null);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(null, detailsEntity);
	}

	/**
	 * Test attribute value without an associated value object.
	 */
	@Test
	public void testAttributeValueWithoutAnAssociatedValueObject() {
		AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.DECIMAL, null);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(null, detailsEntity);
	}

	/**
	 * Test attribute value with short text multi value attribute type.
	 */
	@Test
	public void testShortTextMultiValueAttributeType() {
		DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY,
				Arrays.asList(SHORT_TEXT_MULTIVALUE_VALUE.split(", ")),
				NAME,
				SHORT_TEXT_MULTIVALUE_VALUE);
		AttributeValue attributeValue = createAttributeValue(NAME, KEY, true, AttributeType.SHORT_TEXT, SHORT_TEXT_MULTIVALUE_VALUE);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(expectedDetailsEntity, detailsEntity);
	}

	/**
	 * Test attribute value with boolean attribute type set to true.
	 */
	@Test
	public void testTrueBooleanAttributeType() {
		boolean expectedBoolean = true;
		DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY, expectedBoolean, NAME, "True");

		AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.BOOLEAN, expectedBoolean);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(expectedDetailsEntity, detailsEntity);
	}

	/**
	 * Test attribute value with boolean attribute type set to false.
	 */
	@Test
	public void testFalseBooleanAttributeType() {
		boolean expectedBoolean = false;
		DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY, expectedBoolean, NAME, "False");

		AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.BOOLEAN, expectedBoolean);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(expectedDetailsEntity, detailsEntity);
	}

	/**
	 * Test attribute value with date attribute type.
	 */
	@Test
	public void testDateAttributeType() {
		Date expectedDate = new Date();
		DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY,
				expectedDate.getTime(),
				NAME,
				DateUtil.formatDate(expectedDate, Locale.ENGLISH));

		AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.DATE, expectedDate);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(expectedDetailsEntity, detailsEntity);
	}

	/**
	 * Test attribute value with date attribute type.
	 */
	@Test
	public void testDateTimeAttributeType() {
		Date expectedDate = new Date();
		DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY,
				expectedDate.getTime(),
				NAME,
				DateUtil.formatDateTime(expectedDate, Locale.ENGLISH));

		AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.DATETIME, expectedDate);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(expectedDetailsEntity, detailsEntity);
	}

	/**
	 * Tests that Decimal attributes are formatted correctly.
	 */
	@Test
	public void testDecimalAttributeType() {
		BigDecimal expectedBigDecimal = BigDecimal.valueOf(EXPECTED_DOUBLE);
		DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY, expectedBigDecimal, NAME, EXPECTED_DOUBLE_STRING_VALUE);

		AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.DECIMAL, expectedBigDecimal);

		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(expectedDetailsEntity, detailsEntity);
	}

	/**
	 * Test attribute value with integer attribute type.
	 */
	@Test
	public void testAttributeValueWithIntegerAttributeType() {
		Integer expectedInteger = Integer.valueOf(EXPECTED_INTEGER_STRING_VALUE);
		DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY, expectedInteger, NAME, EXPECTED_INTEGER_STRING_VALUE);

		AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.INTEGER, expectedInteger);
		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, Locale.ENGLISH);

		assertDetailsEntityEquals(expectedDetailsEntity, detailsEntity);
	}

	private DetailsEntity createDetailsEntity(final String name, final Object value, final String displayName, final String displayValue) {

		return DetailsEntity.builder()
				.withName(name)
				.withValue(value)
				.withDisplayName(displayName)
				.withDisplayValue(displayValue)
				.build();
	}

	private AttributeValue createAttributeValue(final String attributeName,
			final String attributeKey,
			final boolean attributeMultiValueEnabled,
			final AttributeType attributeValueAttributeType,
			final Object attributeValueValue) {

		Attribute attribute = createAttribute(attributeName, attributeKey, attributeMultiValueEnabled);
		AttributeValue attributeValue = createAttributeValue(attributeValueAttributeType, attributeValueValue, attribute);

		return attributeValue;
	}

	private Attribute createAttribute(final String name, final String key, final boolean multiValueEnabled) {
		Attribute attribute = new AttributeImpl();

		attribute.setName(name);
		attribute.setKey(key);
		attribute.setMultiValueType(AttributeMultiValueType.createAttributeMultiValueType(String.valueOf(multiValueEnabled)));

		return attribute;
	}

	private AttributeValue createAttributeValue(final AttributeType attributeType, final Object expectedValue, final Attribute attribute) {
		AttributeValue attributeValue = new ProductAttributeValueImpl();

		attributeValue.setAttributeType(attributeType);
		attributeValue.setAttribute(attribute);
		attributeValue.setValue(expectedValue);

		return attributeValue;
	}

	private void assertDetailsEntityEquals(final DetailsEntity expected, final DetailsEntity actual) {
		if (expected == null || actual == null) {
			assertEquals("The details entities should both be null.", expected, actual);
		} else {
			assertEquals("The names should be the same.", expected.getName(), actual.getName());
			assertEquals("The values should be the same.", expected.getValue(), actual.getValue());
			assertEquals("The display names should be the same.", expected.getDisplayName(), actual.getDisplayName());
			assertEquals("The display values should be the same.", expected.getDisplayValue(), actual.getDisplayValue());
		}
	}

}
