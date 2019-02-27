/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.DisposableBean;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Wraps <code>SettingsService</code> to add caching to settings retrieval. Caching strategies are declared in the settings definitions themselves.
 * This service is responsible for:
 * <ul>
 * <li>Retrieval of cache strategies</li>
 * <li>Caching of strategies</li>
 * <li>Retrieval of setting from strategy</li>
 * </ul>
 * This service should rely on the strategies and not need to know anything about how to cache & retrieve settings. New strategy implementations can
 * be injected using the {@link CachedSettingsReaderImpl#setRefreshStrategies(Map)} method.
 */
public class CachedSettingsReaderImpl implements SettingsReader, DisposableBean {

	// Strategy cannot change during program execution
	private static final Map<String, ImmutablePair<String, SettingRefreshStrategy>> SETTING_DATA =
		new HashMap<>();

	private Map<String, SettingRefreshStrategy> refreshStrategies = new HashMap<>();

	private SettingsService settingsService;

	private String refreshStrategyKey;

	private SettingRefreshStrategy defaultRefreshStrategy;

	@Override
	public SettingValue getSettingValue(final String path, final String context) {
		final SettingRefreshStrategy strategy = getRefreshStrategy(path);
		return new ImmutableSettingValueImpl(strategy.retrieveSetting(path, context, SETTING_DATA.get(path).getLeft()));
	}

	@Override
	public SettingValue getSettingValue(final String path) {
		final SettingRefreshStrategy strategy = getRefreshStrategy(path);
		return new ImmutableSettingValueImpl(strategy.retrieveSetting(path, SETTING_DATA.get(path).getLeft()));
	}

	@Override
	public Set<SettingValue> getSettingValues(final String path, final String... contexts) {
		final SettingRefreshStrategy strategy = getRefreshStrategy(path);
		final Set<SettingValue> settingValues = new HashSet<>();
		for (final String context : contexts) {
			settingValues.add(new ImmutableSettingValueImpl(strategy.retrieveSetting(path, context, SETTING_DATA.get(path).getLeft())));
		}
		return settingValues;
	}

	/**
	 * Retrieve the refresh strategy for the specified setting. The strategy will be cached the first time it is loaded and remain the same until the
	 * application is restarted.
	 *
	 * @param path the setting definition path to get the refresh strategy for
	 * @return the refresh strategy for the specified setting
	 */
	protected SettingRefreshStrategy getRefreshStrategy(final String path) {
		return retrieveSettingData(path).getRight();
	}

	/**
	 * @param path the setting definition path to get the setting data for
	 * @return the cached setting data
	 */
	protected ImmutablePair<String, SettingRefreshStrategy> retrieveSettingData(final String path) {
		ImmutablePair<String, SettingRefreshStrategy> settingData = SETTING_DATA.get(path);
		if (settingData == null) {
			// NOTE: If you change the strategy in a setting's metadata, the changes will not be picked up here until the application is restarted
			final SettingDefinition settingDef = getSettingsService().getSettingDefinition(path);

			if (settingDef == null) {
				throw new EpServiceException("No setting definition exists for path [" + path + "]");
			}

			final SettingMetadata refreshStrategyMetadata = settingDef.getMetadata().get(getRefreshStrategyKey());
			SettingRefreshStrategy strategy = null;
			String refreshStrategyParams = null;

			if (refreshStrategyMetadata != null) {
				final Pair<String, String> refreshStrategyData = getMetadataParser().parse(refreshStrategyMetadata.getValue());
				strategy = refreshStrategies.get(refreshStrategyData.getLeft());
				refreshStrategyParams = refreshStrategyData.getRight();
			}
			if (strategy == null) {
				strategy = getDefaultRefreshStrategy();
			}
			settingData = new ImmutablePair<>(refreshStrategyParams, strategy);
			SETTING_DATA.put(path, settingData);
		}
		return settingData;
	}

	/**
	 * Set the available refresh strategies (see {@link SettingRefreshStrategy}) and the map keys used to select them.
	 *
	 * @param refreshStrategies the available refresh strategies
	 */
	public void setRefreshStrategies(final Map<String, SettingRefreshStrategy> refreshStrategies) {
		this.refreshStrategies = refreshStrategies;
	}

	/**
	 * @return the settingsService
	 */
	protected SettingsService getSettingsService() {
		return settingsService;
	}

	/**
	 * @param settingsService the settingsService to set
	 */
	public void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * Sets the key to use when attempting to retrieve the refresh strategy from the <code>SettingMetadata</code> for a given
	 * {@link SettingDefinition}. This must be set in order to determine which {@link SettingRefreshStrategy} to use.
	 *
	 * @param refreshStrategyKey the key to be used when querying the setting metadata for the setting's refresh strategy
	 */
	public void setRefreshStrategyKey(final String refreshStrategyKey) {
		this.refreshStrategyKey = refreshStrategyKey;
	}

	/**
	 * Gets the key to use when attempting to retrieve the refresh strategy from the <code>SettingMetadata</code> for a given
	 * {@link SettingDefinition}.
	 *
	 * @return the key to be used when querying the setting metadata for the setting's refresh strategy
	 */
	protected String getRefreshStrategyKey() {
		return refreshStrategyKey;
	}

	/**
	 * Gets the metadata parser to be used for parsing the refresh strategy metadata.
	 *
	 * @return the <code>RefreshStrategyMetadataParser</code> to be used for parsing
	 */
	protected RefreshStrategyMetadataParser getMetadataParser() {
		return new RefreshStrategyMetadataParser();
	}

	/**
	 * Parser for the refresh strategy metadata.
	 */
	protected static class RefreshStrategyMetadataParser {

		/**
		 * The string separating the refresh strategy type from any additional parameters.
		 */
		private static final String STRATEGY_METADATA_SEPARATOR = ":";

		/**
		 * Parses the metadata by splitting it on the first occurrence of {@link RefreshStrategyMetadataParser#getMetadataSeperator()}.
		 *
		 * @param metadata the metadata to parse
		 * @return the parsed data as a {@link Pair} with {@link Pair#getLeft()} returning everything before the separator and
		 *         {@link Pair#getRight()} returning everything after the separator.
		 */
		public Pair<String, String> parse(final String metadata) {
			final String refreshStrategyType = StringUtils.substringBefore(metadata, getMetadataSeperator());
			final String refreshStrategyParams = StringUtils.substringAfter(metadata, getMetadataSeperator());
			return new ImmutablePair<>(refreshStrategyType, refreshStrategyParams);
		}

		/**
		 * Get the separator string used to parse the metadata.
		 *
		 * @return the refresh strategy metadata separator
		 */
		protected String getMetadataSeperator() {
			return STRATEGY_METADATA_SEPARATOR;
		}
	}

	/**
	 * @return the defaultRefreshStrategy
	 */
	protected SettingRefreshStrategy getDefaultRefreshStrategy() {
		return defaultRefreshStrategy;
	}

	/**
	 * Sets the refresh strategy to be used when a setting definition does not specify one.
	 *
	 * @param defaultRefreshStrategy the defaultRefreshStrategy to set
	 */
	public void setDefaultRefreshStrategy(final SettingRefreshStrategy defaultRefreshStrategy) {
		this.defaultRefreshStrategy = defaultRefreshStrategy;
	}

	@Override
	public void destroy() throws Exception {
		SETTING_DATA.clear();
	}

	static Map<String, ImmutablePair<String, SettingRefreshStrategy>> getSettingData() {
		return SETTING_DATA;
	}
}
