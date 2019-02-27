/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.refreshstrategy.impl;

import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Test class for {@link IntervalRefreshStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class IntervalRefreshStrategyImplTest extends AbstractSettingRefreshStrategyTestCases {

	private static final String TIMEOUT_PARAM_KEY = "timeout";
	private static final String TIMEOUT_VALUE_SETTING_PATH = "COMMERCE/Cache/Cache_1";

	private static final String SETTING_PARAMS = TIMEOUT_PARAM_KEY + "=" + TIMEOUT_VALUE_SETTING_PATH;
	private static final Map<String, String> SETTING_PARAMS_MAP = ImmutableMap.of(TIMEOUT_PARAM_KEY, TIMEOUT_VALUE_SETTING_PATH);

	private Map<String, SettingCacheData> intervalTimeoutCache;

	@Spy
	@InjectMocks
	private IntervalRefreshStrategyImpl refreshStrategy;

	@Before
	public void setUp() {
		intervalTimeoutCache = new HashMap<>();

		refreshStrategy.setTimeoutParamKey(TIMEOUT_PARAM_KEY);

		doReturn(intervalTimeoutCache).when(refreshStrategy).getCache(SETTING_PARAMS_MAP);
	}

	@Before
	@After
	public void clearCaches() {
		refreshStrategy.clearAllCaches();
	}

	@Override
	protected void givenCacheReturnsSettingValue(final String settingPath,
												 final SettingValue defaultSettingValue,
												 final Map<String, SettingValue> contextualSettingValues) {
		intervalTimeoutCache.put(settingPath, new SettingCacheData(settingPath, defaultSettingValue, contextualSettingValues));
	}

	@Override
	protected Optional<SettingValue> getFromCache(final String settingPath,
												  final String settingContext,
												  final Map<String, String> settingsParameterMap) {
		return refreshStrategy.getFromCache(settingPath, settingContext, settingsParameterMap);
	}

	@Override
	protected SettingCacheData populateCache() {
		return refreshStrategy.populateCache(SETTING_PATH, getSettingsParameterMap());
	}

	@Override
	protected SettingRefreshStrategy getRefreshStrategy() {
		return refreshStrategy;
	}

	@Override
	protected String getSettingsParameterString() {
		return SETTING_PARAMS;
	}

	@Override
	protected Map<String, String> getSettingsParameterMap() {
		return SETTING_PARAMS_MAP;
	}

}