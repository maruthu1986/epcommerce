/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Exporter implementation for category object.
 */
@SuppressWarnings("PMD.GodClass")
public class CategoryExporterImpl extends AbstractExporterImpl<Category, CategoryDTO, String> {

	private CategoryLookup categoryLookup;
	private CategoryService categoryService;

	private DomainAdapter<Category, CategoryDTO> categoryAdapter;

	private Set<String> categoryGuidsFromSearchCriteria = Collections.emptySet();

	private ImportExportSearcher importExportSearcher;

	private static final Logger LOG = Logger.getLogger(CategoryExporterImpl.class);

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		categoryGuidsFromSearchCriteria = convertCategoryUidsToGuids(importExportSearcher.searchUids(context.getSearchConfiguration(),
				EPQueryType.CATEGORY));
		LOG.info("The codes list for " + categoryGuidsFromSearchCriteria.size() + " categories is retrieved from database.");
	}

	private Set<String> convertCategoryUidsToGuids(final Collection<Long> uids) {
		Set<String> guids = new LinkedHashSet<>();
		if (!uids.isEmpty()) {
			List<Category> categoryList = getCategoryLookup().findByUids(uids);
			for (Category category : categoryList) {
				guids.add(category.getGuid());
			}
		}
		return guids;
	}

	@Override
	protected List<Category> findByIDs(final List<String> subList) {
		List<Category> categoryList = new ArrayList<>();
		for (String categoryCode : subList) {
			Category category = getCategoryLookup().findByGuid(categoryCode);
			if (category == null) {
				LOG.error(new Message("IE-20700", categoryCode));
				continue;
			}

			if (!category.isLinked()) {
				categoryList.add(category);
			}

		}
		return categoryList;
	}

	@Override
	protected DomainAdapter<Category, CategoryDTO> getDomainAdapter() {
		return categoryAdapter;
	}

	@Override
	protected Class<? extends CategoryDTO> getDtoClass() {
		return CategoryDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.CATEGORY;
	}

	@Override
	protected List<String> getListExportableIDs() {
		Set<String> categoryGuidsSet = new HashSet<>(getContext().getDependencyRegistry().getDependentGuids(Category.class));
		categoryGuidsSet.addAll(categoryGuidsFromSearchCriteria);

		// find all parent category GUIDs in necessary tree order
		Set<String> resultSet = findAncestorCategoryGuidsWithTreeOrder(categoryGuidsSet);

		// add other category GUIDs for export
		resultSet.addAll(categoryGuidsSet);

		return new ArrayList<>(resultSet);
	}

	private Set<String> findAncestorCategoryGuidsWithTreeOrder(final Set<String> categoryGuids) {
		Set<Long> categoryUids = new LinkedHashSet<>();
		for (String categoryGuid : categoryGuids) {
			Category category = getCategoryLookup().findByGuid(categoryGuid);
			categoryUids.add(category.getUidPk());
		}
		Set<Long> ancestorCategoryUids = getCategoryService().findAncestorCategoryUidsWithTreeOrder(categoryUids);
		return convertCategoryUidsToGuids(ancestorCategoryUids);
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Category.class };
	}

	@Override
	protected void addDependencies(final List<Category> objects, final DependencyRegistry dependencyRegistry) {

		if (dependencyRegistry.supportsDependency(Catalog.class)) {
			addCatalogsIntoRegistry(objects, dependencyRegistry);
		}

		if (dependencyRegistry.supportsDependency(CategoryType.class)) {
			addCategoryTypesIntoRegistry(objects, dependencyRegistry);
		}

		if (dependencyRegistry.supportsDependency(Attribute.class)) {
			addAttributesIntoRegistry(objects, dependencyRegistry);
		}
	}

	private void addAttributesIntoRegistry(final List<Category> categories, final DependencyRegistry dependencyRegistry) {
		final NavigableSet<String> dependents = new TreeSet<>();
		for (Category category : categories) {
			for (Entry<String, AttributeValue> entry : category.getAttributeValueMap().entrySet()) {
				dependents.add(entry.getValue().getAttribute().getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(Attribute.class, dependents);
	}

	private void addCategoryTypesIntoRegistry(final List<Category> categories, final DependencyRegistry dependencyRegistry) {
		final NavigableSet<String> dependents = new TreeSet<>();
		for (Category category : categories) {
			if (category.getCategoryType() != null) {
				dependents.add(category.getCategoryType().getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(CategoryType.class, dependents);
	}

	/*
	 * Puts directly influencing catalogs, as well as indirect (catalog containing exported category is virtual).
	 */
	private void addCatalogsIntoRegistry(final List<Category> objects, final DependencyRegistry dependencyRegistry) {
		final VirtualCatalogDependencyHelper virtualDependencyHelper = new VirtualCatalogDependencyHelper();
		final NavigableSet<String> catalogGuidSet = new TreeSet<>();
		for (Category category : objects) {
			catalogGuidSet.add(category.getCatalog().getGuid());
			virtualDependencyHelper.addInfluencingCatalogs(category, dependencyRegistry);
		}
		dependencyRegistry.addGuidDependencies(Catalog.class, catalogGuidSet);
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	protected CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Sets the categoryAdapter.
	 *
	 * @param categoryAdapter the categoryAdapter to set
	 */
	public void setCategoryAdapter(final DomainAdapter<Category, CategoryDTO> categoryAdapter) {
		this.categoryAdapter = categoryAdapter;
	}

	/**
	 * Gets importExportSearcher.
	 *
	 * @return importExportSearcher
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * Sets importExportSearcher.
	 * @param importExportSearcher the ImportExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}
}
