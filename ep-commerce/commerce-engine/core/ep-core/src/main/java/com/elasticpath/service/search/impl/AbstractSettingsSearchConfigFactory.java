/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.misc.SearchConfigInternal;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.SearchHostLocator;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Abstract class that uses the <code>SettingsService</code> to get a search configuration.
 */
public abstract class AbstractSettingsSearchConfigFactory implements SearchConfigFactory {

	private BeanFactory beanFactory;

	private SearchHostLocator hostLocator;

	private SettingValueProvider<BigDecimal> accuracyProvider;
	private SettingValueProvider<Map<String, String>> boostValuesProvider;
	private SettingValueProvider<Collection<String>> exclusiveAttributeListProvider;
	private SettingValueProvider<Integer> maximumResultsThresholdProvider;
	private SettingValueProvider<Integer> maximumReturnNumberProvider;
	private SettingValueProvider<Integer> maximumSuggestionsPerWordProvider;
	private SettingValueProvider<Integer> minimumResultsThresholdProvider;
	private SettingValueProvider<BigDecimal> minimumSimilarityProvider;
	private SettingValueProvider<Integer> prefixLengthProvider;

	/**
	 * Get the search configuration for the given search index name and settings context.
	 * 
	 * @param indexName the name of the index whose search configuration should be retrieved
	 * @param settingsContext the context to use for getting a setting value
	 * @return a search configuration
	 */
	public SearchConfig getSearchConfig(final String indexName, final String settingsContext) {
		final SearchConfigInternal searchConfig = getBeanFactory().getBean(ContextIdNames.SEARCH_CONFIG);
		
		searchConfig.setSearchHost(hostLocator.getSearchHostLocation());

		final int maxReturnNumber = getMaximumReturnNumberProvider().get(settingsContext);
		final BigDecimal minimumSimilarity = getMinimumSimilarityProvider().get(settingsContext);
		final int prefixLength = getPrefixLengthProvider().get(settingsContext);
		final int minimumResultsThreshold = getMinimumResultsThresholdProvider().get(settingsContext);
		final int maximumResultsThreshold = getMaximumResultsThresholdProvider().get(settingsContext);
		final int maximumSuggestionsPerWord = getMaximumSuggestionsPerWordProvider().get(settingsContext);
		final BigDecimal accuracy = getAccuracyProvider().get(settingsContext);
		final Map<String, Float> boostValues = createStringToFloatMap(getBoostValuesProvider().get(settingsContext));
		final Collection<String> exclusiveAttributeList = getExclusiveAttributeListProvider().get(settingsContext);

		searchConfig.setMaxReturnNumber(maxReturnNumber);
		searchConfig.setMinimumSimilarity(minimumSimilarity.floatValue());
		searchConfig.setPrefixLength(prefixLength);
		searchConfig.setMinimumResultsThreshold(minimumResultsThreshold);
		searchConfig.setMaximumResultsThreshold(maximumResultsThreshold);
		searchConfig.setMaximumSuggestionsPerWord(maximumSuggestionsPerWord);
		searchConfig.setAccuracy(accuracy.floatValue());
		searchConfig.setBoostValues(boostValues);
		searchConfig.setExclusiveAttributes(new HashSet<>(exclusiveAttributeList));

		return searchConfig;
	}

	private Map<String, Float> createStringToFloatMap(final Map<String, String> stringStringMap) {
		return stringStringMap.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> Float.valueOf(entry.getValue())));
	}

	/**
	 * Get the bean factory.
	 * 
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * Set the bean factory.
	 * 
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Set the locator providing the search host URL string.
	 * @param locator instance to use
	 */
	public void setSearchHostLocator(final SearchHostLocator locator) {
		this.hostLocator = locator;
	}

	protected SettingValueProvider<Integer> getMaximumReturnNumberProvider() {
		return maximumReturnNumberProvider;
	}

	public void setMaximumReturnNumberProvider(final SettingValueProvider<Integer> maximumReturnNumberProvider) {
		this.maximumReturnNumberProvider = maximumReturnNumberProvider;
	}

	protected SettingValueProvider<BigDecimal> getMinimumSimilarityProvider() {
		return minimumSimilarityProvider;
	}

	public void setMinimumSimilarityProvider(final SettingValueProvider<BigDecimal> minimumSimilarityProvider) {
		this.minimumSimilarityProvider = minimumSimilarityProvider;
	}

	protected SettingValueProvider<Integer> getPrefixLengthProvider() {
		return prefixLengthProvider;
	}

	public void setPrefixLengthProvider(final SettingValueProvider<Integer> prefixLengthProvider) {
		this.prefixLengthProvider = prefixLengthProvider;
	}

	protected SettingValueProvider<Integer> getMinimumResultsThresholdProvider() {
		return minimumResultsThresholdProvider;
	}

	public void setMinimumResultsThresholdProvider(final SettingValueProvider<Integer> minimumResultsThresholdProvider) {
		this.minimumResultsThresholdProvider = minimumResultsThresholdProvider;
	}

	protected SettingValueProvider<Integer> getMaximumResultsThresholdProvider() {
		return maximumResultsThresholdProvider;
	}

	public void setMaximumResultsThresholdProvider(final SettingValueProvider<Integer> maximumResultsThresholdProvider) {
		this.maximumResultsThresholdProvider = maximumResultsThresholdProvider;
	}

	protected SettingValueProvider<Integer> getMaximumSuggestionsPerWordProvider() {
		return maximumSuggestionsPerWordProvider;
	}

	public void setMaximumSuggestionsPerWordProvider(final SettingValueProvider<Integer> maximumSuggestionsPerWordProvider) {
		this.maximumSuggestionsPerWordProvider = maximumSuggestionsPerWordProvider;
	}

	protected SettingValueProvider<BigDecimal> getAccuracyProvider() {
		return accuracyProvider;
	}

	public void setAccuracyProvider(final SettingValueProvider<BigDecimal> accuracyProvider) {
		this.accuracyProvider = accuracyProvider;
	}

	protected SettingValueProvider<Map<String, String>> getBoostValuesProvider() {
		return boostValuesProvider;
	}

	public void setBoostValuesProvider(final SettingValueProvider<Map<String, String>> boostValuesProvider) {
		this.boostValuesProvider = boostValuesProvider;
	}

	protected SettingValueProvider<Collection<String>> getExclusiveAttributeListProvider() {
		return exclusiveAttributeListProvider;
	}

	public void setExclusiveAttributeListProvider(final SettingValueProvider<Collection<String>> exclusiveAttributeListProvider) {
		this.exclusiveAttributeListProvider = exclusiveAttributeListProvider;
	}

}
