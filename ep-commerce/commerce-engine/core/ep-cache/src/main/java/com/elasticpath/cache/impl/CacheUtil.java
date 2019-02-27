/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility methods for implementing caches.
 */
public final class CacheUtil {

	private CacheUtil() {
		// Static methods only
	}

	/**
	 * Merges two result maps into one LinkedHashMap, ordered by the original order of the keys.
	 * Useful for merging cached and uncached results together in a unified, ordered map.
	 *
	 * @param keys the keys used to retrieve the results
	 * @param results the first set of results to merge
	 * @param results2 the second set of results to merge
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return the merged results
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> mergeResults(
			final Collection<? extends K> keys, final Map<K, V> results, final Map<K, V> results2) {
		Map<K, V> combinedResults = new LinkedHashMap<>(keys.size() * 2);
		for (K key : keys) {
			V result = results.get(key);
			if (result != null) {
				combinedResults.put(key, result);
				continue;
			}

			result = results2.get(key);
			if (result != null) {
				combinedResults.put(key, result);
			}
		}
		return combinedResults;
	}
}
