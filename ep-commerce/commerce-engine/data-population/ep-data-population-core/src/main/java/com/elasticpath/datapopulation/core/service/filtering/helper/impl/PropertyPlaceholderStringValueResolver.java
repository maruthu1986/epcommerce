/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering.helper.impl;

import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;

import com.elasticpath.datapopulation.core.service.filtering.helper.PropertiesStringValueResolver;

/**
 * This class, and its nested {@link PropertyPlaceholderResolver} class are used by
 * {@link com.elasticpath.datapopulation.core.service.filtering.FilteredPropertiesFactory} to created a filtered
 * Properties file. They are based on {@link PropertyPlaceholderConfigurer}'s inner classes which are private and so not available for reuse.
 */
public class PropertyPlaceholderStringValueResolver implements PropertiesStringValueResolver {
	private final PropertyPlaceholderHelper helper;
	private final PropertyPlaceholderResolver resolver;

	/**
	 * Constructor which configures the property placeholder values, system properties mode and whether unresolvable placeholders should be ignored,
	 * otherwise an exception will be thrown.
	 *
	 * @param placeholderPrefix              the property placeholder prefix to use.
	 * @param placeholderSuffix              the property placeholder suffix to use.
	 * @param systemPropertiesMode           the system properties mode. Valid values should be obtained from {@link PropertyPlaceholderConfigurer}
	 *                                       constants.
	 * @param ignoreUnresolvablePlaceholders if unresolved placeholders should be skipped over, otherwise an exception is thrown.
	 */
	public PropertyPlaceholderStringValueResolver(final String placeholderPrefix, final String placeholderSuffix,
												  final int systemPropertiesMode, final boolean ignoreUnresolvablePlaceholders) {
		this.helper = createPropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, ignoreUnresolvablePlaceholders);
		this.resolver = createPropertyPlaceholderResolver(placeholderPrefix, placeholderSuffix, systemPropertiesMode);
	}

	@Override
	public String resolveStringValue(final String stringValue) throws BeansException {
		return this.helper.replacePlaceholders(stringValue, this.resolver);
	}

	@Override
	public void addProperties(final Properties overridingProperties) {
		if (MapUtils.isNotEmpty(overridingProperties)) {
			getResolver().getProperties().putAll(overridingProperties);
		}
	}

	// Factory methods

	/**
	 * Creates a {@link PropertyPlaceholderHelper} object using the given property placeholder values and whether unresolved placeholders should be
	 * ignored.
	 *
	 * @param placeholderPrefix              the property placeholder prefix to use.
	 * @param placeholderSuffix              the property placeholder suffix to use.
	 * @param ignoreUnresolvablePlaceholders if unresolved placeholders should be skipped over, otherwise an exception is thrown.
	 * @return {@link PropertyPlaceholderHelper} object using the given property placeholder values and whether unresolved placeholders should be
	 * ignored.
	 */
	protected final PropertyPlaceholderHelper createPropertyPlaceholderHelper(final String placeholderPrefix, final String placeholderSuffix,
																			  final boolean ignoreUnresolvablePlaceholders) {
		return new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, PropertyPlaceholderConfigurer.DEFAULT_VALUE_SEPARATOR,
				ignoreUnresolvablePlaceholders);
	}

	/**
	 * Creates a {@link PropertyPlaceholderResolver} object using the given property placeholder values and system properties mode.
	 *
	 * @param placeholderPrefix    the property placeholder prefix to use.
	 * @param placeholderSuffix    the property placeholder suffix to use.
	 * @param systemPropertiesMode the system properties mode. Valid values should be obtained from {@link PropertyPlaceholderConfigurer} constants.
	 * @return a {@link PropertyPlaceholderResolver} object using the given property placeholder values and system properties mode.
	 */
	protected final PropertyPlaceholderResolver createPropertyPlaceholderResolver(final String placeholderPrefix, final String placeholderSuffix,
																				  final int systemPropertiesMode) {
		return new PropertyPlaceholderResolver(new Properties(), placeholderPrefix, placeholderSuffix, systemPropertiesMode);
	}

	protected PropertyPlaceholderHelper getHelper() {
		return this.helper;
	}

	protected PropertyPlaceholderResolver getResolver() {
		return this.resolver;
	}
}
