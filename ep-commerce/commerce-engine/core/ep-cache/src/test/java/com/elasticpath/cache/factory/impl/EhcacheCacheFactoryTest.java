/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.factory.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.persistence.api.Persistable;

public class EhcacheCacheFactoryTest {
	private static final String CACHE_NAME = "cache";

	@Mock private CacheManager cacheManager;
	@Mock private Cache ehcache;

	private EhcacheCacheFactory factory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		factory = new EhcacheCacheFactory();
		factory.setCacheManager(cacheManager);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateCache() throws Exception {
		// Given
		when(cacheManager.<String, Persistable>getCache(CACHE_NAME)).thenReturn(ehcache);

		// When
		com.elasticpath.cache.Cache<String, Persistable> newCache = factory.createCache(CACHE_NAME);

		// Then
		assertSame("Decorator should wrap a ehcache", ehcache, newCache.unwrap(Cache.class));
	}
}
