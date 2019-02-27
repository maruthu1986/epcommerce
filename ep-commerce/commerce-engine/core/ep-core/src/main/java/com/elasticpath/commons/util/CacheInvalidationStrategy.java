/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.util;

import java.util.Set;

/**
 * Interface for a cache invalidation strategy used to invalidate a group of caches.
 * Implementors can add optional behavior such as pre and post processing.
 */
public interface CacheInvalidationStrategy {
	/**
	 * Invalidate known caches.
	 */
	void invalidateCaches();
	/**
	 * Remove from known caches a specific object.
	 * @param objectUid the object key to evict from cache
	 */
	void invalidateCachesForObject(Object objectUid);

	/**
	 * Set the caches to be managed by this invalidation strategy.
	 * @param caches set of caches that need invalidation
	 */
	void setInvalidatableCaches(Set<InvalidatableCache> caches);

}
