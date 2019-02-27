/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EhcacheCacheAdapterTest {
	private static final String CACHE_NAME = "test-cache";
	private final CacheManager cacheManager = CacheManager.create();

	private Cache cache;
	private EhcacheCacheAdapter<String, String> adapter;

	@Before
	public void setUp() throws Exception {
		cache = new Cache(CACHE_NAME, 0, false, true, 0, 0);
		cacheManager.addCache(cache);

		adapter = new EhcacheCacheAdapter<>(cache);
	}

	@After
	public void tearDown() {
		cacheManager.removeCache(CACHE_NAME);
	}

	@Test
	public void verifyThatGetOnCacheHitReturnsACachedObject() throws Exception {
		cache.put(new Element("foo", "bar"));
		assertEquals("bar", adapter.get("foo"));
	}

	@Test
	public void verifyThatGetOnCacheMissReturnsNull() throws Exception {
		assertNull(adapter.get("foo"));
	}
}
