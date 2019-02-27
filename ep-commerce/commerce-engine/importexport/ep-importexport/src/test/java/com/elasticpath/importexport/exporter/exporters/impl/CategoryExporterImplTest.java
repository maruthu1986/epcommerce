/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Category exporter test.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryExporterImplTest {

	private static final String QUERY = "FIND Category WHERE CatalogCode='1000500'";

	private static final long CATEGORY_UID = 1L;
	private static final String CATEGORY_GUID = "6a4c3748-01d2-40aa-8447-a931042c0d7f";
	private static final String CATALOG_GUID = "1cac9cb9-5f56-4d35-b8b4-aa8a3d05dbda";
	private static final String CATEGORY_TYPE_GUID = "300b7b76-88a1-4c8b-a732-2aa2d4985349";
	private static final String ATTRIBUTE_GUID = "2a32d491-c48d-49de-8bf0-a4879b116365";
	private CategoryExporterImpl categoryExporter;
	private ExportContext exportContext;
	@Mock
	private CategoryLookup categoryLookup;
	@Mock
	private CategoryService categoryService;
	@Mock
	private ImportExportSearcher importExportSearcher;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		@SuppressWarnings("unchecked") final DomainAdapter<Category, CategoryDTO> categoryAdapter = mock(DomainAdapter.class);

		final Category category = new CategoryImpl();
		category.setUidPk(CATEGORY_UID);
		category.setGuid(CATEGORY_GUID);
		Catalog catalog = new CatalogImpl();
		catalog.setCode(CATALOG_GUID);
		AttributeValue attributeValue = new CategoryAttributeValueImpl();
		Attribute attribute = new AttributeImpl();
		attribute.setGuid(ATTRIBUTE_GUID);
		attributeValue.setAttribute(attribute);
		CategoryType categoryType = new CategoryTypeImpl();
		categoryType.setGuid(CATEGORY_TYPE_GUID);
		categoryType.setCatalog(catalog);
		category.setAttributeValueMap(Collections.singletonMap("atr_key", attributeValue));
		category.setCategoryType(categoryType);
		category.setCatalog(catalog);

		final CategoryDTO dto = new CategoryDTO();

		when(categoryLookup.findByUids(Collections.singletonList(CATEGORY_UID))).thenReturn(Collections.singletonList(category));
		when(categoryLookup.findByGuid(CATEGORY_GUID)).thenReturn(category);
		when(categoryService.findAncestorCategoryUidsWithTreeOrder(Collections.singleton(category.getUidPk()))).thenReturn(new HashSet<>());
		when(categoryAdapter.createDtoObject()).thenReturn(dto);


		categoryExporter = new CategoryExporterImpl();
		categoryExporter.setImportExportSearcher(importExportSearcher);
		categoryExporter.setCategoryLookup(categoryLookup);
		categoryExporter.setCategoryService(categoryService);
		categoryExporter.setCategoryAdapter(categoryAdapter);
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for categories to be exported.
	 */
	@Test
	public void testExporterInitialization() throws ConfigurationException {
		final List<Long> categoryUidPkList = new ArrayList<>();
		categoryUidPkList.add(CATEGORY_UID);

		ExportConfiguration exportConfiguration = new ExportConfiguration();
		SearchConfiguration searchConfiguration = new SearchConfiguration();

		ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
		exportConfiguration.setExporterConfiguration(exporterConfiguration);

		searchConfiguration.setEpQLQuery(QUERY);
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());
		exportContext.setDependencyRegistry(new DependencyRegistry(Arrays
				.asList(new Class<?>[]{Category.class, Catalog.class, CategoryType.class, Attribute.class})));

		when(importExportSearcher.searchUids(searchConfiguration, EPQueryType.CATEGORY)).thenReturn(categoryUidPkList);
		categoryExporter.initialize(exportContext);
		verify(importExportSearcher, times(1)).searchUids(searchConfiguration, EPQueryType.CATEGORY);
	}

	/**
	 * Check an export of one product without export criteria.
	 */
	@Test
	public void testProcessExportWithoutCriteria() throws ConfigurationException {
		testExporterInitialization();
		categoryExporter.processExport(System.out);
		Summary summary = categoryExporter.getContext().getSummary();
		assertThat(summary.getCounters())
				.size()
				.isEqualTo(1);
		assertThat(summary.getCounters())
				.containsKey(JobType.CATEGORY);
		assertThat(summary.getCounters().get(JobType.CATEGORY))
				.isEqualTo(1);
		assertThat(summary.getFailures())
				.size()
				.isEqualTo(0);
		assertThat(summary.getStartDate())
				.isNotNull();
		assertThat(summary.getElapsedTime())
				.isNotNull();
		assertThat(summary.getElapsedTime().toString())
				.isNotNull();
		DependencyRegistry registry = exportContext.getDependencyRegistry();
		assertThat(registry.getDependentGuids(Catalog.class))
				.as("Missing catalog dependency")
				.contains(CATALOG_GUID);
		assertThat(registry.getDependentGuids(Catalog.class))
				.size()
				.as("Extra catalog uid in dependency registry")
				.isEqualTo(1);
		assertThat(registry.getDependentGuids(CategoryType.class))
				.as("Missing category type dependency")
				.contains(CATEGORY_TYPE_GUID);
		assertThat(registry.getDependentGuids(CategoryType.class))
				.size()
				.as("Extra category type uid in dependency registry")
				.isEqualTo(1);
		assertThat(registry.getDependentGuids(Attribute.class))
				.as("Missing attribute dependency")
				.contains(ATTRIBUTE_GUID);
		assertThat(registry.getDependentGuids(Attribute.class))
				.size()
				.as("Extra attribute uid in dependency registry")
				.isEqualTo(1);
	}

}
