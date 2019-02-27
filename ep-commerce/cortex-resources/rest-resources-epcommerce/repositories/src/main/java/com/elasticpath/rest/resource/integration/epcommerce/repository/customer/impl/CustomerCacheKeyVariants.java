/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.cache.CacheKeyVariants;

/**
 * Generates cache keys from class name and parameters.
 */
@Singleton
@Named("customerCacheKeyVariants")
public final class CustomerCacheKeyVariants implements CacheKeyVariants<Customer> {

	@Override
	public Collection<Object[]> get(final Customer customer) {
		return Arrays.asList(
				new Object[] { customer.getGuid() },
				new Object[] { customer.getStoreCode(), customer.getUserId() }
		);
	}

	@Override
	public Class<Customer> getType() {
		return Customer.class;
	}
}
