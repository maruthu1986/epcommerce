/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.refreshstrategy.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Contains test cases that are universal to all {@link com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy SettingRefreshStrategy}
 * implementations.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractSettingRefreshStrategyTestCases {

	/**
	 * A sample setting path.
	 */
	protected static final String SETTING_PATH = "/COMMERCE/SYSTEM/settingPath";

	/**
	 * A sample setting context.
	 */
	protected static final String SETTING_CONTEXT = "mycontext";

	/**
	 * A mocked Settings Reader.
	 */
	@Mock
	private SettingsReader settingsReader;

	/**
	 * Returns the Setting Refresh Strategy instance under test.
	 *
	 * @return the Setting Refresh Strategy instance
	 */
	protected abstract SettingRefreshStrategy getRefreshStrategy();

	/**
	 * Returns the map of settings parameters for use in tests.
	 *
	 * @return the settings parameters
	 */
	protected abstract String getSettingsParameterString();

	/**
	 * Returns a String representation of settings parameters for use in tests.
	 *
	 * @return a String representation of settings parameters
	 */
	protected abstract Map<String, String> getSettingsParameterMap();

	/**
	 * Populates the cache with the given settings values.
	 *
	 * @param settingPath             the path of the setting of which to populate the cache
	 * @param fallbackSettingValue    the default setting value
	 * @param contextualSettingValues a map of contextual settings values
	 */
	protected abstract void givenCacheReturnsSettingValue(String settingPath,
														  SettingValue fallbackSettingValue,
														  Map<String, SettingValue> contextualSettingValues);

	/**
	 * Returns a SettingValue from the underlying cache, if present.
	 *
	 * @param settingPath          the path of the setting
	 * @param settingContext       the context
	 * @param settingsParameterMap a map of settings parameters
	 * @return an optional SettingValue
	 */
	protected abstract Optional<SettingValue> getFromCache(String settingPath, String settingContext, Map<String, String> settingsParameterMap);

	/**
	 * Verifies that a cache miss with a context will populate the cache.
	 */
	@Test
	public void verifyCacheMissWithContextPopulatesCache() {
		final String differentContext = "DifferentContext";
		final SettingValue expectedSettingValue = mockSettingValue(SETTING_CONTEXT);
		final SettingValue siblingSettingValue = mockSettingValue(differentContext);

		when(settingsReader.getSettingValues(SETTING_PATH)).thenReturn(ImmutableSet.of(siblingSettingValue, expectedSettingValue));

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, SETTING_CONTEXT, getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(expectedSettingValue);

		assertThat(getFromCache(SETTING_PATH, SETTING_CONTEXT, getSettingsParameterMap()))
				.isPresent()
				.contains(expectedSettingValue);

		assertThat(getFromCache(SETTING_PATH, differentContext, getSettingsParameterMap()))
				.isPresent()
				.contains(siblingSettingValue);
	}

	/**
	 * Verifies that a cache hit with a context will not invoke the settings reader.
	 */
	@Test
	public void verifyCacheHitWithContextDoesNotDelegateToSettingsReader() {
		final SettingValue expectedSettingValue = mockSettingValue(SETTING_CONTEXT);

		givenCacheReturnsSettingValue(SETTING_PATH, null, ImmutableMap.of(SETTING_CONTEXT, expectedSettingValue));

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, SETTING_CONTEXT, getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(expectedSettingValue);

		verifyZeroInteractions(settingsReader);
	}

	/**
	 * Verifies that a cache miss with a non-matching context will fall back to the default setting value.
	 */
	@Test
	public void verifyCacheMissWithNotFoundContextFallsBackToDefaultContext() {
		final SettingValue contextualSettingValue = mockSettingValue(SETTING_CONTEXT);
		final SettingValue fallbackSettingValue = mockSettingValue();

		when(settingsReader.getSettingValues(SETTING_PATH)).thenReturn(ImmutableSet.of(contextualSettingValue));
		when(settingsReader.getSettingValue(SETTING_PATH)).thenReturn(fallbackSettingValue);

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, "No such context value", getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(fallbackSettingValue);
	}

	/**
	 * Verifies that a cache hit with a non-matching context will not invoke the settings reader.
	 */
	@Test
	public void verifyCacheHitWithNotFoundContextDoesNotInvokeSettingsReader() {
		final SettingValue defaultContextSettingValue = mockSettingValue();

		givenCacheReturnsSettingValue(SETTING_PATH, defaultContextSettingValue, Collections.emptyMap());

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, "No such context value", getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(defaultContextSettingValue);

		verifyZeroInteractions(settingsReader);
	}

	/**
	 * Verifies that a cache miss without a context will populate the cache.
	 */
	@Test
	public void verifyCacheMissWithoutContextPopulatesCache() {
		final SettingValue expectedSettingValue = mockSettingValue();

		when(settingsReader.getSettingValue(SETTING_PATH)).thenReturn(expectedSettingValue);

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(expectedSettingValue);

		verify(settingsReader).getSettingValue(SETTING_PATH);
	}

	/**
	 * Verifies that a cache hit with a context will not invoke the settings reader.
	 */
	@Test
	public void verifyCacheHitWithoutContextDoesNotDelegateToSettingsReader() {
		final SettingValue expectedSettingValue = mockSettingValue();

		givenCacheReturnsSettingValue(SETTING_PATH, expectedSettingValue, Collections.emptyMap());

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(expectedSettingValue);

		verifyZeroInteractions(settingsReader);
	}

	/**
	 * Verifies that a cache miss with a context in a different case will populate the cache.
	 */
	@Test
	public void verifyCacheMissWithDifferentContextCasePopulatesCache() {
		final SettingValue expectedSettingValue = mockSettingValue(SETTING_CONTEXT);

		when(settingsReader.getSettingValues(SETTING_PATH)).thenReturn(ImmutableSet.of(expectedSettingValue));

		final String upperCaseContext = SETTING_CONTEXT.toUpperCase(Locale.getDefault());

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, upperCaseContext,
				getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(expectedSettingValue);

		assertThat(getFromCache(SETTING_PATH, SETTING_CONTEXT, getSettingsParameterMap()))
				.isPresent()
				.contains(expectedSettingValue);
	}

	/**
	 * Verifies that a request with a context in a different case to an already-cached value will result in a hit.
	 */
	@Test
	public void verifyCacheHitWithDifferentContextCaseDoesNotDelegateToSettingsReader() {
		final SettingValue expectedSettingValue = mockSettingValue(SETTING_CONTEXT);
		final String upperCaseContext = SETTING_CONTEXT.toUpperCase(Locale.getDefault());

		givenCacheReturnsSettingValue(SETTING_PATH, null, ImmutableMap.of(SETTING_CONTEXT, expectedSettingValue));

		final SettingValue settingValue = getRefreshStrategy().retrieveSetting(SETTING_PATH, upperCaseContext, getSettingsParameterString());

		assertThat(settingValue)
				.isSameAs(expectedSettingValue);

		verifyZeroInteractions(settingsReader);
	}

	/**
	 * Tests that the a call to populate the cache will succeed when the settingValue has a null context.
	 */
	@Test
	public void testCachePopulatesWithNullSettingValueContext() {
		final SettingValue contextualSettingValue = mockSettingValue(null);

		when(settingsReader.getSettingValues(SETTING_PATH)).thenReturn(ImmutableSet.of(contextualSettingValue));

		SettingCacheData settingCacheData = populateCache();

		assertThat(settingCacheData).isNotNull();

	}

	/**
	 * Creates a new, mocked, SettingValue.
	 *
	 * @return a mocked SettingValue instance
	 */
	protected SettingValue mockSettingValue() {
		return mock(SettingValue.class);
	}

	/**
	 * Creates a new, mocked, SettingValue.
	 *
	 * @param context the context for the mocked setting value
	 * @return a mocked SettingValue instance
	 */
	protected SettingValue mockSettingValue(final String context) {
		final SettingValue settingValue = mock(SettingValue.class);

		when(settingValue.getContext()).thenReturn(context);

		return settingValue;
	}


	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}


	/**
	 * Calls the concrete implementation to populate the cache.
	 * @return the SettingsCacheData.
	 */
	protected abstract SettingCacheData populateCache();
}