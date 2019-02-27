/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.provider.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;
import com.elasticpath.settings.MalformedSettingValueException;
import com.elasticpath.settings.UnexpectedSettingValueTypeException;
import com.elasticpath.settings.domain.SettingValue;

public class SettingValueTypeConverterImplTest {

	private static final String STRING_TYPE = "String";
	private static final String BIG_DECIMAL_TYPE = "BigDecimal";
	private static final String OBJECT_TYPE = "Object";
	private static final String INTEGER_TYPE = "Integer";
	private static final String BOOLEAN_TYPE = "boolean";

	private static final String BIGDECIMAL_VALUE = "10.0";
	private static final String STRING_VALUE = "setting-value";

	private final Map<String, StringToTypeConverter<?>> typeConverterMap = new HashMap<String, StringToTypeConverter<?>>();

	@Test
	public void verifyNullSettingValueValueReturnsNull() throws Exception {
		final SettingValue settingValue = mock(SettingValue.class);

		typeConverterMap.put(STRING_TYPE, mock(StringToTypeConverter.class));
		when(settingValue.getValueType()).thenReturn(STRING_TYPE);

		when(settingValue.getValue()).thenReturn(null);

		assertThat(createSettingValueTypeConverter().<String>convert(settingValue))
				.isNull();
	}

	@Test
	public void verifyTypeConverterMapIsConsultedForAppropriateValueType() {
		final BigDecimal expected = new BigDecimal("123.4");

		final SettingValue bigDecimalSettingValue = mock(SettingValue.class);

		@SuppressWarnings("unchecked")
		final StringToTypeConverter<BigDecimal> bigDecimalTypeConverter = mock(StringToTypeConverter.class);
		typeConverterMap.put(BIG_DECIMAL_TYPE, bigDecimalTypeConverter);

		// These guys should never be invoked
		typeConverterMap.put(OBJECT_TYPE, mock(StringToTypeConverter.class));
		typeConverterMap.put(INTEGER_TYPE, mock(StringToTypeConverter.class));
		typeConverterMap.put(BOOLEAN_TYPE, mock(StringToTypeConverter.class));

		when(bigDecimalSettingValue.getValueType()).thenReturn(BIG_DECIMAL_TYPE);
		when(bigDecimalSettingValue.getValue()).thenReturn(BIGDECIMAL_VALUE);
		when(bigDecimalTypeConverter.convert(BIGDECIMAL_VALUE)).thenReturn(expected);

		final SettingValueTypeConverterImpl settingValueTypeConverter = createSettingValueTypeConverter();

		assertThat(settingValueTypeConverter.<BigDecimal>convert(bigDecimalSettingValue))
				.as("Unexpected converted value")
				.isEqualTo(expected);
	}

	@Test
	public void verifyUnexpectedSettingValueTypeExceptionThrownWhenNoCapableTypeConverterFound() {
		// Only a Boolean converter goes into the map...
		typeConverterMap.put(BOOLEAN_TYPE, mock(StringToTypeConverter.class));

		final SettingValue settingValue = mock(SettingValue.class);

		// ... but we'll need a String converter instead.
		when(settingValue.getValueType()).thenReturn(STRING_TYPE);
		when(settingValue.getValue()).thenReturn("foo");

		assertThatThrownBy(() -> createSettingValueTypeConverter().convert(settingValue))
				.isInstanceOf(UnexpectedSettingValueTypeException.class);
	}

	@Test
	public void verifyConversionExceptionsFromConverterWrappedInMalformedSettingValueException() {
		final StringToTypeConverter<?> typeConverter = mock(StringToTypeConverter.class);
		typeConverterMap.put(STRING_TYPE, typeConverter);

		final SettingValue settingValue = mock(SettingValue.class);

		final ConversionMalformedValueException conversionException = new ConversionMalformedValueException("Boom");

		when(settingValue.getValueType()).thenReturn(STRING_TYPE);
		when(settingValue.getValue()).thenReturn(STRING_VALUE);

		doThrow(conversionException).when(typeConverter).convert(STRING_VALUE);

		// for the exception message
		when(settingValue.getPath()).thenReturn("A/B/c");

		assertThatThrownBy(() -> createSettingValueTypeConverter().convert(settingValue))
				.isInstanceOf(MalformedSettingValueException.class)
				.hasCause(conversionException);
	}

	private SettingValueTypeConverterImpl createSettingValueTypeConverter() {
		final SettingValueTypeConverterImpl settingValueTypeConverter = new SettingValueTypeConverterImpl();
		settingValueTypeConverter.setStringToTypeConverterMap(typeConverterMap);
		return settingValueTypeConverter;
	}

}