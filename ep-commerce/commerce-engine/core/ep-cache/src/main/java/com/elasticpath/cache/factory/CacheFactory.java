/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.factory;

import com.elasticpath.cache.Cache;

/**
 * CacheFactories are used to create new {@link com.elasticpath.cache.Cache} instances in an implementation
 * independent way.  Typically, the spring context will have multiple cache factories available for each
 * usage, e.g. a "localEntityCacheFactory" and a "distributedEntityCacheFactory".
 */
public interface CacheFactory {
	/**
	 * Creates a cache with the given name.
	 *
	 * @param cacheName the name of the cache to create
	 * @param <K> the class implemented by cache keys
	 * @param <V> the class implemented by cache values
	 *
	 * @return the new cache
	 */
	<K, V> Cache<K, V> createCache(String cacheName);
}
