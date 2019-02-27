/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.caching.core.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.cache.CacheLoader;
import com.elasticpath.cache.MultiKeyCache;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.service.catalog.ProductLookup;

public class CachingProductLookupImplTest {
	private static final String CACHE_KEY_UIDPK = "uidPk";
	public static final String CACHE_KEY_GUID = "guid";

	@Mock private MultiKeyCache<Product> cache;
	@Mock private ProductLookup fallback;
	private CachingProductLookupImpl cachingProductLookup;
	private ProductImpl product;
	private ProductImpl product2;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		product = new ProductImpl();
		product.initialize();
		product.setUidPk(1L);
		product.setCode("1");

		product2 = new ProductImpl();
		product2.initialize();
		product2.setUidPk(2L);
		product2.setCode("2");

		cachingProductLookup = new CachingProductLookupImpl();
		cachingProductLookup.setCache(cache);
		cachingProductLookup.setFallbackLookup(fallback);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindByUidDelegatesToCacheWithFallbackLoader() {
		// Given
		when(cache.get(eq(CACHE_KEY_UIDPK), eq(product.getUidPk()), any(CacheLoader.class))).thenReturn(product);

		// When
		Product found = cachingProductLookup.findByUid(product.getUidPk());

		assertSame("Cached product should have been returned", product, found);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindByUidsDelegatesToMultiKeyCacheWithFallbackLoader() {
		// Given
		final List<Long> productUids = Arrays.asList(product.getUidPk(), product2.getUidPk());
		when(cache.getAll(eq(CACHE_KEY_UIDPK), eq(productUids), any(CacheLoader.class))).thenReturn(
				ImmutableMap.of(
						product.getUidPk(), product,
						product2.getUidPk(), product2
				));

		// When
		List<Product> found = cachingProductLookup.findByUids(productUids);

		assertEquals("Cached products should have been returned",
				Arrays.asList(product, product2), found);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindByGuidOnCacheHitReturnsCachedProduct() {
		// Given
		when(cache.get(eq(CACHE_KEY_GUID), eq(product.getGuid()), any(CacheLoader.class))).thenReturn(product);

		// When
		Product found = cachingProductLookup.findByGuid(product.getGuid());

		assertSame("Cached product should have been returned", product, found);
	}
}
