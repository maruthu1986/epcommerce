/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that SimpleTimeoutCache works as intended.
 */
public class SimpleTimeoutCacheTest {

	private static final String FRED = "fred";
	private static final String FREDS_INFORMATION = "fred's information";
	private static final long ONE_SEC = 1000L;
	private static final long ONE_AND_HALF_SEC = 1500L;

	private final CacheManager cacheManager = CacheManager.getInstance();
	private final SimpleTimeoutCache<String, String> fixture = new SimpleTimeoutCache<>();

	private Cache ehCache;

	@Before
	public void before() {
		ehCache = createEhcacheInstance("simple_timeout", 2, 1L, 1L);

		fixture.setCache(ehCache);
	}

	@After
	public void after() {
		ehCache.dispose();
	}
	
	/**
	 * Simple test for putting and getting from the SimpleTimeoutCache.
	 */
	@Test
	public void testSimpleCaching() {
		final long sleepTime = 1050L;
		final long timeout = 1000L;

		fixture.changeTimeout(timeout);
		fixture.put(FRED, FREDS_INFORMATION);

		assertEquals(FREDS_INFORMATION, fixture.get(FRED));
		
		// Make sure it's available after the first call
		assertEquals(FREDS_INFORMATION, fixture.get(FRED));
		assertEquals(FREDS_INFORMATION, fixture.get(FRED));
		assertEquals(FREDS_INFORMATION, fixture.get(FRED));

		sleep(sleepTime);

		assertNull(fixture.get(FRED));
	}
	
	/** 
	 * Test that with a zero timeout values are cached indefinitely (eternal cache).
	 */
	@Test
	public void testZeroTimeout() {
		final long timeout = 0L;

		fixture.changeTimeout(timeout);
		fixture.put(FRED, FREDS_INFORMATION);

		sleep(ONE_SEC);

		assertEquals(FREDS_INFORMATION, fixture.get(FRED));
	}
	
	/** 
	 * Test that with a negative timeout values nothing is cached (timeout must be in negative seconds).
	 */
	@Test
	public void testNegativeTimeout() {
		final long timeout = -1000L;

		fixture.changeTimeout(timeout);
		fixture.put(FRED, FREDS_INFORMATION);

		sleep(ONE_SEC);

		assertNull("Information should not have been cached", fixture.get(FRED));
	}
	
	
	/**
	 * Test the timeout works as expected.
	 */
	@Test
	public void testNormalCacheTimeout() {
		final long timeout = 3000L;

		fixture.changeTimeout(timeout);
		fixture.put(FRED, FREDS_INFORMATION);  // put in at time=0 - should expire after 3s

		assertEquals(FREDS_INFORMATION, fixture.get(FRED));
		
		sleep(ONE_SEC);
		assertEquals(FREDS_INFORMATION, fixture.get(FRED));

		sleep(ONE_SEC);
		assertEquals(FREDS_INFORMATION, fixture.get(FRED));

		sleep(ONE_AND_HALF_SEC);
		assertNull("Cached entry should have expired", fixture.get(FRED));
	}

	/**
	 * Test storage and retrieval of null values.
	 */
	@Test
	public void testNullKeysAndValues() {
		fixture.put(null, null);
		assertNull("Returned cached entry must be null", fixture.get(null));

		fixture.put(null, FRED);
		assertNull("Returned cached entry must be null if key is null, regardless of value", fixture.get(null));
	}

	/**
	 * Test creation of multiple instances of SimpleTimeoutCache with Ehcache instances with the same name.
	 */
	@Test
	public void testMultipleInstanceOfSimpleTimeoutCacheWithMultipleEhcacheInstancesWithSameName() {
		final SimpleTimeoutCache<String, String> simpleCache1 = new SimpleTimeoutCache<>();
		final Cache ehCache1 = createEhcacheInstance("simple_timeout", 2, 1L, 1L);

		simpleCache1.setCache(ehCache1);

		final int maxElementsInHeap = 20;
		final long timeout = 10L;
		final SimpleTimeoutCache<String, String> simpleCache2 = new SimpleTimeoutCache<>();
		final Cache ehCache2 = createEhcacheInstance("simple_timeout", maxElementsInHeap, timeout, timeout);

		simpleCache2.setCache(ehCache2);

		//assert ehCache1 params
		assertEquals(2, simpleCache1.getCache().getCacheConfiguration().getMaxEntriesLocalHeap());
		assertEquals(1L, simpleCache1.getCache().getCacheConfiguration().getTimeToIdleSeconds());
		assertEquals(1L, simpleCache1.getCache().getCacheConfiguration().getTimeToLiveSeconds());

		//assert ehCache2 params
		assertEquals(maxElementsInHeap, simpleCache2.getCache().getCacheConfiguration().getMaxEntriesLocalHeap());
		assertEquals(timeout, simpleCache2.getCache().getCacheConfiguration().getTimeToIdleSeconds());
		assertEquals(timeout, simpleCache2.getCache().getCacheConfiguration().getTimeToLiveSeconds());

	}

	private void sleep(final long timeToWait) {

		try {
			Thread.sleep(timeToWait);
		} catch (Exception e) {
		}
	}

	private Cache createEhcacheInstance(final String name, final int maxElementsInHeap, final long timeToIdleSeconds, final long timeToLiveSeconds) {
		final Cache ehCache = new Cache(name, maxElementsInHeap, false, false, timeToIdleSeconds, timeToLiveSeconds);
		ehCache.setCacheManager(cacheManager);
		ehCache.initialise();

		return ehCache;
	}

	//TODO tests for cache event listener and timeout; test ordering
	
}
