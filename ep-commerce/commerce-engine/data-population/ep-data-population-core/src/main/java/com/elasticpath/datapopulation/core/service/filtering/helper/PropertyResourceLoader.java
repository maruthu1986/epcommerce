/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering.helper;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.springframework.core.io.Resource;

/**
 * Helper interface to allow a {@link Properties} object to be loaded from given {@link Resource} objects.
 */
public interface PropertyResourceLoader {
	/**
	 * Loads all the properties defined in the {@link Resource}s given, optionally skipping over {@link Resource} objects that cannot be found,
	 * else an exception is thrown instead.
	 *
	 * @param ignoreResourceNotFound if {@link Resource} objects that cannot be resolved should be skipped over, otherwise an exception is thrown.
	 * @param resources              the {@link Resource} objects to load.
	 * @return a {@link Properties} object containing all properties mappings from the {@link Resource} objects given.
	 * @throws IOException if there was a problem reading from any of the given {@link Resource}s.
	 */
	Properties loadProperties(boolean ignoreResourceNotFound, Resource... resources) throws IOException;

	/**
	 * Loads all the properties defined in the {@link Resource}s given, optionally skipping over {@link Resource} objects that cannot be found,
	 * else an exception is thrown instead.
	 *
	 * @param ignoreResourceNotFound if {@link Resource} objects that cannot be resolved should be skipped over, otherwise an exception is thrown.
	 * @param resources              the {@link Resource} objects to load.
	 * @return a {@link Properties} object containing all properties mappings from the {@link Resource} objects given.
	 * @throws IOException if there was a problem reading from any of the given {@link Resource}s.
	 */
	Properties loadProperties(boolean ignoreResourceNotFound, Collection<Resource> resources) throws IOException;
}
