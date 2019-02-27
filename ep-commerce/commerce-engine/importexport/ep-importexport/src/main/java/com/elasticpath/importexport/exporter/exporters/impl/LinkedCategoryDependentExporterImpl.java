/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.service.catalog.CategoryService;

/** Responsible for exporting linked {@link Category}s. */
public class LinkedCategoryDependentExporterImpl extends AbstractDependentExporterImpl<Category, LinkedCategoryDTO, CategoryDTO> {
	private CategoryService categoryService;

	@Override
	public void bindWithPrimaryObject(final List<LinkedCategoryDTO> dependentDtoObjects, final CategoryDTO primaryDtoObject) {
		primaryDtoObject.setLinkedCategoryDTOList(dependentDtoObjects);
	}

	@Override
	public List<Category> findDependentObjects(final long primaryObjectUid) {
		List<Category> linkedCategories = categoryService.findLinkedCategories(primaryObjectUid);
		addDependenciesForLinkedCategories(linkedCategories, getContext().getDependencyRegistry());
		return linkedCategories;
	}

	private void addDependenciesForLinkedCategories(final List<Category> linkedCategories, final DependencyRegistry dependencyRegistry) {
		if (dependencyRegistry.supportsDependency(Catalog.class)) {
			NavigableSet<String> catalogGuids = new TreeSet<>();

			for (Category linkedCategory : linkedCategories) {
				catalogGuids.add(linkedCategory.getCatalog().getGuid());
			}

			dependencyRegistry.addGuidDependencies(Catalog.class, catalogGuids);
		}
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
}
