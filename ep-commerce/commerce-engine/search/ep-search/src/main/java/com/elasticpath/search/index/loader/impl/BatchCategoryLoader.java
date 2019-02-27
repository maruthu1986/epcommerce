/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
/**
 * 
 */
package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * Fetches a batch of {@link Category}s.
 */
public class BatchCategoryLoader extends AbstractEntityLoader<Category> {

	private CategoryLookup categoryLookup;

	/**
	 * Loads the {@link Category}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link Category}s
	 */
	@Override
	public Collection<Category> loadBatch() {

		return getCategoryLookup().findByUids(getUidsToLoad());
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}
}
