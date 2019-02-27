/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.category.impl;

import java.util.Arrays;
import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.rest.cache.CacheKeyVariants;

/**
 * Cache key variants for Category retrieval.
 */
@Singleton
@Named("categoryCacheKeyVariants")
public class CategoryCacheKeyVariants implements CacheKeyVariants<Category> {
	@Override
	public Collection<Object[]> get(final Category category) {
		return Arrays.asList(
				new Object[] {category.getGuid()},
				new Object[] {category.getCode(), category.getCatalog()}
		);
	}

	@Override
	public Class<Category> getType() {
		return Category.class;
	}
}
