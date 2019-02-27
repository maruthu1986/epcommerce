/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.caching.core.pricing;

import java.util.Collection;
import java.util.List;

import com.elasticpath.cache.Cache;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.service.pricing.BaseAmountFinder;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;

/**
 * A cached implementation of the Base Amount Finder.
 */
public class CachingBaseAmountFinderImpl implements BaseAmountFinder {

	private BaseAmountFinder fallBackFinder;

	private Cache<PricingCacheKey, Collection<BaseAmount>> cache;

	@Override
	public Collection<BaseAmount> getBaseAmounts(final ProductSku productSku, final PriceListStack plStack,
												 final BaseAmountDataSource baseAmountDataSource) {
		final PricingCacheKey pricingCacheKey = new PricingCacheKey(plStack, productSku.getSkuCode());

		Collection<BaseAmount> result = getCache().get(pricingCacheKey);
		if (result == null) {
			result = fallBackFinder.getBaseAmounts(productSku, plStack, baseAmountDataSource);
			getCache().put(pricingCacheKey, result);
		}

		return result;
	}

	@Override
	public List<BaseAmount> filterBaseAmounts(final Collection<BaseAmount> baseAmounts, final String plGuid,
											  final BaseAmountObjectType objectType, final String guid) {
		return fallBackFinder.filterBaseAmounts(baseAmounts, plGuid, objectType, guid);
	}

	protected Cache<PricingCacheKey, Collection<BaseAmount>> getCache() {
		return cache;
	}

	public void setCache(final Cache<PricingCacheKey, Collection<BaseAmount>> cache) {
		this.cache = cache;
	}

	protected BaseAmountFinder getFallBackFinder() {
		return fallBackFinder;
	}

	public void setFallBackFinder(final BaseAmountFinder fallBackFinder) {
		this.fallBackFinder = fallBackFinder;
	}
}
