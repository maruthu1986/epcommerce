/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering.helper.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.Resource;

import com.elasticpath.datapopulation.core.service.filtering.helper.PropertyResourceLoader;

/**
 * Standard implementation of the {@link PropertyResourceLoader} interface.
 */
public class PropertyResourceLoaderImpl implements PropertyResourceLoader {
	@Override
	public Properties loadProperties(final boolean ignoreResourceNotFound, final Collection<Resource> resources) throws IOException {
		return loadProperties(ignoreResourceNotFound, convertToArray(resources));
	}

	@Override
	public Properties loadProperties(final boolean ignoreResourceNotFound, final Resource... resources) throws IOException {
		return createPropertiesFactoryBean(ignoreResourceNotFound, resources).getObject();
	}

	// Helper methods

	/**
	 * Null-safe converter method to convert the given collection into an array. When given a null collection, an empty array will be returned.
	 *
	 * @param resources the {@link Collection} to convert to an array, may be null.
	 * @return an array containing the elements that the given {@link Collection} contains, or an empty array if a null {@link Collection} was
	 * passed.
	 */
	protected Resource[] convertToArray(final Collection<Resource> resources) {
		final Resource[] resourceArray;

		if (CollectionUtils.isNotEmpty(resources)) {
			resourceArray = resources.toArray(new Resource[resources.size()]);
		} else {
			resourceArray = new Resource[0];
		}
		return resourceArray;
	}

	/**
	 * Creates a {@link PropertiesFactoryBean} configured with the given parameter values.
	 *
	 * @param ignoreResourceNotFound if unresolvable {@link Resource} objects contained in the resources parameter should be skipped over rather
	 *                               than throw an exception.
	 * @param resources              the {@link Resource} objects to configure the returned {@link PropertiesFactoryBean} with.
	 * @return a {@link PropertiesFactoryBean} configured with the given parameter values.
	 */
	protected PropertiesFactoryBean createPropertiesFactoryBean(final boolean ignoreResourceNotFound, final Resource... resources) {
		final PropertiesFactoryBean result = new PropertiesFactoryBean();

		result.setIgnoreResourceNotFound(ignoreResourceNotFound);
		result.setLocations(resources);
		result.setSingleton(false);

		return result;
	}
}
