/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cache.factory.impl;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.cache.CacheManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.cache.impl.OpenJPAEntityCacheDecorator;
import com.elasticpath.persistence.api.Persistable;

public class JCacheEntityCacheFactoryTest {
	private static final String CACHE_NAME = "cache";

	@Mock private CacheManager cacheManager;
	@Mock private javax.cache.Cache<String, Persistable> jcache;

	private JCacheEntityCacheFactory factory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		factory = new JCacheEntityCacheFactory();
		factory.setCacheManager(cacheManager);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateCache() throws Exception {
		// Given
		when(cacheManager.<String, Persistable>getCache(CACHE_NAME)).thenReturn(jcache);
		when(jcache.unwrap(any(Class.class))).thenReturn(jcache);

		// When
		com.elasticpath.cache.Cache<String, Persistable> newCache = factory.createCache(CACHE_NAME);

		// Then
		assertTrue("Cache should have an Entity decorator", newCache instanceof OpenJPAEntityCacheDecorator);
		assertSame("Decorator should wrap a jcache", jcache, newCache.unwrap(javax.cache.Cache.class));
	}
}
