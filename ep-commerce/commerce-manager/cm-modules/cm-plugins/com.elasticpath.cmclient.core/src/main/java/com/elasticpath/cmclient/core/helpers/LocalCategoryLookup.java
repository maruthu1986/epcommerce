/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * A "Local" implementation of {@link CategoryLookup} that delegates to a remoted CategoryLookup
 * via Spring-http and caches results locally.
 * 
 * This class is NOT thread-safe, and not particularly heap friendly either.  The purpose of this
 * class is to provide a local cache for individual SWT components - not to provide a global cache for all of
 * cmclient.
 */
public class LocalCategoryLookup implements CategoryLookup {

	private final Map<Category, Category> categoryParents = new HashMap<>();

	@Override
	public <C extends Category> C findByUid(final long uid) {
		return getRemoteCategoryLookup().findByUid(uid);
	}

	@Override
	public <C extends Category> List<C> findByUids(final Collection<Long> uids) {
		return getRemoteCategoryLookup().findByUids(uids);
	}

	@Override
	public <C extends Category> C findByGuid(final String guid) {
		return getRemoteCategoryLookup().findByGuid(guid);
	}

	@Override
	public <C extends Category> C findByCategoryCodeAndCatalog(final String categoryCode, final Catalog catalog) {
		return getRemoteCategoryLookup().findByCategoryCodeAndCatalog(categoryCode, catalog);
	}

	@Override
	public <C extends Category> C findByCategoryAndCatalogCode(final String categoryCode, final String catalogCode) {
		return getRemoteCategoryLookup().findByCategoryAndCatalogCode(categoryCode, catalogCode);
	}

	@Override
	public <C extends Category> C findByCompoundCategoryAndCatalogCodes(final String compoundGuid) {
		return getRemoteCategoryLookup().findByCompoundCategoryAndCatalogCodes(compoundGuid);
	}

	@Override
	public <C extends Category> List<C> findChildren(final Category category) {
		return getRemoteCategoryLookup().findChildren(category);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Category> C findParent(final Category category) {
		if (!category.hasParent()) {
			return null;
		}

		final Category cached = categoryParents.get(category);
		if (cached != null) {
			return (C) cached;
		}

		final Category parent = getRemoteCategoryLookup().findParent(category);
		categoryParents.put(category, parent);

		return (C) parent;
	}

	protected CategoryLookup getRemoteCategoryLookup() {
		return ServiceLocator.getService(ContextIdNames.CATEGORY_LOOKUP);
	}
}
