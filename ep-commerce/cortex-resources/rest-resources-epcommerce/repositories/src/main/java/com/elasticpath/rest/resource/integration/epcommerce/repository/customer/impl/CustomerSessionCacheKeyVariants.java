/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.ArrayUtils;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.rest.cache.CacheKeyVariants;

/**
 * Generates cache keys from class name and parameters.
 */
@Singleton
@Named("customerSessionCacheKeyVariants")
public class CustomerSessionCacheKeyVariants implements CacheKeyVariants<CustomerSession> {

	@Override
	public Collection<Object[]> get(final CustomerSession objectToCache) {
		Shopper shopper = objectToCache.getShopper();
		return Arrays.asList(
				ArrayUtils.EMPTY_OBJECT_ARRAY,
				new Object[] { shopper.getStoreCode(), shopper.getCustomer().getGuid() }
		);
	}

	@Override
	public Class<CustomerSession> getType() {
		return CustomerSession.class;
	}
}
