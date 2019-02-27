/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.caching.core.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.CacheLoader;
import com.elasticpath.cache.MultiKeyCache;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.ProductLookup;

/**
 * A cached version of the {@link ProductLookup} interface.
 */
public class CachingProductLookupImpl implements ProductLookup {
	private static final String CACHE_KEY_UIDPK = "uidPk";
	private static final String CACHE_KEY_GUID = "guid";

	private final CacheLoader<String, Product> productsByGuidLoader = new ProductsByGuidLoader();
	private final CacheLoader<Long, Product> productsByUidpkLoader = new ProductsByUidpkLoader();

	private MultiKeyCache<Product> cache;
	private ProductLookup fallbackLookup;

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Product> P findByUid(final long uidpk) throws EpServiceException {
		return (P) getCache().get(CACHE_KEY_UIDPK, uidpk, productsByUidpkLoader);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Product> List<P> findByUids(final Collection<Long> uidPks) throws EpServiceException {
		Map<Long, Product> result = getCache().getAll(CACHE_KEY_UIDPK, uidPks, productsByUidpkLoader);

		List<P> resultList = new ArrayList<>(result.size());
		resultList.addAll((Collection<P>) result.values());

		return resultList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Product> P findByGuid(final String guid) throws EpServiceException {
		return (P) getCache().get(CACHE_KEY_GUID, guid, productsByGuidLoader);
	}

	protected MultiKeyCache<Product> getCache() {
		return cache;
	}

	public void setCache(final MultiKeyCache<Product> cache) {
		this.cache = cache;
	}

	public void setFallbackLookup(final ProductLookup lookup) {
		this.fallbackLookup = lookup;
	}

	protected ProductLookup getFallbackLookup() {
		return fallbackLookup;
	}

	/**
	 * A CacheLoader which loads Products by uidPk.
	 */
	private class ProductsByUidpkLoader implements CacheLoader<Long, Product> {
		@Override
		public Product load(final Long key) {
			return getFallbackLookup().findByUid(key);
		}

		@Override
		public Map<Long, Product> loadAll(final Iterable<? extends Long> keys) {
			List<Product> products = getFallbackLookup().findByUids(Lists.newArrayList(keys));
			Map<Long, Product> productMap = new LinkedHashMap<>(products.size() * 2);
			for (Product product : products) {
				productMap.put(product.getUidPk(), product);
			}

			return productMap;
		}
	}

	/**
	 * A CacheLoader which loads Products by guid (code).
	 */
	private class ProductsByGuidLoader implements CacheLoader<String, Product> {
		@Override
		public Product load(final String key) {
			return getFallbackLookup().findByGuid(key);
		}

		@Override
		public Map<String, Product> loadAll(final Iterable<? extends String> keys) {
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
}
