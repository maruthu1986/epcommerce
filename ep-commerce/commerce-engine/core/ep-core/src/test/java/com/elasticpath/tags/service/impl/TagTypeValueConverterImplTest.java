/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;

/**
 * Test class for {@link TagTypeValueConverterImpl}.
 */
public class TagTypeValueConverterImplTest {

	private static final String EXPECTED_VALUE_DOES_NOT_MATCH_RESULT = "Expected value does not match result.";
	private static final String TAG_VALUE = "1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final TagTypeValueConverterImpl tagTypeValueConverter = new TagTypeValueConverterImpl();

	private final StringToTypeConverter<?> typeConverter = context.mock(StringToTypeConverter.class);

	private final TagDefinition tagDefinition = context.mock(TagDefinition.class);

	private final TagValueType tagValueType = context.mock(TagValueType.class);

	@Test
	public void testConvertStringValue() {
		final BigDecimal tagValue = BigDecimal.ONE;
		ensureTagDefinitionReturnsJavaType(BigDecimal.class.getCanonicalName());
		ensureTypeConverterReturnsValue(BigDecimal.class.getCanonicalName(), tagValue);

		Object resultObject = tagTypeValueConverter.convertValueTypeToTagJavaType(tagDefinition, TAG_VALUE);

		assertEquals(EXPECTED_VALUE_DOES_NOT_MATCH_RESULT, tagValue, resultObject);
	}

	@Test
	public void testConvertStringWhenNoConverterFound() {
		ensureTagDefinitionReturnsJavaType(String.class.getCanonicalName());
		ensureTypeConverterReturnsValue(BigDecimal.class.getCanonicalName(), null);

		Object resultObject = tagTypeValueConverter.convertValueTypeToTagJavaType(tagDefinition, TAG_VALUE);

		assertEquals(EXPECTED_VALUE_DOES_NOT_MATCH_RESULT, TAG_VALUE, resultObject);
	}

	@Test
	public void testConvertStringWhenExceptionThrownFromConverter() {
		ensureTagDefinitionReturnsJavaType(String.class.getCanonicalName());
		ensureTypeConverterReturnsException(String.class.getCanonicalName());

		Object resultObject = tagTypeValueConverter.convertValueTypeToTagJavaType(tagDefinition, TAG_VALUE);

		assertEquals(EXPECTED_VALUE_DOES_NOT_MATCH_RESULT, TAG_VALUE, resultObject);
	}

	@Test
	public void testNullTagDefinitionTreatedAsStringConverter() {
		ensureTypeConverterReturnsException(String.class.getCanonicalName());
		Object resultObject = tagTypeValueConverter.convertValueTypeToTagJavaType(null, TAG_VALUE);
		assertEquals(EXPECTED_VALUE_DOES_NOT_MATCH_RESULT, TAG_VALUE, resultObject);
	}

	private void ensureTypeConverterReturnsValue(final String typeName, final Object convertedObject) {
		context.checking(new Expectations() {
			{
				allowing(typeConverter).convert(TAG_VALUE);
				will(returnValue(convertedObject));
			}
		});
		Map<String, StringToTypeConverter<?>> typeConverterMap = new HashMap<>();
		typeConverterMap.put(typeName, typeConverter);
		tagTypeValueConverter.setTypeConverterMap(typeConverterMap);
	}

	private void ensureTypeConverterReturnsException(final String typeName) {
		context.checking(new Expectations() {
			{
				allowing(typeConverter).convert(TAG_VALUE);
				will(throwException(new ConversionMalformedValueException("Malformed value.")));
			}
		});

		Map<String, StringToTypeConverter<?>> typeConverterMap = new HashMap<>();
		typeConverterMap.put(typeName, typeConverter);
		tagTypeValueConverter.setTypeConverterMap(typeConverterMap);
	}

	private void ensureTagDefinitionReturnsJavaType(final String javaType) {
		context.checking(new Expectations() {
			{
				allowing(tagDefinition).getValueType();
				will(returnValue(tagValueType));

				allowing(tagValueType).getJavaType();
				will(returnValue(javaType));
			}
		});
	}
}