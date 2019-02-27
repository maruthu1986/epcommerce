/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.cache;

import java.util.Set;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Shipping calculation result cache key.
 */
public interface ShippingCalculationResultCacheKey {

	/**
	 * @return a set of keys identifying each shopping item and its quantity used to generate the cache key.
	 */
	Set<String> getShippableItemKeys();

	/**
	 * @return the address guid used to generate the cache key.
	 */
	ShippingAddress getDestination();

	/**
	 * @return the store code used to generate the cache key.
	 */
	String getStoreCode();

	/**
	 * @return the locale String representation used to generate the cache key.
	 */
	String getLocaleString();

	/**
	 * @return the currency code used to generate the cache key.
	 */
	String getCurrencyCode();
}
