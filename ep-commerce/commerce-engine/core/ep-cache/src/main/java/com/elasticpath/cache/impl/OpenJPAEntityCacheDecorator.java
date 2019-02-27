/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.impl;

import com.elasticpath.cache.Cache;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * A {@link com.elasticpath.cache.Cache} decorator which detaches OpenJPA entities before adding them
 * to the cache.
 *
 * OpenJPA entities <em>MUST</em> be detached before being cached in Local caches because attached
 * OpenJPA entities are not thread safe.
 *
 * @param <K> the class implemented by the cache keys
 * @param <V> the class implemented by the cache values
 */
public class OpenJPAEntityCacheDecorator<K, V extends Persistable> implements Cache<K, V> {
	private final PersistenceEngine persistenceEngine;
	private final Cache<K, V> decoratedCache;

	/**
	 * Decorator constructor.
	 *
	 * @param decoratedCache the cache that will be wrapped by this decorator
	 * @param persistenceEngine the (OpenJPA) persistenceEngine used to detach entities before caching them
	 */
	public OpenJPAEntityCacheDecorator(final Cache<K, V> decoratedCache, final PersistenceEngine persistenceEngine) {
		this.decoratedCache = decoratedCache;
		this.persistenceEngine = persistenceEngine;
	}

	@Override
	public V get(final K key) {
		return decoratedCache.get(key);
	}

	@Override
	public void put(final K key, final V value) {
		V detached = getPersistenceEngine().detach(value);

		decoratedCache.put(key, detached);
	}

	@Override
	public boolean remove(final K key) {
		return decoratedCache.remove(key);
	}

	@Override
	public void removeAll() {
		decoratedCache.removeAll();
	}

	@Override
	public String getName() {
		return decoratedCache.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(final Class<T> clazz) {
		if (OpenJPAEntityCacheDecorator.class.isAssignableFrom(clazz)) {
			return (T) this;
		}

		return decoratedCache.unwrap(clazz);
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}
}
