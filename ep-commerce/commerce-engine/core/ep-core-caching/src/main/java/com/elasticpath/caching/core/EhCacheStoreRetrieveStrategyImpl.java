/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.caching.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreRetrieveStrategy;

/**
 * Implementation of the Store Cache.
 */
public class EhCacheStoreRetrieveStrategyImpl implements StoreRetrieveStrategy, InvalidatableCache {

	private Ehcache storeCache;

	@Override
	public void invalidate() {
		storeCache.removeAll();
	}

	@Override
	public void invalidate(final Object objectUid) {
		storeCache.remove(objectUid);
	}

	@Override
	public Store retrieveStore(final long storeUid) {
		return findInCache(storeUid);
	}

	@Override
	public List<Store> retrieveStores(final Collection<Long> storeUids) {

		final List<Store> stores = new ArrayList<>(storeUids.size());
		for (final Long uid : storeUids) {
			final Store store = retrieveStore(uid);
			if (store != null) {
				stores.add(store);
			}
		}
		return stores;
	}

	@Override
	public Store retrieveStore(final String storeCode) {
		return findInCache(StringUtils.upperCase(storeCode));
	}

	@SuppressWarnings("unchecked")
	private Store findInCache(final Object cacheKey) {
		final Element cacheElement = storeCache.get(cacheKey);
		if (cacheElement != null && !cacheElement.isExpired()) {
			final Object cacheValue = cacheElement.getValue();
			if (cacheValue instanceof Long) {
				return findInCache(cacheValue);
			}

			return (Store) cacheValue;
		}
		return null;
	}

	@Override
	public void cacheStore(final Store store) {
		final Element cacheByUid =  new Element(store.getUidPk(), store);
		final Element cacheByCode = new Element(StringUtils.upperCase(store.getCode()), store.getUidPk());
		storeCache.put(cacheByUid);
		storeCache.put(cacheByCode);
	}

	/**
	 * Sets the EhCache .
	 * @param storeCache the cache of stores.
	 */
	public void setStoreCache(final Ehcache storeCache) {
		this.storeCache = storeCache;
	}
}
