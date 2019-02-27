/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.provider.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.settings.MalformedSettingValueException;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.provider.converter.SettingValueTypeConverter;

/**
 * Test class for {@link com.elasticpath.settings.provider.SettingValueProvider}.
 */
public class SettingValueProviderImplTest {

	private static final String PATH = "COMMERCE/ITEST/SETTINGS/mockSetting";
	private static final String CONTEXT = "CONTEXT";

	private SettingValueProviderImpl<?> settingValueProvider;

	private final SettingsReader settingsReader = mock(SettingsReader.class);

	private final SettingValue settingValue = mock(SettingValue.class);

	@Before
	public void setUp() {
		settingValueProvider = new SettingValueProviderImpl<>();
		settingValueProvider.setSettingsReader(settingsReader);

		settingValueProvider.setPath(PATH);

		// Dummy Type Converter for unit tests. This is preferable to mocking as it allows each test to define the expected value for the
		// SettingValue.getValue()
		settingValueProvider.setSettingValueTypeConverter(new SettingValueTypeConverter() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> T convert(final SettingValue settingValue) {
				return (T) settingValue.getValue();
			}
		});
	}

	@Test
	public void verifyGetWithNoSettingsReaderThrowsIllegalStateException() {
		settingValueProvider.setSettingsReader(null);

		assertThatThrownBy(() -> settingValueProvider.get())
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	public void verifyGetWithNoSettingValueTypeConverterThrowsIllegalStateException() {
		settingValueProvider.setSettingValueTypeConverter(null);

		assertThatThrownBy(() -> settingValueProvider.get())
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	public void verifyGetWithNoPathThrowsIllegalStateException() {
		settingValueProvider.setPath(null);

		assertThatThrownBy(() -> settingValueProvider.get())
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	public void verifyGetWithPathAndNoContextCallsSettingsReader() {
		final String expected = UUID.randomUUID().toString();

		when(settingsReader.getSettingValue(PATH)).thenReturn(settingValue);
		when(settingValue.getValue()).thenReturn(expected);

		assertThat(settingValueProvider.get())
				.as("Expected the SettingValue's value to be returned by the provider")
				.isEqualTo(expected);
	}

	@Test
	public void verifyGetWithPathAndContextCallsSettingsReader() {
		final String expected = UUID.randomUUID().toString();

		settingValueProvider.setContext(CONTEXT);

		when(settingsReader.getSettingValue(PATH, CONTEXT)).thenReturn(settingValue);
		when(settingValue.getValue()).thenReturn(expected);

		assertThat(settingValueProvider.get())
				.as("Expected the SettingValue's value to be returned by the provider")
				.isEqualTo(expected);
	}

	@Test
	public void verifySettingValueConverterReturnsCorrectValueType() {
		final BigDecimal expected = new BigDecimal("123.4");

		final SettingValue bigDecimalSettingValue = mock(SettingValue.class, "bigDecimalTypeSettingValue");

		@SuppressWarnings("unchecked") final SettingValueTypeConverter typeConverter = mock(SettingValueTypeConverter.class);

		when(settingsReader.getSettingValue(PATH)).thenReturn(bigDecimalSettingValue);
		when(typeConverter.convert(bigDecimalSettingValue)).thenReturn(expected);

		settingValueProvider.setSettingValueTypeConverter(typeConverter);
		settingValueProvider.get();
	}

	@Test
	public void verifyGetPropagatesUnexpectedSettingValueTypeExceptionsFromConverter() {
		final SettingValueTypeConverter typeConverter = mock(SettingValueTypeConverter.class);

		when(settingsReader.getSettingValue(PATH)).thenReturn(settingValue);
		doThrow(MalformedSettingValueException.class).when(typeConverter).convert(settingValue);

		settingValueProvider.setSettingValueTypeConverter(typeConverter);

		assertThatThrownBy(() -> settingValueProvider.get())
				.isInstanceOf(MalformedSettingValueException.class);
	}

	@Test
	public void verifyGetWithContextUsedWhenContextNotConfigured() throws Exception {
		final String expected = UUID.randomUUID().toString();

		when(settingsReader.getSettingValue(PATH, CONTEXT)).thenReturn(settingValue);
		when(settingValue.getValue()).thenReturn(expected);

		assertThat(settingValueProvider.get(CONTEXT))
				.as("Expected the context value to be used to determine the setting value")
				.isEqualTo(expected);
	}

	@Test
	public void verifyGetWithContextOverridesSetContext() throws Exception {
		final String overrideContext = "CONTEXT2";
		final String expected = UUID.randomUUID().toString();

		settingValueProvider.setContext(CONTEXT);

		when(settingsReader.getSettingValue(PATH, overrideContext)).thenReturn(settingValue);
		when(settingValue.getValue()).thenReturn(expected);

		assertThat(settingValueProvider.get(overrideContext))
				.as("Expected the context value to be used to determine the setting value")
				.isEqualTo(expected);
	}

}