/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationLoader;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationParser;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Loads FilteredNavigationConfiguration (FNC) String from the SettingsService,
 * parses it with the injected parser, and populates a new FNC object. Assumes
 * that the SettingsService will return the String encoded as UTF-8.
 */
public class FilteredNavigationConfigurationLoaderImpl implements FilteredNavigationConfigurationLoader {

	private BeanFactory beanFactory;
	
	/** Injected parser implementation that knows how to parse the configuration document. */
	private FilteredNavigationConfigurationParser parser;
	
	private final Map<String, Pair<String, FilteredNavigationConfiguration>> fncCache =
		new HashMap<>();
	
	private SettingValueProvider<String> separatorInTokenProvider;

	private SettingValueProvider<String> configurationProvider;

	@Override
	public FilteredNavigationConfiguration loadFilteredNavigationConfiguration(final String storeCode) {
		// this method wasn't put in a spring init-method, due to the cyclic dependencies, that affects the loading
		// of the jpaPersistenceEngine.
		final String fncString = getFncString(storeCode);
		Pair<String, FilteredNavigationConfiguration> cachedValue = fncCache.get(storeCode);
		if (cachedValue == null || !fncString.equals(cachedValue.getFirst())) {
			FilteredNavigationConfiguration fnc = createConfigFromString(fncString);
			cachedValue = new Pair<>(fncString, fnc);
			fncCache.put(storeCode, cachedValue);
		}
		return cachedValue.getSecond();
	}

	private FilteredNavigationConfiguration createConfigFromString(final String fncString) {
		FilteredNavigationConfiguration fnc = createFnc();
		InputStream configStream = getInputStreamFromString(fncString);
		parser.parse(configStream, fnc);
		return fnc;
	}
	
	/**
	 * Converts the given String into an InputStream.
	 * @param fncString the string from which to get an InputStream
	 * @return the inputStream
	 */
	InputStream getInputStreamFromString(final String fncString) {
		return new ByteArrayInputStream(fncString.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Retrieves the FilteredNavigation configuration as a String.
	 * @param storeCode the code representing the store for which the FNC should be retrieved
	 * @return the FNC XML document as a String.
	 */
	String getFncString(final String storeCode) {
		return getConfigurationProvider().get(storeCode);
	}

	/**
	 * Creates a new FNC object using the bean factory.
	 * @return the new FNC object
	 */
	FilteredNavigationConfiguration createFnc() {
		return beanFactory.getBean("filteredNavigationConfiguration");
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the parser
	 */
	public FilteredNavigationConfigurationParser getParser() {
		return parser;
	}

	/**
	 * @param parser the parser to set
	 */
	public void setParser(final FilteredNavigationConfigurationParser parser) {
		this.parser = parser;
	}

	@Override
	public String getSeparatorInToken() {
		return getSeparatorInTokenProvider().get();
	}

	protected SettingValueProvider<String> getSeparatorInTokenProvider() {
		return separatorInTokenProvider;
	}

	public void setSeparatorInTokenProvider(final SettingValueProvider<String> separatorInTokenProvider) {
		this.separatorInTokenProvider = separatorInTokenProvider;
	}

	protected SettingValueProvider<String> getConfigurationProvider() {
		return configurationProvider;
	}

	public void setConfigurationProvider(final SettingValueProvider<String> configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

}
