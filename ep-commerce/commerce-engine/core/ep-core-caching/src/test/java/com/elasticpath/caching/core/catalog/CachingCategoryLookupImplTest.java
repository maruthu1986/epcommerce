/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.caching.core.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static com.elasticpath.caching.core.catalog.CachingCategoryLookupImpl.CACHE_KEY_COMPOUND_GUID;
import static com.elasticpath.caching.core.catalog.CachingCategoryLookupImpl.CACHE_KEY_GUID;
import static com.elasticpath.caching.core.catalog.CachingCategoryLookupImpl.CACHE_KEY_UIDPK;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.cache.Cache;
import com.elasticpath.cache.CacheLoader;
import com.elasticpath.cache.MultiKeyCache;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.service.catalog.CategoryLookup;

@SuppressWarnings("unchecked")
public class CachingCategoryLookupImplTest {

	@Mock private MultiKeyCache<Category> cache;
	@Mock private Cache<Long, List<Long>> childCache;
	@Mock private CategoryLookup fallbackReader;
	private CachingCategoryLookupImpl cachingCategoryLookup;
	private CategoryImpl category;
	private CategoryImpl category2;
	private CatalogImpl catalog;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		catalog = new CatalogImpl();
		catalog.setCode("catalog");

		category = new CategoryImpl();
		category.initialize();
		category.setUidPk(1L);
		category.setCode("1");
		category.setCatalog(catalog);

		category2 = new CategoryImpl();
		category2.initialize();
		category2.setUidPk(2L);
		category2.setCode("2");
		category2.setCatalog(catalog);
		category2.setParent(category);

