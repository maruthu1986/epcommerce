/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.StringValueResolver;

import com.elasticpath.datapopulation.core.service.filtering.helper.PropertyResourceLoader;
import com.elasticpath.datapopulation.core.service.filtering.helper.impl.PropertyPlaceholderStringValueResolver;
import com.elasticpath.datapopulation.core.service.filtering.helper.impl.PropertyResourceLoaderImpl;

/**
 * This factory class creates a {@link Properties} object generated from combining and filtering source {@link Properties} object(s).
 * The filtering is done by filtering property placeholders. The format is customizable, but by default using Spring's standard ${...} notation.
 * The filtering uses source {@link Properties} object(s) to provide the values for any placeholders in the {@link Properties} objects to filter.
 * Optionally the original source {@link Properties} objects can be merged into the generated {@link Properties} object.
 */
@SuppressWarnings("PMD.GodClass")
public class FilteredPropertiesFactory implements FactoryBean<Properties> {

	private Properties sourceProperties;
	private List<Resource> sourceLocations;

	private Properties propertiesToFilter;
	private List<Resource> locationsToFilter;

	private String placeholderPrefix;
	private String placeholderSuffix;
	private int systemPropertiesMode = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK;
	private boolean includeSourceProperties;
	private boolean ignoreSourceLocationNotFound;
	private boolean ignoreLocationToFilterNotFound;

	private PropertyResourceLoader propertyResourceLoader;

	// FactoryBean methods

