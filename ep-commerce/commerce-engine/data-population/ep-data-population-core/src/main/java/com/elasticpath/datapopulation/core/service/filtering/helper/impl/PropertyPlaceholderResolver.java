/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering.helper.impl;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * A helper class which extends Spring's {@link PropertyPlaceholderConfigurer} to make use of Spring's property placeholder resolution
 * capabilities, which includes resolving nested property placeholders. Out of the box it Spring's property placeholder resolution isn't readily
 * accessible for our use, so this sub-class provides that access.
 */
class PropertyPlaceholderResolver extends PropertyPlaceholderConfigurer implements PropertyPlaceholderHelper.PlaceholderResolver {
	private Properties properties;
	private int systemPropertiesMode;

	/**
	 * Constructor which takes in the configuration values required.
	 *
	 * @param properties           the source properties to resolve property placeholders with.
	 * @param placeholderPrefix    the property placeholder prefix to resolve.
	 * @param placeholderSuffix    the property placeholder suffix to resolve.
	 * @param systemPropertiesMode the system properties mode. Valid values should be obtained from {@link PropertyPlaceholderConfigurer}
	 *                             constants.
	 */
	PropertyPlaceholderResolver(final Properties properties, final String placeholderPrefix,
								final String placeholderSuffix, final int systemPropertiesMode) {
		this.properties = properties;
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
		this.systemPropertiesMode = systemPropertiesMode;
	}

	@Override
	public String resolvePlaceholder(final String placeholderName) {
		return resolvePlaceholder(placeholderName, getProperties(), getSystemPropertiesMode());
	}

	// Getters and Setters

	/**
	 * Gets the source properties to resolve property placeholders with.
	 *
	 * @return the source properties to resolve property placeholders with.
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * Sets the source properties to resolve property placeholders with.
	 *
	 * @param properties the source properties to resolve property placeholders with.
	 */
	@Override
	public void setProperties(final Properties properties) {
		this.properties = properties;
	}

	/**
	 * Gets the system properties mode to use. Valid values should be obtained from {@link PropertyPlaceholderConfigurer} constants.
	 *
	 * @return the system properties mode to use. Valid values should be obtained from {@link PropertyPlaceholderConfigurer} constants.
	 */
	public int getSystemPropertiesMode() {
		return this.systemPropertiesMode;
	}

	/**
	 * Sets the system properties mode to use. Valid values should be obtained from {@link PropertyPlaceholderConfigurer} constants.
	 *
	 * @param systemPropertiesMode the system properties mode to use. Valid values should be obtained from {@link PropertyPlaceholderConfigurer}
	 *                             constants.
	 */
	@Override
	public void setSystemPropertiesMode(final int systemPropertiesMode) {
		super.setSystemPropertiesMode(systemPropertiesMode);
		this.systemPropertiesMode = systemPropertiesMode;
	}
}
