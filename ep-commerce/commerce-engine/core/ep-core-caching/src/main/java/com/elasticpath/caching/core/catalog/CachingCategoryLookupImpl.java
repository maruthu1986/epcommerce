/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.caching.core.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.elasticpath.cache.Cache;
import com.elasticpath.cache.CacheLoader;
import com.elasticpath.cache.MultiKeyCache;
import com.elasticpath.commons.util.CategoryGuidUtil;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * A Caching implementation of the {@link com.elasticpath.service.catalog.CategoryLookup} interface.
 */
public class CachingCategoryLookupImpl implements CategoryLookup {
	/** The cache key used for caching by uidpk. */
	protected static final String CACHE_KEY_UIDPK = "uidPk";
	/** The cache key used for caching by guid. */
	protected static final String CACHE_KEY_GUID = "guid";
	/** The cache key used for caching by category code + catalog code. */
	protected static final String CACHE_KEY_COMPOUND_GUID = "compoundGuid";

	private final CacheLoader<Long, Category> categoriesByUidpkLoader = new CategoriesByUidpkLoader();
	private final CacheLoader<String, Category> categoriesByGuidLoader = new CategoriesByGuidLoader();
	private final CacheLoader<String, Category> categoriesByCodeLoader = new CategoriesByCodeLoader();
	private final CategoryGuidUtil categoryGuidUtil = new CategoryGuidUtil();

	private MultiKeyCache<Category> categoryCache;
	private CategoryLookup fallbackReader;
	private Cache<Long, List<Long>> childCategoryCache;

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Category> C findByUid(final long uidPk) {
		return (C) getCategoryCache().get(CACHE_KEY_UIDPK, uidPk, getCategoriesByUidpkLoader());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Category> List<C> findByUids(final Collection<Long> categoryUids) {
		Map<Long, Category> resultMap = getCategoryCache().getAll(
				CACHE_KEY_UIDPK, categoryUids, getCategoriesByUidpkLoader());

		List<C> results = new ArrayList<>(resultMap.size());
		results.addAll((Collection<C>) resultMap.values());

		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Category> C findByGuid(final String guid) {
		return (C) getCategoryCache().get(CACHE_KEY_GUID, guid, getCategoriesByGuidLoader());
	}

	@Override
	public <C extends Category> C findByCategoryCodeAndCatalog(final String code, final Catalog catalog) {
		return findByCategoryAndCatalogCode(code, catalog.getGuid());
	}

	@Override
	public <C extends Category> C findByCategoryAndCatalogCode(final String categoryCode, final String catalogCode) {
		String compoundGuid = getCategoryGuidUtil().get(categoryCode, catalogCode);

		return findByCompoundCategoryAndCatalogCodes(compoundGuid);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Category> C findByCompoundCategoryAndCatalogCodes(final String compoundGuid) {
		return (C) getCategoryCache().get(CACHE_KEY_COMPOUND_GUID, compoundGuid, getCategoriesByCodeLoader());
	}

	@Override
	public <C extends Category> List<C> findChildren(final Category parent) {
		List<Long> childIds = getChildCategoryCache().get(parent.getUidPk());
		if (childIds == null) {
			List<C> children = getFallbackReader().findChildren(parent);
			List<Long> resultIds = ImmutableList.copyOf(
				Lists.transform(children, new Function<C, Long>() {
				@Override
				public Long apply(final C category) {
					return category.getUidPk();
				}
			}));
			getChildCategoryCache().put(parent.getUidPk(), resultIds);

			return children;
		}
		return findByUids(childIds);
	}

	@Override
	public <C extends Category> C findParent(final Category child) {
		if (child.getParentGuid() == null) {
			return null;
		}

		return findByGuid(child.getParentGuid());
	}

	protected MultiKeyCache<Category> getCategoryCache() {
		return categoryCache;
	}

	public void setCategoryCache(final MultiKeyCache<Category> categoryCache) {
		this.categoryCache = categoryCache;
	}

	protected CategoryLookup getFallbackReader() {
		return fallbackReader;
	}

	public void setFallbackReader(final CategoryLookup fallbackReader) {
		this.fallbackReader = fallbackReader;
	}

	protected CategoryGuidUtil getCategoryGuidUtil() {
		return categoryGuidUtil;
	}

	public void setChildCategoryCache(final Cache<Long, List<Long>> childCategoryCache) {
		this.childCategoryCache = childCategoryCache;
	}

	protected Cache<Long, List<Long>> getChildCategoryCache() {
		return childCategoryCache;
	}

	protected CacheLoader<Long, Category> getCategoriesByUidpkLoader() {
		return categoriesByUidpkLoader;
	}

	protected CacheLoader<String, Category> getCategoriesByCodeLoader() {
		return categoriesByCodeLoader;
	}

	protected CacheLoader<String, Category> getCategoriesByGuidLoader() {
		return categoriesByGuidLoader;
	}

	/**
	 * CacheLoader which loads Categories by uidPk.
	 */
	protected class CategoriesByUidpkLoader implements CacheLoader<Long, Category> {
		@Override
		public Category load(final Long key) {
			return getFallbackReader().findByUid(key);
		}

		@Override
		public Map<Long, Category> loadAll(final Iterable<? extends Long> keys) {
			List<Category> loaded = getFallbackReader().findByUids(Lists.newArrayList(keys));
			Map<Long, Category> result = new LinkedHashMap<>(loaded.size() * 2);
			for (Category category : loaded) {
				result.put(category.getUidPk(), category);
			}
			return result;
		}
	}

	/**
	 * CacheLoader which loads Categories by Guid.
	 */
	protected class CategoriesByGuidLoader implements CacheLoader<String, Category> {
		@Override
		public Category load(final String key) {
			return getFallbackReader().findByGuid(key);
		}

		@Override
		public Map<String, Category> loadAll(final Iterable<? extends String> keys) {
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}

	/**
	 * CacheLoader which loads Categories by Compound Guid (CategoryCode + CatalogCode).
	 */
	protected class CategoriesByCodeLoader implements CacheLoader<String, Category> {
		@Override
		public Category load(final String key) {
			return getFallbackReader().findByCompoundCategoryAndCatalogCodes(key);
		}

		@Override
		public Map<String, Category> loadAll(final Iterable<? extends String> keys) {
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
}
