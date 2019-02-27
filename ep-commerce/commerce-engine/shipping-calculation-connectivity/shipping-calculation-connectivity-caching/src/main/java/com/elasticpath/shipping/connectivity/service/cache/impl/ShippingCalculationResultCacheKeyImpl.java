/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.cache.impl;

import static java.util.Collections.unmodifiableSet;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKey;

/**
 * Default implementation of {@link ShippingCalculationResultCacheKey}.
 */
public class ShippingCalculationResultCacheKeyImpl implements ShippingCalculationResultCacheKey {

	private final Set<String> shippableItemKeys;
	private final ShippingAddress destination;
	private final String storeCode;
	private final String localeString;
	private final String currencyCode;

	/**
	 * The constructor.
	 *
	 * @param shippableItemKeys the shippable item keys
	 * @param destination       the destination
	 * @param storeCode         the store code
	 * @param localeString      the locale string
	 * @param currencyCode      the currency code
	 */
	public ShippingCalculationResultCacheKeyImpl(final Set<String> shippableItemKeys, final ShippingAddress destination,
												 final String storeCode, final String localeString,
												 final String currencyCode) {
		this.shippableItemKeys = shippableItemKeys;
		this.destination = destination;
		this.storeCode = storeCode;
		this.localeString = localeString;
		this.currencyCode = currencyCode;
	}

	@Override
	public Set<String> getShippableItemKeys() {
		return unmodifiableSet(this.shippableItemKeys);
	}

	@Override
	public ShippingAddress getDestination() {
		return this.destination;
	}

	@Override
	public String getStoreCode() {
		return this.storeCode;
	}

	@Override
	public String getLocaleString() {
		return this.localeString;
	}

	@Override
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final ShippingCalculationResultCacheKeyImpl that = (ShippingCalculationResultCacheKeyImpl) obj;
		return Objects.equals(shippableItemKeys, that.shippableItemKeys)
				&& Objects.equals(destination, that.destination)
				&& Objects.equals(storeCode, that.storeCode)
				&& Objects.equals(localeString, that.localeString)
				&& Objects.equals(currencyCode, that.currencyCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shippableItemKeys, destination,
				storeCode, localeString, currencyCode);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
