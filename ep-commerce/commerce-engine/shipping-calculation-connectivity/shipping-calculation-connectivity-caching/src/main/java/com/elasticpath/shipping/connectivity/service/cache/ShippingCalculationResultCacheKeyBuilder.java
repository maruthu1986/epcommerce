/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.cache;

import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Builder for simplifying creation of {@link ShippingCalculationResultCacheKey} objects.
 */
public interface ShippingCalculationResultCacheKeyBuilder {
	/**
	 * Populates the impl from the given {@link ShippingCalculationResultCacheKey}.
	 *
	 * @param cacheKey the cache key
	 * @return instance of impl
	 */
	ShippingCalculationResultCacheKeyBuilder from(ShippingCalculationResultCacheKey cacheKey);

	/**
	 * Populates the impl with the shippable items.
	 *
	 * @param shippableItems the shippable items
	 * @return instance of impl
	 */
	ShippingCalculationResultCacheKeyBuilder withShippableItems(Collection<? extends ShippableItem> shippableItems);

	/**
	 * Populates the impl with the priced shippable items.
	 *
	 * @param pricedShippableItems the priced shippable items
	 * @return instance of impl
	 */
	ShippingCalculationResultCacheKeyBuilder withPricedShippableItems(Collection<? extends PricedShippableItem> pricedShippableItems);

	/**
	 * Populates the impl with the destination.
	 *
	 * @param destination the destination
	 * @return instance of impl
	 */
	ShippingCalculationResultCacheKeyBuilder withDestination(ShippingAddress destination);

	/**
	 * Populates the impl with the store code.
	 *
	 * @param storeCode the store code
	 * @return instance of impl
	 */
	ShippingCalculationResultCacheKeyBuilder withStoreCode(String storeCode);

	/**
	 * Populates the impl with the locale.
	 *
	 * @param locale the locale
	 * @return instance of impl
	 */
	ShippingCalculationResultCacheKeyBuilder withLocale(Locale locale);

	/**
	 * Populates the impl with the currency.
	 *
	 * @param currency the currency
	 * @return instance of impl
	 */
	ShippingCalculationResultCacheKeyBuilder withCurrency(Currency currency);

	/**
	 * Builds {@link ShippingCalculationResultCacheKey}.
	 *
	 * @return ShippingCalculationResultCacheKey instance
	 */
	ShippingCalculationResultCacheKey build();
}
