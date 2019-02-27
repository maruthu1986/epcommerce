/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.impl;

import net.sf.ehcache.Element;

/**
 * A wrapper which provides a {@link com.elasticpath.cache.Cache} interface for ehcache.
 *
 * @param <K> The class implemented by the cache keys
 * @param <V> The class implemented by the cache values
 */
public class EhcacheCacheAdapter<K, V> implements com.elasticpath.cache.Cache<K, V> {
	private final net.sf.ehcache.Cache cache;

	/**
	 * Constructs the adapter.
	 *
	 * @param cache the underlying JCache
	 */
	public EhcacheCacheAdapter(final net.sf.ehcache.Cache cache) {
		this.cache = cache;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(final K key) {
		final Element element = cache.get(key);
		if (element == null) {
			return null;
		}
		return (V) element.getObjectValue();
	}

	@Override
	public void put(final K key, final V value) {
		Element ele = new Element(key, value);
		cache.put(ele);
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
	@SuppressWarnings("unchecked")
	public <T> T unwrap(final Class<T> clazz) {
		return (T) cache;
	}
}
