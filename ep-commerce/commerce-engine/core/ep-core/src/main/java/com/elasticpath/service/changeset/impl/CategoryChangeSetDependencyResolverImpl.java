/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;

/**
 * The class which resolves the change set dependent objects of categories.
 */
public class CategoryChangeSetDependencyResolverImpl implements
		ChangeSetDependencyResolver {

	private CategoryLookup categoryLookup;

	@Override
	public Category getObject(final BusinessObjectDescriptor businessObjectDescriptor, final Class<?> objectClass) {
		if (Category.class.isAssignableFrom(objectClass)) {
			return getCategoryLookup().findByGuid(businessObjectDescriptor.getObjectIdentifier());
		}
		return null;
	}
	
	@Override
	public Set<?> getChangeSetDependency(final Object object) {
		if (object instanceof Category) {
			Set<Object> dependents = new LinkedHashSet<>();
			Category category = (Category) object;
			Category parent = getCategoryLookup().findParent(category);
			if (parent != null) {
				dependents.add(parent);
			}

			if (category.isLinked()) {
				dependents.add(category.getMasterCategory());
			}
			dependents.add(category.getCategoryType());
			
			return dependents;
		}
		return Collections.emptySet();
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}
}
