/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.impl;

/**
 * A wrapper which provides a {@link com.elasticpath.cache.Cache} interface for a JCache (JSR-107) compliant cache.
 *
 * @param <K> The class implemented by the cache keys
 * @param <V> The class implemented by the cache values
 */
public class JCacheCacheAdapter<K, V> implements com.elasticpath.cache.Cache<K, V> {
	private final javax.cache.Cache<K, V> cache;

	/**
	 * Constructs the adapter.
	 *
	 * @param cache the underlying JCache
	 */
	public JCacheCacheAdapter(final javax.cache.Cache<K, V> cache) {
		this.cache = cache;
	}

	@Override
	public V get(final K key) {
		return cache.get(key);
	}

	@Override
	public void put(final K key, final V value) {
		cache.put(key, value);
	}

	@Override
	public boolean remove(final K key) {
		return cache.remove(key);
	}

	@Override
	public void removeAll() {
		cache.removeAll();
	}

	@Override
	public String getName() {
		return cache.getName();
	}

	@Override
	public <T> T unwrap(final Class<T> clazz) {
		return cache.unwrap(clazz);
	}
}
