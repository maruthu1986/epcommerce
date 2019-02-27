/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.refreshstrategy.impl;

import static java.util.function.Function.identity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Timeout cache type implementation of {@link SettingRefreshStrategy}.
 */
public class IntervalRefreshStrategyImpl implements SettingRefreshStrategy, SettingCacheManager {

	private static final Map<String, Map<String, SettingCacheData>> TIMEOUT_CACHE_MAP = new ConcurrentHashMap<>();

	private SettingsReader settingsReader;

	private String timeoutParamKey;

	@Override
	public SettingValue retrieveSetting(final String path, final String params) {
		return retrieveSetting(path, null, params);
	}

	@Override
	public SettingValue retrieveSetting(final String path, final String context, final String params) {
		final Map<String, String> parameters = parseParameters(params);

		final Optional<SettingValue> cachedValue = getFromCache(path, context, parameters);

		if (cachedValue.isPresent()) {
			return cachedValue.get();
		}

		final SettingCacheData settingCacheData = populateCache(path, parameters);

		return settingCacheData.getSettingValue(context);
	}

	/**
	 * <p>
	 * Builds a map of timeout caches with different intervals.
	 * </p>
	 * <p>
	 * These are keyed on the setting PATH of the timeout value, not the value itself.
	 * </p>
	 *
	 * @param parameters the refresh strategy parameters
	 * @return the cache
	 */
	@Override
	public Map<String, SettingCacheData> getCache(final Map<String, String> parameters) {
		final String timeoutCacheSettingPath = parameters.get(getTimeoutParamKey());
		return TIMEOUT_CACHE_MAP.computeIfAbsent(timeoutCacheSettingPath,
				settingPath -> createTimeoutCacheMap(getIntervalFromSetting(settingPath)));
	}

	/**
	 * Creates a map to be used as the backing timeout cache.
	 *
	 * @param timeoutMillis the timeout of each cached item
	 * @return a map to be used as the backing timeout cache
	 */
	protected Map<String, SettingCacheData> createTimeoutCacheMap(final long timeoutMillis) {
		final Cache<String, SettingCacheData> cache = CacheBuilder.newBuilder()
				.expireAfterWrite(timeoutMillis, TimeUnit.MILLISECONDS)
				.build();

		return cache.asMap();
	}

	/**
	 * Populates the cache with values retrieved from {@link SettingsReader}.
	 *
	 * @param path       the path of the setting with which to populate the cache
	 * @param parameters the refresh strategy parameters
	 * @return the cached settings data
	 */
	protected SettingCacheData populateCache(final String path, final Map<String, String> parameters) {
		final SettingValue fallbackSettingValue = getSettingsReader().getSettingValue(path);
		final Set<SettingValue> contextualSettingValues = getSettingsReader().getSettingValues(path);

		final Map<String, SettingValue> settingValueMap = contextualSettingValues.stream()
				.collect(Collectors.toMap(SettingValue::getContext, identity()));

		final SettingCacheData settingCacheData = new SettingCacheData(path, fallbackSettingValue, settingValueMap);

		putInCache(settingCacheData, parameters);

		return settingCacheData;
	}

	/**
	 * Parses the string of refresh strategy parameters into a <code>Map</code>. Assumes that parameters are separated by "&" signs and that
	 * key-value pairs are separated by "=" signs.
	 *
	 * @param params the string of refresh strategy parameters
	 * @return a <code>Map</code> of the parameters as key-value pairs
	 */
	Map<String, String> parseParameters(final String params) {
		final Map<String, String> parameters = new HashMap<>();
		final String[] paramArray = StringUtils.split(params, '&');
		for (final String param : paramArray) {
			parameters.put(StringUtils.substringBefore(param, "="), StringUtils.substringAfter(param, "="));
		}
		return parameters;
	}

	/**
	 * Gets the timeout interval from the cache setting path.
	 *
	 * @param path the cache setting path to get the interval from
	 * @return the cache refresh interval
	 */
	protected long getIntervalFromSetting(final String path) {
		return Long.parseLong(getSettingsReader().getSettingValue(path).getValue());
	}

	/**
	 * Clears all entries from all caches.
	 */
	@VisibleForTesting
	protected void clearAllCaches() {
		TIMEOUT_CACHE_MAP.values().forEach(Map::clear);
		TIMEOUT_CACHE_MAP.clear();
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

	/**
	 * Gets the key to use when looking for the timeout setting in the refresh strategy params.
	 *
	 * @return the timeout param key
	 */
	protected String getTimeoutParamKey() {
		return timeoutParamKey;
	}

	/**
	 * Sets the key to use when looking for the timeout setting in the refresh strategy params. This must be set for this refresh strategy to
	 * function properly.
	 *
	 * @param timeoutParamKey the key to use when looking for the timeout setting
	 */
	public void setTimeoutParamKey(final String timeoutParamKey) {
		this.timeoutParamKey = timeoutParamKey;
	}

}
