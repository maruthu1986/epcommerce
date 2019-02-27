/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.impl;

import com.google.common.base.Predicate;

import com.elasticpath.domain.catalog.Category;

/**
 * A Predicate which returns true if the Category is available.
 */
public class CategoryIsAvailablePredicate implements Predicate<Category> {
	@Override
	public boolean apply(final Category category) {
		return category.isAvailable();
	}
}
