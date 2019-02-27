/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.CacheEventListener;

/**
 * Provides a simple timeout cache around Ehcache instance.
 *
 * @param <K> the type used as a key into the cache.
 * @param <V> the type to actually cache.
 */
@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
public class SimpleTimeoutCache<K, V> {

	private static final long ONE_SECOND = 1000L;
	//Injected via Spring
	private Ehcache cache;

	/**
	 * Returns the value associated with the specified <tt>key</tt>. Returns
	 * <tt>null</tt> if the cache contains no association for this key or if
	 * the cached item associated with the key has timed out.
	 *
	 * @param key - Key whose associated value will be returned.
	 * @return The value associated with the key if found, or
	 *         <tt>null</tt> if there is no associated value or it is timed out.
	 */
	@SuppressWarnings("unchecked")
	public synchronized V get(final K key) {

		final Element cacheObject = cache.get(key);

		if (cacheObject == null) {
			return null;
		}

		return (V) cacheObject.getObjectValue();
	}
	
	/**
	 * Stores the <tt>key</tt>/<tt>value</tt> association into the cache.
	 *
	 * @param key - The identifier to associate the value with in the cache.
	 * @param value - The value to be stored in the cache.
	 */
	public synchronized void put(final K key, final V value) {

		final Element cacheElement = new Element(key, value);
		cache.put(cacheElement);
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		this.cache.removeAll();
	}

	//no need to synchronize - called in IntervalRefreshStrategyImpl when new cache is created
	/**
	 * Allows changing time-to-idle and time-to-live caching parameters at runtime, by setting
	 * the same value to both parameters.
	 *
	 * @param timeoutMillis Timeout in milliseconds.
	 */
	public void changeTimeout(final long timeoutMillis) {
		final CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
		cacheConfiguration.setTimeToIdleSeconds(timeoutMillis / ONE_SECOND);
		cacheConfiguration.setTimeToLiveSeconds(timeoutMillis / ONE_SECOND);
	}

	public Ehcache getCache() {
		return cache;
	}

	public void setCache(final Ehcache cache) {
		this.cache = cache;
	}

	/**
	 * Spring method for setting cache event listener.
	 * @param cacheEventListener parameter.
	 */
	public void setCacheEventListener(final CacheEventListener cacheEventListener) {

		if (cache == null) {
			throw new IllegalStateException("Cache instance must be set before using the listener. Check the order of properties in Spring "
													+ "configuration");
		}
		cache.getCacheEventNotificationService().registerListener(cacheEventListener);
	}


	/**
	 * Spring method for setting optional cache timeout.
	 *
	 * @param timeoutSeconds Optional timeout.
	 */
	public void setTimeoutSeconds(final Long timeoutSeconds) {
		if (cache == null) {
			throw new IllegalStateException("Cache instance must be set before setting the timeout. Check the order of properties in Spring "
													+ "configuration");
		}

		this.changeTimeout(timeoutSeconds * ONE_SECOND);
	}
}
