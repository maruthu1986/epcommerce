/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.factory.impl;

import javax.cache.CacheManager;

import com.elasticpath.cache.factory.CacheFactory;
import com.elasticpath.cache.impl.JCacheCacheAdapter;
import com.elasticpath.cache.impl.OpenJPAEntityCacheDecorator;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * CacheFactory which creates JCache (JSR-107) backed caches that cache OpenJPA entities.
 */
public class JCacheEntityCacheFactory implements CacheFactory {
	private CacheManager cacheManager;
	private PersistenceEngine persistenceEngine;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <K, V> com.elasticpath.cache.Cache<K, V> createCache(final String cacheName) {
		javax.cache.Cache<K, V> jCache = getCacheManager().getCache(cacheName);
		com.elasticpath.cache.Cache<K, V> epCache = new JCacheCacheAdapter<>(jCache);

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
