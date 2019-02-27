/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.refreshstrategy.impl;

import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Test class for {@link ApplicationLifetimeRefreshStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class ApplicationLifetimeRefreshStrategyImplTest extends AbstractSettingRefreshStrategyTestCases {

	@InjectMocks
	private ApplicationLifetimeRefreshStrategyImpl refreshStrategy;

	@Before
	@After
	public void clearCache() {
		refreshStrategy.getCache(null).clear();
	}

	@Override
	protected SettingRefreshStrategy getRefreshStrategy() {
		return refreshStrategy;
	}

	@Override
	public void givenCacheReturnsSettingValue(final String settingPath,
											  final SettingValue defaultSettingValue,
											  final Map<String, SettingValue> contextualSettingValues) {
		refreshStrategy.putInCache(settingPath, defaultSettingValue, contextualSettingValues, null);
	}

	@Override
	public Optional<SettingValue> getFromCache(final String settingPath,
											   final String settingContext,
											   final Map<String, String> settingsParameterMap) {
		return refreshStrategy.getFromCache(settingPath, settingContext, settingsParameterMap);
	}

	// Setting parameters are unused for this refresh strategy.

	@Override
	protected String getSettingsParameterString() {
		return null;
	}

	@Override
	protected Map<String, String> getSettingsParameterMap() {
		return null;
	}


	@Override
	protected SettingCacheData populateCache() {
		return refreshStrategy.populateCache(SETTING_PATH);
	}


}
