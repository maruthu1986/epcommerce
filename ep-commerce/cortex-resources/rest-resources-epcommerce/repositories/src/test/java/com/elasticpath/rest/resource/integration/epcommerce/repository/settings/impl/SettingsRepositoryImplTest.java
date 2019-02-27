/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.settings.impl;

import io.reactivex.Maybe;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.provider.converter.SettingValueTypeConverter;

/**
 * Test that {@link SettingsRepositoryImpl} behaves as expected.
 */
public class SettingsRepositoryImplTest {

	private static final String SETTING_PATH = "/SETTINGS/PATH";
	private static final String STORE_CODE = "store";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final SettingsReader settingsReader = context.mock(SettingsReader.class);
	private final SettingValueTypeConverter settingValueTypeConverter = context.mock(SettingValueTypeConverter.class);

	private final SettingsRepository repository = new SettingsRepositoryImpl(settingsReader, settingValueTypeConverter);

	/**
	 * Test the behaviour of get string setting value.
	 */
	@Test
	public void testGetStringSettingValue() {
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(SETTING_PATH, STORE_CODE);
				will(returnValue(mockSettingValue));
				oneOf(settingValueTypeConverter).convert(mockSettingValue);
				will(returnValue("some value"));
			}
		});

		repository.getSetting(SETTING_PATH, STORE_CODE)
				.test()
				.assertNoErrors()
				.assertValue("some value");
	}

	/**
	 * Test the behaviour of get integer setting value.
	 */
	@Test
	public void testGetIntegerSettingValue() {
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		final Integer value = 123;

		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(SETTING_PATH, STORE_CODE);
				will(returnValue(mockSettingValue));
				oneOf(settingValueTypeConverter).convert(mockSettingValue);
				will(returnValue(value));
			}
		});

		repository.getSetting(SETTING_PATH, STORE_CODE)
				.test()
				.assertNoErrors()
				.assertValue(value);
	}

	/**
	 * Test the behaviour of get string setting value when the settings reader fails.
	 */
	@Test
	public void testGetStringSettingValueWhenReaderFails() {
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(SETTING_PATH, STORE_CODE);
				will(throwException(new EpServiceException("No such setting")));
			}
		});

		final Maybe<String> result = repository.getSetting(SETTING_PATH, STORE_CODE);
		result.test()
				.assertError(ResourceOperationFailure.class)
				.assertError(throwable -> ResourceStatus.SERVER_ERROR.equals(((ResourceOperationFailure) throwable).getResourceStatus()));
	}

	@Test
	public void testGetStringSettingValueNullReturnsSuccessWithNullData() {
		final SettingValue mockSettingValue = context.mock(SettingValue.class);

		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(SETTING_PATH, STORE_CODE);
				will(returnValue(mockSettingValue));
				oneOf(settingValueTypeConverter).convert(mockSettingValue);
				will(returnValue(null));
			}
		});

		repository.getSetting(SETTING_PATH, STORE_CODE)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

}
