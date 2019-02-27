/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.CatalogService;

/**
 * Catalog exporter test.
 */
@RunWith(MockitoJUnitRunner.class)
public class CatalogExporterImplTest {

	private static final String CATALOG_CODE = "e30d77e7-f4a5-45dc-9ba8-287c51a468a2";

	private CatalogExporterImpl catalogExporter;

	@Mock
	private CatalogService catalogService;
	@Mock
	private DomainAdapter<Catalog, CatalogDTO> catalogAdapter;
	@Mock
	private ImportExportSearcher importExportSearcher;
	@Mock
	private AttributeService attributeService;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		final CatalogDTO catalogDTO = new CatalogDTO();
		final Catalog catalog = new CatalogImpl();
		catalog.setGuid(CATALOG_CODE);

		when(catalogService.findByCode(CATALOG_CODE)).thenReturn(catalog);
		when(catalogAdapter.createDtoObject()).thenReturn(catalogDTO);

		catalogExporter = new CatalogExporterImpl();
		catalogExporter.setImportExportSearcher(importExportSearcher);
		catalogExporter.setCatalogService(catalogService);
		catalogExporter.setCatalogAdapter(catalogAdapter);
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for catalogs to be exported.
	 */
	@Test
	public void testExporterInitialization() throws Exception {
		final List<String> catalogGuidList = new ArrayList<>();
		catalogGuidList.add(CATALOG_CODE);

		ExportConfiguration exportConfiguration = new ExportConfiguration();
		final SearchConfiguration searchConfiguration = new SearchConfiguration();
		searchConfiguration.setEpQLQuery("FIND Catalog");

		when(importExportSearcher.searchGuids(searchConfiguration, EPQueryType.CATALOG)).thenReturn(catalogGuidList);

		ExportContext exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());

		exportContext.setDependencyRegistry(new DependencyRegistry(Arrays.asList(new Class<?>[]{Catalog.class})));

		catalogExporter.initialize(exportContext);

		verify(importExportSearcher, times(1)).searchGuids(searchConfiguration, EPQueryType.CATALOG);
	}

	/**
	 * Check an export of one catalog with export criteria.
	 */
	@Test
	public void testProcessExportWithCriteria() throws Exception {
		testExporterInitialization();
		catalogExporter.processExport(System.out);
		Summary summary = catalogExporter.getContext().getSummary();
		assertThat(summary.getCounters())
				.size()
				.isEqualTo(1);

		assertThat(summary.getCounters())
				.containsKey(JobType.CATALOG);

		assertThat(summary.getCounters().get(JobType.CATALOG))
				.isEqualTo(1);

		assertThat(summary.getFailures())
				.size()
				.isEqualTo(0);

		assertThat(summary.getStartDate()).isNotNull();
		assertThat(summary.getElapsedTime()).isNotNull();
		assertThat(summary.getElapsedTime().toString()).isNotNull();
	}

	/**
	 * Tests that calling get attributes returns global attributes. (BB-1211)
	 */
	@Test
	public void testGetAttributesForGlobalAttributes() {
		final List<Attribute> attributeList = new ArrayList<>();
		AttributeImpl attribute = new AttributeImpl();
		// Note that no catalog is set on the attribute.
		attributeList.add(attribute);

		when(attributeService.findAllCatalogOrGlobalAttributes(0L)).thenReturn(attributeList);
		// Set here so that we get the expectations
		AttributeDependentExporterImpl attributeExporter = new AttributeDependentExporterImpl();
		attributeExporter.setAttributeService(attributeService);

		List<Attribute> actualList = attributeExporter.getByCatalog(0L);

		assertThat(actualList)
				.size()
				.as("The attribute above should be here")
				.isEqualTo(1);

		assertThat(actualList.get(0))
				.as("The attribute above should be here")
				.isEqualTo(attribute);
	}
}