	/**
	 * Loads the configured {@link Properties} objects and {@link Resource}s to filter into a single {@link Properties} object and filters it
	 * replacing property placeholders using the prefix and suffix configured using the configured source {@link Properties} objects
	 * {@link Resource}s before returning it. Depending on if {@link #isIncludeSourceProperties()} is true, the source properties may also be
	 * included
	 * in the returned {@link Properties} object.
	 *
	 * @return a filtered {@link Properties} object as described above.
	 * @throws IOException if the source properties {@link Resource}s cannot be loaded.
	 */
	@Override
	public Properties getObject() throws IOException {
		final Properties result = new Properties();

		final Properties givenSourceProperties = getSourceProperties();
		final Properties givenPropertiesToFilter = getPropertiesToFilter();

		final Properties sourceProperties = new Properties();
		final Properties propertiesToFilter = new Properties();

		// The source and filter Properties objects are the ones passed in directly
		if (givenSourceProperties != null && !givenSourceProperties.isEmpty()) {
			sourceProperties.putAll(givenSourceProperties);
		}
		if (givenPropertiesToFilter != null && !givenPropertiesToFilter.isEmpty()) {
			propertiesToFilter.putAll(givenPropertiesToFilter);
		}

		// As well as any Properties loaded from Resource locations
		final Properties propertiesFromSourceLocations = getPropertiesFromSourceLocations();
		if (MapUtils.isNotEmpty(propertiesFromSourceLocations)) {
			sourceProperties.putAll(propertiesFromSourceLocations);
		}

		final Properties propertiesFromLocationsToFilter = getPropertiesFromLocationsToFilter();
		if (MapUtils.isNotEmpty(propertiesFromLocationsToFilter)) {
			propertiesToFilter.putAll(propertiesFromLocationsToFilter);
		}

		// Before filtering, add the source properties if requested.
		// Adding them before filtering means that filtered values will take precedence
		if (isIncludeSourceProperties()) {
			result.putAll(sourceProperties);
		}

		// Finally if we have any properties to filter, then filter and add them
		if (!propertiesToFilter.isEmpty()) {
			filterProperties(propertiesToFilter, sourceProperties, result);
		}

		return result;
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	// Implementation methods

	/**
	 * Filters the propertiesToFilter with the collection of source {@link Properties} objects, writing the filtered properties to the destination
	 * {@link Properties} object.
	 *
	 * @param propertiesToFilter the unfiltered properties to filter.
	 * @param sourceProperties   a collection of {@link Properties} objects which are searched in turn to resolve each unfiltered property key and
	 *                           value.
	 * @param destination        the properties object to write the filtered keys and values to.
	 */
	public void filterProperties(final Properties propertiesToFilter, final Properties sourceProperties, final Map<Object, Object> destination) {
		if (!propertiesToFilter.isEmpty()) {
			if (sourceProperties.isEmpty()) {
				// No source properties so we can't filter, so just add in the unfiltered properties
				destination.putAll(propertiesToFilter);
			} else {
				final StringValueResolver resolver = createPropertyResolver(sourceProperties);
				final Set<String> propertyKeysToFilter = propertiesToFilter.stringPropertyNames();

				for (String unfilteredPropertyKey : propertyKeysToFilter) {
					// Filter both the key and the value
					final String filteredPropertyKey = resolver.resolveStringValue(unfilteredPropertyKey);
					final String filteredPropertyValue = resolver.resolveStringValue(propertiesToFilter.getProperty(unfilteredPropertyKey));

					// Before adding them to the destination Properties object
					destination.put(filteredPropertyKey, filteredPropertyValue);
				}
			}
		}
	}

	// Helper methods

	/**
	 * Loads the source properties {@link Resource}s as specified by {@link #getSourceLocations()} into a single {@link Properties} object which is
	 * returned.
	 *
	 * @return a {@link Properties} object which contains all property mappings from the source {@link Resource}s.
	 * @throws IOException if there was a problem reading any of the source {@link Resource}s.
	 */
	public Properties getPropertiesFromSourceLocations() throws IOException {
		return getPropertyResourceLoader().loadProperties(isIgnoreSourceLocationNotFound(), getSourceLocations());
	}

	/**
	 * Loads the properties-to-filter {@link Resource}s as specified by {@link #getLocationsToFilter()} into a single {@link Properties} object which
	 * is returned.
	 *
	 * @return a {@link Properties} object which contains all property mappings from the properties-to-filter {@link Resource}s.
	 * @throws IOException if there was a problem reading any of the {@link Resource}s.
	 */
	public Properties getPropertiesFromLocationsToFilter() throws IOException {
		return getPropertyResourceLoader().loadProperties(isIgnoreLocationToFilterNotFound(), getLocationsToFilter());
	}

	/**
	 * Returns the default property placeholder prefix if one hasn't been explicitly configured.
	 *
	 * @return {@link PropertyPlaceholderConfigurer#DEFAULT_PLACEHOLDER_PREFIX}.
	 */
	protected String getDefaultPlaceholderPrefix() {
		return PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_PREFIX;
	}

	/**
	 * Returns the default property placeholder suffix if one hasn't been explicitly configured.
	 *
	 * @return {@link PropertyPlaceholderConfigurer#DEFAULT_PLACEHOLDER_SUFFIX}.
	 */
	protected String getDefaultPlaceholderSuffix() {
		return PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_SUFFIX;
	}

	// Factory methods

	/**
	 * Creates a {@link StringValueResolver} that resolves property placeholders using the given {@link Properties} objects.
	 * In practice, this method delegates to {@link #createPropertyResolver()} before adding the the given {@link Properties} objects to it.
	 *
	 * @param properties {@link Properties} objects that are used by the returned {@link StringValueResolver} to resolve
	 *                   property placeholders.
	 * @return a {@link StringValueResolver} that resolves property placeholders using the given {@link Properties} objects.
	 */
	protected StringValueResolver createPropertyResolver(final Properties properties) {
		final PropertyPlaceholderStringValueResolver result = createPropertyResolver();
		result.addProperties(properties);
		return result;
	}

	/**
	 * Creates a {@link PropertyPlaceholderStringValueResolver} using the configured property placeholder and system properties mode values.
	 *
	 * @return a {@link PropertyPlaceholderStringValueResolver} using the configured property placeholder and system properties mode values.
	 * @see #getPlaceholderPrefix()
	 * @see #getPlaceholderSuffix()
	 * @see #getSystemPropertiesMode()
	 */
	protected PropertyPlaceholderStringValueResolver createPropertyResolver() {
		return new PropertyPlaceholderStringValueResolver(getPlaceholderPrefix(), getPlaceholderSuffix(), getSystemPropertiesMode(), true);
	}

	/**
	 * Creates a {@link PropertyResourceLoader} to use if one hasn't been explicitly set in
	 * {@link #setPropertyResourceLoader(com.elasticpath.datapopulation.core.service.filtering.helper.PropertyResourceLoader)}.
	 *
	 * @return a {@link PropertyResourceLoader} to use if one hasn't been explicitly set.
	 */
	protected PropertyResourceLoader createDefaultPropertyResourceLoader() {
		return new PropertyResourceLoaderImpl();
	}

	// Getters and Setters

	/**
	 * Gets the source {@link Properties} objects to filter with.
	 *
	 * @return the source {@link Properties} objects to filter with.
	 */
	protected Properties getSourceProperties() {
		return this.sourceProperties;
	}

	/**
	 * Sets the source {@link Properties} objects to filter with.
	 *
	 * @param sourceProperties the source {@link Properties} objects to filter with.
	 */
	public void setSourceProperties(final Properties sourceProperties) {
		this.sourceProperties = sourceProperties;
	}

	/**
	 * Gets the source properties {@link Resource} locations to filter with.
	 *
	 * @return the source properties {@link Resource} locations to filter with.
	 */
	protected List<Resource> getSourceLocations() {
		return this.sourceLocations;
	}

	/**
	 * Sets the source properties {@link Resource} locations to filter with.
	 *
	 * @param sourceLocations the source properties {@link Resource} locations to filter with.
	 */
	public void setSourceLocations(final List<Resource> sourceLocations) {
		this.sourceLocations = sourceLocations;
	}

	/**
	 * Gets the {@link Properties} objects to filter.
	 *
	 * @return the {@link Properties} objects to filter.
	 */
	protected Properties getPropertiesToFilter() {
		return this.propertiesToFilter;
	}

	/**
	 * Sets the {@link Properties} objects to filter.
	 *
	 * @param propertiesToFilter the {@link Properties} objects to filter.
	 */
	public void setPropertiesToFilter(final Properties propertiesToFilter) {
		this.propertiesToFilter = propertiesToFilter;
	}

	/**
	 * Gets the {@link Resource} locations to filter.
	 *
	 * @return the {@link Resource} locations to filter.
	 */
	protected List<Resource> getLocationsToFilter() {
		return this.locationsToFilter;
	}

	/**
	 * Sets the {@link Resource} locations to filter.
	 *
	 * @param locations the {@link Resource} locations to filter.
	 */
	public void setLocationsToFilter(final List<Resource> locations) {
		this.locationsToFilter = locations;
	}

	/**
	 * Gets the property placeholder prefix to use. If one has not been explicitly set, {@link #getDefaultPlaceholderPrefix()} is called and its
	 * value is returned.
	 *
	 * @return the property placeholder prefix to use.
	 */
	public String getPlaceholderPrefix() {
		if (this.placeholderPrefix == null) {
			this.placeholderPrefix = getDefaultPlaceholderPrefix();
		}
		return this.placeholderPrefix;
	}

	/**
	 * Sets the property placeholder prefix to use.
	 *
	 * @param placeholderPrefix the property placeholder prefix to use.
	 */
	public void setPlaceholderPrefix(final String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * Gets the property placeholder suffix to use. If one has not been explicitly set, {@link #getDefaultPlaceholderSuffix()} is called and its
	 * value is returned.
	 *
	 * @return the property placeholder suffix to use.
	 */
	public String getPlaceholderSuffix() {
		if (this.placeholderSuffix == null) {
			this.placeholderSuffix = getDefaultPlaceholderSuffix();
		}
		return this.placeholderSuffix;
	}

	/**
	 * Sets the property placeholder suffix to use.
	 *
	 * @param placeholderSuffix the property placeholder prefix to use.
	 */
	public void setPlaceholderSuffix(final String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Gets the system properties mode to use when resolving property placeholders. The valid values are defined in
	 * {@link PropertyPlaceholderConfigurer}'s constant values.
	 *
	 * @return the system properties mode to use when resolving property placeholders.
	 */
	public int getSystemPropertiesMode() {
		return this.systemPropertiesMode;
	}

	/**
	 * Sets the system properties mode to use when resolving property placeholders. The valid values are defined in
	 * {@link PropertyPlaceholderConfigurer}'s constant values.
	 *
	 * @param systemPropertiesMode the system properties mode to use when resolving property placeholders.
	 */
	public void setSystemPropertiesMode(final int systemPropertiesMode) {
		this.systemPropertiesMode = systemPropertiesMode;
	}

	/**
	 * Returns whether the source properties configured (either by {@link #getSourceProperties()} or loaded from {@link #getSourceLocations()},
	 * should be included in the generated filtered {@link Properties} object by {@link #getObject()}. Note, if true, the source properties are
	 * included unfiltered.
	 *
	 * @return true if the source properties configured (either by {@link #getSourceProperties()} or loaded from {@link #getSourceLocations()},
	 * should be included in the generated filtered {@link Properties} object; false otherwise.
	 */
	public boolean isIncludeSourceProperties() {
		return this.includeSourceProperties;
	}

	/**
	 * Sets whether the source properties configured (either by {@link #getSourceProperties()} or loaded from {@link #getSourceLocations()},
	 * should be included in the generated filtered {@link Properties} object. Note, if true, the source properties are included unfiltered.
	 *
	 * @param includeSourceProperties whether the source properties configured (either by {@link #getSourceProperties()} or loaded from
	 *                                {@link #getSourceLocations()}, should be included in the generated filtered {@link Properties} object
	 */
	public void setIncludeSourceProperties(final boolean includeSourceProperties) {
		this.includeSourceProperties = includeSourceProperties;
	}

	/**
	 * Returns whether this object should ignore any {@link Resource} objects that cannot be found when returned by {@link #getSourceLocations()}.
	 *
	 * @return true if this object should ignore any {@link Resource} objects that cannot be found when returned by {@link #getSourceLocations()};
	 * false otherwise.
	 */
	public boolean isIgnoreSourceLocationNotFound() {
		return this.ignoreSourceLocationNotFound;
	}

	/**
	 * Sets whether this object should ignore any {@link Resource} objects that cannot be found when returned by {@link #getSourceLocations()}.
	 *
	 * @param ignoreSourceLocationNotFound sets if this object should ignore any {@link Resource} objects that cannot be found when returned by
	 *                                     {@link #getSourceLocations()}.
	 */
	public void setIgnoreSourceLocationNotFound(final boolean ignoreSourceLocationNotFound) {
		this.ignoreSourceLocationNotFound = ignoreSourceLocationNotFound;
	}

	/**
	 * Returns whether this object should ignore any {@link Resource} objects that cannot be found when returned by {@link #getLocationsToFilter()}.
	 *
	 * @return true if this object should ignore any {@link Resource} objects that cannot be found when returned by {@link #getLocationsToFilter()};
	 * false otherwise.
	 */
	public boolean isIgnoreLocationToFilterNotFound() {
		return this.ignoreLocationToFilterNotFound;
	}

	/**
	 * Sets whether this object should ignore any {@link Resource} objects that cannot be found when returned by {@link #getLocationsToFilter()}.
	 *
	 * @param ignoreLocationToFilterNotFound sets if this object should ignore any {@link Resource} objects that cannot be found when returned by
	 *                                       {@link #getLocationsToFilter()}.
	 */
	public void setIgnoreLocationToFilterNotFound(final boolean ignoreLocationToFilterNotFound) {
		this.ignoreLocationToFilterNotFound = ignoreLocationToFilterNotFound;
	}

	/**
	 * Gets the {@link PropertyResourceLoader} to use. If not set, {@link #createDefaultPropertyResourceLoader()} is called and its value is set and
	 * returned.
	 *
	 * @return the {@link PropertyResourceLoader} to use; never null.
	 */
	protected PropertyResourceLoader getPropertyResourceLoader() {
		if (this.propertyResourceLoader == null) {
			this.propertyResourceLoader = createDefaultPropertyResourceLoader();
		}
		return this.propertyResourceLoader;
	}

	/**
	 * Sets the {@link PropertyResourceLoader} to use by this object.
	 *
	 * @param propertyResourceLoader the {@link PropertyResourceLoader} to use by this object.
	 */
	public void setPropertyResourceLoader(final PropertyResourceLoader propertyResourceLoader) {
		this.propertyResourceLoader = propertyResourceLoader;
	}
}
