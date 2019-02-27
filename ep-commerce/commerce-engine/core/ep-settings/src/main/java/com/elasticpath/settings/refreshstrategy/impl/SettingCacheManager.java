/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.refreshstrategy.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.MoreObjects;

import com.elasticpath.settings.domain.SettingValue;

/**
 * Capabilities required by a cache backing a {@link com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy SettingRefreshStrategy}.
 */
public interface SettingCacheManager {

	/**
	 * Gets the backing cache for {@link SettingValue}s.
	 *
	 * @param parameters the refresh strategy parameters
	 * @return the map backing the cache
	 */
	Map<String, SettingCacheData> getCache(Map<String, String> parameters);

	/**
	 * Retrieve a setting value from the cache, if it exists.
	 *
	 * @param path       the setting path
	 * @param context    the setting context
	 * @param parameters the refresh strategy parameters
	 * @return an optional SettingValue
	 */
	default Optional<SettingValue> getFromCache(String path, String context, Map<String, String> parameters) {
		return Optional.ofNullable(getCache(parameters).get(path))
				.map(settingCacheData -> settingCacheData.getSettingValue(context));
	}

	/**
	 * Adds setting values to the cache.
	 *
	 * @param path                the path of the setting
	 * @param defaultSettingValue the default setting value
	 * @param settingValueMap     contextual setting values
	 * @param parameters          the refresh strategy parameters
	 */
	default void putInCache(String path,
							SettingValue defaultSettingValue,
							Map<String, SettingValue> settingValueMap, Map<String, String> parameters) {
		getCache(parameters).put(path,
				new SettingCacheData(path, defaultSettingValue, MoreObjects.firstNonNull(settingValueMap, Collections.emptyMap())));
	}

	/**
	 * Adds setting data to the cache.
	 *
	 * @param settingCacheData the setting cache data
	 * @param parameters       the refresh strategy parameters
	 */
	default void putInCache(SettingCacheData settingCacheData, Map<String, String> parameters) {
		getCache(parameters).put(settingCacheData.getPath(), settingCacheData);
	}

}
