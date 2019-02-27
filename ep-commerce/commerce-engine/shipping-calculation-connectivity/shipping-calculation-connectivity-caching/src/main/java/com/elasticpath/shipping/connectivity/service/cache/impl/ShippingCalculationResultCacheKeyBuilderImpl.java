/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.cache.impl;

import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKey;
import com.elasticpath.shipping.connectivity.service.cache.ShippingCalculationResultCacheKeyBuilder;

/**
 * Default implementation of {@link ShippingCalculationResultCacheKeyBuilder}.
 */
public class ShippingCalculationResultCacheKeyBuilderImpl implements ShippingCalculationResultCacheKeyBuilder {

	private Set<String> shippableItemKeys;
	private ShippingAddress destination;
	private String storeCode;
	private String localeString;
	private String currencyCode;

	@Override
	public ShippingCalculationResultCacheKeyBuilder from(final ShippingCalculationResultCacheKey cacheKey) {
		this.shippableItemKeys = cacheKey.getShippableItemKeys();
		this.destination = cacheKey.getDestination();
		this.storeCode = cacheKey.getStoreCode();
		this.localeString = cacheKey.getLocaleString();
		this.currencyCode = cacheKey.getCurrencyCode();

		return this;
	}

	@Override
	public ShippingCalculationResultCacheKeyBuilder withShippableItems(final Collection<? extends ShippableItem> shippableItems) {
		if (shippableItems == null) {
			this.shippableItemKeys = null;
		} else {
			this.shippableItemKeys = shippableItems.stream()
					// Uses sku guid rather than ShoppingItem guid as ShoppingCart.getApportionedLeafItems() is passed into here which
					// are actually brand new OrderSkus each time with new guids so means the cache was never hit
					.map(item -> item.getSkuGuid() + ":" + item.getQuantity())
					.collect(Collectors.toSet());
		}
		return this;
	}

	@Override
	public ShippingCalculationResultCacheKeyBuilder withPricedShippableItems(final Collection<? extends PricedShippableItem> pricedShippableItems) {
		if (pricedShippableItems == null) {
			this.shippableItemKeys = null;
		} else {
			this.shippableItemKeys = pricedShippableItems.stream()
					.map(item -> item.getSkuGuid() + ":" + item.getQuantity() + ":" + item.getUnitPrice())
					.collect(Collectors.toSet());
		}
		return this;
	}

	@Override
	public ShippingCalculationResultCacheKeyBuilder withDestination(final ShippingAddress destination) {
		this.destination = destination;
		return this;
	}

	@Override
	public ShippingCalculationResultCacheKeyBuilder withStoreCode(final String storeCode) {
		this.storeCode = storeCode;
		return this;
	}

	@Override
	public ShippingCalculationResultCacheKeyBuilder withLocale(final Locale locale) {
		this.localeString = locale == null ? null : locale.toString();
		return this;
	}

	@Override
	public ShippingCalculationResultCacheKeyBuilder withCurrency(final Currency currency) {
		this.currencyCode = currency == null ? null : currency.getCurrencyCode();
		return this;
	}

	@Override
	public ShippingCalculationResultCacheKey build() {
		return new ShippingCalculationResultCacheKeyImpl(
				shippableItemKeys,
				destination,
				storeCode,
				localeString,
				currencyCode);
	}

}