		cachingCategoryLookup = new CachingCategoryLookupImpl();
		cachingCategoryLookup.setCategoryCache(cache);
		cachingCategoryLookup.setChildCategoryCache(childCache);
		cachingCategoryLookup.setFallbackReader(fallbackReader);
	}

	@Test
	public void testFindByUidDelegatesToCacheWithFallbackLoader() {
		// Given
		when(cache.get(eq(CACHE_KEY_UIDPK), eq(category.getUidPk()), any(CacheLoader.class))).thenReturn(category);

		// When
		Category found = cachingCategoryLookup.findByUid(category.getUidPk());

		assertSame("uid Cached category should have been returned", category, found);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindByUidsDelegatesToMultiKeyCacheWithFallbackLoader() {
		// Given
		final List<Long> categoryUids = Arrays.asList(category.getUidPk(), category2.getUidPk());
		when(cache.getAll(eq(CACHE_KEY_UIDPK), eq(categoryUids), any(CacheLoader.class))).thenReturn(
				ImmutableMap.of(
						category.getUidPk(), category,
						category2.getUidPk(), category2
				));

		// When
		List<Category> found = cachingCategoryLookup.findByUids(categoryUids);

		assertEquals("Cached categories should have been returned",
				Arrays.asList(category, category2), found);
	}


	@Test
	public void testFindByGuidDelegatesToCacheWithFallbackLoader() {
		// Given
		when(cache.get(eq(CACHE_KEY_GUID), eq(category.getGuid()), any(CacheLoader.class))).thenReturn(category);

		// When
		Category found = cachingCategoryLookup.findByGuid(category.getGuid());

		assertSame("guid Cached category should have been returned", category, found);
	}

	@Test
	public void testFindByCategoryCodeAndCatalogDelegatesToCache() {
		// Given
		when(cache.get(
				eq(CACHE_KEY_COMPOUND_GUID),
				eq(category.getGuid() + "|" + catalog.getGuid()),
				any(CacheLoader.class)))
			.thenReturn(category);

		// When
		Category found = cachingCategoryLookup.findByCategoryCodeAndCatalog(category.getGuid(), catalog);

		assertSame("guid Cached category should have been returned", category, found);
	}

	@Test
	public void testFindByCategoryAndCatalogCodeDelegatesToCache() {
		// Given
		when(cache.get(
				eq(CACHE_KEY_COMPOUND_GUID),
				eq(category.getGuid() + "|" + catalog.getGuid()),
				any(CacheLoader.class)))
				.thenReturn(category);

		// When
		Category found = cachingCategoryLookup.findByCategoryAndCatalogCode(category.getGuid(), catalog.getCode());

		assertSame("guid Cached category should have been returned", category, found);
	}

	@Test
	public void testFindByCompoundCategoryAndCatalogCodesDelegatesToCache() {
		// Given
		when(cache.get(
				eq(CACHE_KEY_COMPOUND_GUID),
				eq(category.getCode() + "|" + catalog.getCode()),
				any(CacheLoader.class)))
				.thenReturn(category);

		// When
		Category found = cachingCategoryLookup.findByCompoundCategoryAndCatalogCodes(category.getCode() + "|" + catalog.getCode());

		assertSame("compound guid cached category should have been returned", category, found);
	}

	@Test
	public void testFindParentDelegatesToFindByGuid() {
		// Given
		when(cache.get(eq(CACHE_KEY_GUID), eq(category.getGuid()), any(CacheLoader.class))).thenReturn(category);

		// When
		Category found = cachingCategoryLookup.findParent(category2);

		assertSame("Category 2's parent category should have been returned", category, found);
	}

	@Test
	public void testFindParentReturnsNullIfParentIsNull() {
		// When
		Category found = cachingCategoryLookup.findParent(category);

		assertNull(found);
		verifyNoMoreInteractions(cache);
	}

	@Test
	public void testFindChildrenRetrievesFromCacheOnCacheHit() {
		// Given
		when(childCache.get(category.getUidPk()))
				.thenReturn(Collections.singletonList(category2.getUidPk()));
		when(cache.getAll(eq(CACHE_KEY_UIDPK), eq(Collections.singletonList(category2.getUidPk())), any(CacheLoader.class)))
				.thenReturn(Collections.singletonMap(category2.getUidPk(), category2));

		// When
		List<Category> found = cachingCategoryLookup.findChildren(category);

		assertEquals("Cached child categories should have been returned",
				Collections.singletonList(category2), found);
	}

	@Test
	public void testFindChildrenRetrievesFromFallbackReaderOnCacheMiss() {
		// Given
		when(childCache.get(category.getUidPk())).thenReturn(null);
		when(fallbackReader.findChildren(category)).thenReturn(
				Collections.<Category>singletonList(category2));

		// When
		List<Category> found = cachingCategoryLookup.findChildren(category);

		// Then
		assertEquals("Cached child categories should have been returned",
				Collections.singletonList(category2), found);
		// Verify that the list of child category ids are cached
		verify(childCache).put(category.getUidPk(), Collections.singletonList(category2.getUidPk()));
	}

	@Test
	public void testFindChildrenCachesEmptyChildIdListsForLeafNodes() {
		// Given
		when(childCache.get(category2.getUidPk())).thenReturn(null);
		when(fallbackReader.findChildren(category2)).thenReturn(
				Collections.<Category>emptyList());

		// When
		List<Category> found = cachingCategoryLookup.findChildren(category2);

		// Then
		assertEquals("Category2 has no children", Collections.emptyList(), found);
		// Verify that the (empty) list of child category ids was cached
		verify(childCache).put(category2.getUidPk(), Collections.<Long>emptyList());
	}

	@Test
	public void testCategoriesByUidpkLoaderDelegatesToFallbackForSingleIds() {
		// Given
		when(fallbackReader.findByUid(category.getUidPk())).thenReturn(category);

		// When
		CacheLoader<Long, Category> loader = cachingCategoryLookup.getCategoriesByUidpkLoader();
		Category found = loader.load(category.getUidPk());

		// Then
		assertSame("uidpk Loader should delegate to fallback", category, found);
	}

	@Test
	public void testCategoriesByUidpkLoaderDelegatesToFallbackForMultipleIds() {
		// Given
		when(fallbackReader.findByUids(Arrays.asList(category.getUidPk(), category2.getUidPk())))
				.thenReturn(Arrays.<Category>asList(category, category2));

		// When
		CacheLoader<Long, Category> loader = cachingCategoryLookup.getCategoriesByUidpkLoader();
		Map<Long, Category> found = loader.loadAll(Arrays.asList(category.getUidPk(), category2.getUidPk()));

		// Then
		assertEquals("uidpk Loader should delegate to fallback",
				ImmutableMap.of(
						category.getUidPk(), category,
						category2.getUidPk(), category2),
				found);
	}

	@Test
	public void testCategoriesByGuidLoaderDelegatesToFallbackForSingleIds() {
		// Given
		when(fallbackReader.findByGuid(category.getGuid())).thenReturn(category);

		// When
		CacheLoader<String, Category> loader = cachingCategoryLookup.getCategoriesByGuidLoader();
		Category found = loader.load(category.getGuid());

		// Then
		assertSame("guid Loader should delegate to fallback", category, found);
	}

	@Test
	public void testCategoriesByCodeLoaderDelegatesToFallbackForSingleIds() {
		// Given
		when(fallbackReader.findByCompoundCategoryAndCatalogCodes(category.getCompoundGuid())).thenReturn(category);

		// When
		CacheLoader<String, Category> loader = cachingCategoryLookup.getCategoriesByCodeLoader();
		Category found = loader.load(category.getCompoundGuid());

		// Then
		assertSame("guid Loader should delegate to fallback", category, found);
	}
}
