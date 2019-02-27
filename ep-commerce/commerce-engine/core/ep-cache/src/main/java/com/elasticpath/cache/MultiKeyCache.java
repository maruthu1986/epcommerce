/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Generic, threadsafe cache that can cache Persistable entities by multiple keys simultaneously.
 *
 * @param <V> The object class being cached
 */
public interface MultiKeyCache<V> {
	/**
	 * Caches the given object by all configured keys.
	 *
	 * @param obj the object to cache
	 *
	 * @throws IllegalArgumentException if obj is null
	 * @throws com.elasticpath.base.exception.EpServiceException if the cache is not configured correctly
	 */
	void put(V obj) throws EpServiceException;

	/**
	 * Retrieves an object from cache using the given key.
	 *
	 * @param keyName the name of the key to retrieve the object by
	 * @param keyValue the value of the key to retrieve
	 * @return the cached object
	 *
	 * @throws NullPointerException if either argument is null
	 */
	V get(String keyName, Object keyValue);

	/**
	 * Retrieves an object from cache using the given key.
	 *
	 * @param keyName the name of the key to retrieve the object by
	 * @param keyValue the value of the key to retrieve
	 * @param fallbackLoader a cache loader to use to load the value if the value cannot be found in cache
	 * @param <K> the key's class
	 * @return the cached object
	 *
	 * @throws NullPointerException if either argument is null
	 */
	<K> V get(String keyName, K keyValue, CacheLoader<K, V> fallbackLoader);

	/**
	 * Retrieves an object from cache using the given keys.
	 *
	 * @param keyName the name of the key to retrieve the object by
	 * @param keyValues the value of the key to retrieve
	 * @param <K> the key type
	 * @return A map of entries that were found for the given keys. Keys not found
	 *         in the cache are not in the returned map.
	 *
	 * @throws NullPointerException if either argument is null
	 */
	<K> Map<K, V> getAll(String keyName, Collection<? extends K> keyValues);


	/**
	 * Retrieves an object from cache using the given keys.
	 *
	 * @param keyName the name of the key to retrieve the object by
	 * @param keyValues the value of the key to retrieve
	 * @param fallbackLoader a cache loader to use to load the value if values cannot be found in cache
	 * @param <K> the key type
	 * @return A map of entries that were found for the given keys. Keys not found
	 *         in the cache are not in the returned map.
	 *
	 * @throws NullPointerException if either argument is null
	 */
	<K> Map<K, V> getAll(String keyName, Collection<? extends K> keyValues, CacheLoader<K, V> fallbackLoader);
}
