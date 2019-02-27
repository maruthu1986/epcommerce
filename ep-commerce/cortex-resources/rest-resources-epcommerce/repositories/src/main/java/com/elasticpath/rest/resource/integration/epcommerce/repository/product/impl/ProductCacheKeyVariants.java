/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.product.impl;

import java.util.Arrays;
import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.rest.cache.CacheKeyVariants;

/**
 * Generates cache keys from class name and parameters.
 */
@Singleton
@Named("productCacheKeyVariants")
public class ProductCacheKeyVariants implements CacheKeyVariants<Product> {

	@Override
	public Collection<Object[]> get(final Product objectToCache) {
		return Arrays.asList(
				new Object[]{objectToCache.getUidPk()},
				new Object[]{objectToCache.getGuid()}
		);
	}

	@Override
	public Class<Product> getType() {
		return Product.class;
	}
}
