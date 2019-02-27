/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.factory.impl;

import net.sf.ehcache.CacheManager;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.factory.CacheFactory;
import com.elasticpath.cache.impl.EhcacheCacheAdapter;

/**
 * CacheFactory which creates ehcache backed caches that cache generic objects.
 */
public class EhcacheCacheFactory implements CacheFactory {
	private CacheManager cacheManager;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <K, V> com.elasticpath.cache.Cache<K, V> createCache(final String cacheName) {
		net.sf.ehcache.Cache ehcache = getCacheManager().getCache(cacheName);
		if (ehcache == null) {
			throw new EpServiceException(
					"Could not retrieve ehcache named [" + cacheName + " from cacheManager " + getCacheManager().getName());
		}

		return new EhcacheCacheAdapter<>(ehcache);
	}

	protected CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
