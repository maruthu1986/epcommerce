/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import java.util.Arrays;
import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.cache.CacheKeyVariants;

/**
 * Generates cache keys from class name and parameters.
 */
@Singleton
@Named("cartOrderCacheKeyVariants")
public class CartOrderCacheKeyVariants implements CacheKeyVariants<CartOrder> {

	@Override
	public Collection<Object[]> get(final CartOrder objectToCache) {
		return Arrays.asList(
				new Object[] {objectToCache.getGuid()},
				new Object[] {objectToCache.getShoppingCartGuid()}
		);
	}

	@Override
	public Class<CartOrder> getType() {
		return CartOrder.class;
	}
}
