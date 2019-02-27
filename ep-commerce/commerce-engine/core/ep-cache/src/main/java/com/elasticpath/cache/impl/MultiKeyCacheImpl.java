/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.Cache;
import com.elasticpath.cache.CacheLoader;
import com.elasticpath.cache.MultiKeyCache;

/**
 * Generic cache that caches objects by multiple keys.  This implementation caches the objects
 * using a {@link com.elasticpath.cache.Cache}.
 *
 * @param <P> The persistable object type being cached
 */
public class MultiKeyCacheImpl<P> implements MultiKeyCache<P> {
	private List<String> keyProperties;
	private Cache<CacheKey, P> cache;

	@Override
	public void put(final P obj) throws EpServiceException {
		if (obj == null) {
			throw new IllegalArgumentException("Cannot put null objects in cache");
		}

		for (String keyName : getKeyProperties()) {
			try {
				final Object key = PropertyUtils.getProperty(obj, keyName);
				if (key != null) {
					final CacheKey cacheKey = createCacheKey(keyName, key);

					getCache().put(cacheKey, obj);
				}
			} catch (Exception ex) {
				throw new EpServiceException("Could not cache object " + obj, ex);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public P get(final String keyName, final Object keyValue) {
		CacheKey cacheKey = createCacheKey(keyName, keyValue);

		return getCache().get(cacheKey);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K> P get(final String keyName, final K keyValue, final CacheLoader<K, P> fallbackLoader) {
		P found = get(keyName, keyValue);
		if (found == null) {
			found = fallbackLoader.load(keyValue);
			if (found != null) {
				put(found);
			}
		}

		return found;
	}

	@Override
	public <K> Map<K, P> getAll(final String keyName, final Collection<? extends K> keyValues) {
		Map<K, P> result = new LinkedHashMap<>();
		for (K key : keyValues) {
			P value = get(keyName, key);
			if (value != null) {
				result.put(key, value);
			}
		}

		return result;
	}

	@Override
	public <K> Map<K, P> getAll(final String keyName, final Collection<? extends K> keyValues, final CacheLoader<K, P> fallbackLoader) {
		Map<K, P> cachedValues = getAll(keyName, keyValues);
		if (cachedValues.size() == keyValues.size()) {
			return cachedValues;
		}

		List<K> uncachedKeys = new ArrayList<>(keyValues.size() - cachedValues.size());
		for (K key : keyValues) {
			if (!cachedValues.containsKey(key)) {
				uncachedKeys.add(key);
			}
		}

		Map<K, P> uncachedValueMap = fallbackLoader.loadAll(uncachedKeys);
		for (P uncachedValue : uncachedValueMap.values()) {
			put(uncachedValue);
		}

		if (cachedValues.isEmpty()) {
			return uncachedValueMap;
		}

		return CacheUtil.mergeResults(keyValues, cachedValues, uncachedValueMap);
	}

	/**
	 * Creates a key used to get and put objects in the underlying cache.
	 *
	 * @param keyName the name of the key property
	 * @param key the value of the key
	 *
	 * @return a unique key used to stash the objects in the underlying cache
	 */
	protected CacheKey createCacheKey(final String keyName, final Object key) {
		return new CacheKey(keyName, key);
	}

	protected List<String> getKeyProperties() {
		return keyProperties;
	}

	public void setKeyProperties(final List<String> keyProperties) {
		this.keyProperties = keyProperties;
	}

	protected Cache<CacheKey, P> getCache() {
		return cache;
	}

	public void setCache(final Cache<CacheKey, P> cache) {
		this.cache = cache;
	}

	/**
	 * Value class which stores cache keys in the cache.
	 */
	protected static final class CacheKey implements Serializable {
		private static final long serialVersionUID = 1L;

		private final String keyName;
		private final Object keyValue;

		/**
		 * CacheKey constructor.
		 *
		 * @param keyName the key's name
		 * @param keyValue the key's value
		 */
		public CacheKey(final String keyName, final Object keyValue) {
			this.keyName = keyName;
			this.keyValue = keyValue;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}

			final CacheKey cacheKey = (CacheKey) obj;

			return Objects.equals(keyName, cacheKey.keyName)
				&& Objects.equals(keyValue, cacheKey.keyValue);
		}

		@Override
		public int hashCode() {
			return Objects.hash(keyName, keyValue);
		}

		@Override
		public String toString() {
			return "[" + keyName + ":" + keyValue + "]";
		}
	}
}
