/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.refreshstrategy.impl;

import static java.util.function.Function.identity;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Implementation of {@link SettingRefreshStrategy} with entries that are never automatically evicted.
 */
public class ApplicationLifetimeRefreshStrategyImpl implements SettingRefreshStrategy, SettingCacheManager {

	private static final Map<String, SettingCacheData> CACHE_MAP = new ConcurrentHashMap<>();

	private SettingsReader settingsReader;

	@Override
	public SettingValue retrieveSetting(final String path, final String params) {
		return retrieveSetting(path, null, params);
	}

	@Override
	public SettingValue retrieveSetting(final String path, final String context, final String params) {
		final Optional<SettingValue> cachedValue = getFromCache(path, context, null);

		if (cachedValue.isPresent()) {
			return cachedValue.get();
		}

		final SettingCacheData settingCacheData = populateCache(path);

		return settingCacheData.getSettingValue(context);
	}

	@Override
	public Map<String, SettingCacheData> getCache(final Map<String, String> parameters) {
		return CACHE_MAP;
	}

	/**
	 * Populates the cache with values retrieved from {@link SettingsReader}.
	 *
	 * @param path the path of the setting with which to populate the cache
	 * @return the cached settings data
	 */
	protected SettingCacheData populateCache(final String path) {
		final SettingValue fallbackSettingValue = getSettingsReader().getSettingValue(path);
		final Set<SettingValue> contextualSettingValues = getSettingsReader().getSettingValues(path);

		final Map<String, SettingValue> settingValueMap = contextualSettingValues.stream()
				.collect(Collectors.toMap(SettingValue::getContext, identity()));

		final SettingCacheData settingCacheData = new SettingCacheData(path, fallbackSettingValue, settingValueMap);

		putInCache(settingCacheData, null);

		return settingCacheData;
	}

	/**
	 * @param settingsReader the settings reader to be used for retrieving the setting values
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * @return the settings reader to be used for retrieving the setting values
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

}
