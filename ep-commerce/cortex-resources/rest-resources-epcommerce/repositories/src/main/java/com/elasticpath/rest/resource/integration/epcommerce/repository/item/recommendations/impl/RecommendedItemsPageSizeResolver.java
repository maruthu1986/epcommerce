/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.impl;

import java.util.Dictionary;
import javax.inject.Named;
import javax.inject.Singleton;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.configadmin.ConfigurationUtil;

/**
 * Recommended items page size resolver.
 */
@Singleton
@Named("recommendedItemsPageSizeResolver")
public class RecommendedItemsPageSizeResolver implements ManagedService {

	private static final Logger LOG = LoggerFactory.getLogger(RecommendedItemsPageSizeResolver.class);
	/**
	 * Recommendations page size configuration property key.
	 */
	public static final String PAGE_SIZE_PROPERTY = "rest.recommendations.page.size.VALUE";

	private static final int DEFAULT_PAGE_SIZE = 5;
	private static final int RANGE_MIN = 1;
	private static final int RANGE_MAX = Short.MAX_VALUE;
	private static final String OUT_OF_RANGE_MESSAGE = String.format("Recommendations page size is out of range: %d-%d", RANGE_MIN, RANGE_MAX);

	private Integer pageSize = DEFAULT_PAGE_SIZE;

	/**
	 * This is called when the managed service configuration is updated.
	 *
	 * @param properties the managed service property values
	 * @throws ConfigurationException when the page size is out of range
	 */
	@Override
	public void updated(final Dictionary<String, ?> properties) throws ConfigurationException {

		Integer newPageSize = ConfigurationUtil.asInteger(PAGE_SIZE_PROPERTY, properties, DEFAULT_PAGE_SIZE);

		if (!isPageSizeInRange(newPageSize)) {
			LOG.warn(OUT_OF_RANGE_MESSAGE);
			throw new ConfigurationException(PAGE_SIZE_PROPERTY, OUT_OF_RANGE_MESSAGE);
		}

		pageSize = newPageSize;
	}

	/**
	 * Gets the page size configuration value.
	 *
	 * @return the page size.
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	private static boolean isPageSizeInRange(final int pageSize) {
		return pageSize >= RANGE_MIN && pageSize <= RANGE_MAX;
	}
}
