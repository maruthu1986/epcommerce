/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.factory.impl;

import net.sf.ehcache.CacheManager;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.factory.CacheFactory;
import com.elasticpath.cache.impl.EhcacheCacheAdapter;
import com.elasticpath.cache.impl.OpenJPAEntityCacheDecorator;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * CacheFactory which creates ehcache backed caches that cache OpenJPA entities.
 */
public class EhcacheEntityCacheFactory implements CacheFactory {
	private CacheManager cacheManager;
	private PersistenceEngine persistenceEngine;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <K, V> com.elasticpath.cache.Cache<K, V> createCache(final String cacheName) {
		net.sf.ehcache.Cache ehcache = getCacheManager().getCache(cacheName);
		if (ehcache == null) {
			throw new EpServiceException("Could not retrieve ehcache with name [" + cacheName + "]");
		}
		com.elasticpath.cache.Cache<K, V> epCache = new EhcacheCacheAdapter<>(ehcache);

		return new OpenJPAEntityCacheDecorator(epCache, getPersistenceEngine());
	}

	protected CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
