/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cache.SimpleTimeoutCache;

/**
 * A trivial map-based implementation of Simple Timeout Cache for use in tests.
 *
 * @param <K> the key
 * @param <V> the value
 */
public class MapBasedSimpleTimeoutCache<K, V> extends SimpleTimeoutCache<K, V> {

	private final Map<K, V> cache = new HashMap<>();

	@Override
	public V get(final K key) {
		return cache.get(key);
	}

	@Override
	public void put(final K key, final V value) {
		cache.put(key, value);
	}
}
