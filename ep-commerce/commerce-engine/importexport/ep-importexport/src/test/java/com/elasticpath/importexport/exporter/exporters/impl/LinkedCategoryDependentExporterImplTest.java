/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Test for {@link LinkedCategoryDependentExporterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class LinkedCategoryDependentExporterImplTest {
	private final LinkedCategoryDependentExporterImpl linkedCategoryExporter = new LinkedCategoryDependentExporterImpl();
	@Mock
	private CategoryService categoryService;
	@Mock
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private static final long CATALOG_UID = 14441;

	/**
	 * Test initialization.
	 *
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		linkedCategoryExporter.setCategoryService(categoryService);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		linkedCategoryExporter.initialize(exportContext, dependentExporterFilter);
	}

	/**
	 * Tests finding dependent objects.
	 */
	@Test
	public void testFindDependentObjects() {
		Category linkedCategory1 = mock(Category.class, "linkedCategory-1");
		Category linkedCategory2 = mock(Category.class, "linkedCategory-2");
		final List<Category> linkedCategoryList = Arrays.asList(linkedCategory1, linkedCategory2);

		when(categoryService.findLinkedCategories(CATALOG_UID)).thenReturn(linkedCategoryList);

		assertThat(linkedCategoryExporter.findDependentObjects(CATALOG_UID))
				.isEqualTo(linkedCategoryList);

		verify(categoryService, times(1)).findLinkedCategories(CATALOG_UID);
	}

	/**
	 * Tests finding dependent objects when we the {@link DependencyRegistry} supports {@link Catalog}s.
	 */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Collections.singletonList(Catalog.class));
		exportContext.setDependencyRegistry(registry);

		final String catalog1Guid = "cb96ceb5-7c10-41a6-b707-cef1ba6851e7";
		final String catalog2Guid = "cd6a60d3-4c41-4c91-a023-2628f72c617f";

		final Catalog catalog1 = mock(Catalog.class, "catalog-1");
		final Catalog catalog2 = mock(Catalog.class, "catalog-2");

		final Category category1 = mock(Category.class, "category-1");
		final Category category2 = mock(Category.class, "category-2");
		final Category category3 = mock(Category.class, "category-3");


		when(catalog1.getGuid()).thenReturn(catalog1Guid);
		when(catalog2.getGuid()).thenReturn(catalog2Guid);

		when(category1.getCatalog()).thenReturn(catalog1);
		when(category2.getCatalog()).thenReturn(catalog2);
		when(category3.getCatalog()).thenReturn(catalog1);

		when(categoryService.findLinkedCategories(CATALOG_UID)).thenReturn(Arrays.asList(category1, category2, category3));

		List<Category> result = linkedCategoryExporter.findDependentObjects(CATALOG_UID);

		final int expectedCategoryListSize = 3;
		assertThat(result)
				.as("Missing category-1")
				.contains(category1);
		assertThat(result)
				.as("Missing category-2")
				.contains(category2);
		assertThat(result)
				.as("Missing category-3")
				.contains(category3);
		assertThat(result)
				.size()
				.as("Other Categories returned?")
				.isEqualTo(expectedCategoryListSize);

		Set<String> catalogDependencies = registry.getDependentGuids(Catalog.class);

		assertThat(catalogDependencies)
				.as("Missing catalog1Guid")
				.contains(catalog1Guid);
		assertThat(catalogDependencies)
				.as("Missing catalog2Guid")
				.contains(catalog2Guid);
		assertThat(catalogDependencies)
				.size()
				.as("Other catalogs reutrned?")
				.isEqualTo(2);

		verify(categoryService, times(1)).findLinkedCategories(CATALOG_UID);
	}
}
