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
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Test for {@link SkuOptionDependentExporterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.NonStaticInitializer", "PMD.TooManyStaticImports"})
public class SkuOptionDependentExporterImplTest {
	private final SkuOptionDependentExporterImpl skuOptionExporter = new SkuOptionDependentExporterImpl();
	@Mock
	private SkuOptionService skuOptionService;
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
		skuOptionExporter.setSkuOptionService(skuOptionService);

		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		skuOptionExporter.initialize(exportContext, dependentExporterFilter);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsFiltered() {
		SkuOption skuOption1 = mock(SkuOption.class, "skuOption-1");
		SkuOption skuOption2 = mock(SkuOption.class, "skuOption-2");
		final List<SkuOption> skuOptionList = Arrays.asList(skuOption1, skuOption2);
		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(true);
		when(skuOptionService.findAllSkuOptionFromCatalog(CATALOG_UID)).thenReturn(skuOptionList);

		assertThat(skuOptionExporter.findDependentObjects(CATALOG_UID))
				.isEqualTo(skuOptionList);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(skuOptionService, times(1)).findAllSkuOptionFromCatalog(CATALOG_UID);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>>asList(SkuOption.class));
		exportContext.setDependencyRegistry(registry);

		final String skuOption1Guid = "9be92e32-5975-4c6b-93be-5ce49279a472";
		final String skuOption2Guid = "d85820d2-8038-4f7a-9bac-ac91456c2ca0";
		final String skuOption3Guid = "fbe11ec6-78e9-4d6f-b1db-6fcc9a4aba8a";
		registry.addGuidDependency(SkuOption.class, skuOption1Guid);
		registry.addGuidDependencies(SkuOption.class, new TreeSet<>(Arrays.asList(skuOption2Guid, skuOption3Guid)));

		final SkuOption skuOption1 = mock(SkuOption.class, "skuOption-1");
		final SkuOption skuOption2 = mock(SkuOption.class, "skuOption-2");
		final SkuOption skuOption3 = mock(SkuOption.class, "skuOption-3");

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(false);

		Catalog dependentCatalog = mock(Catalog.class, "dependentCatalog");
		Catalog otherCatalog = mock(Catalog.class, "otherCatalog");

		when(dependentCatalog.getUidPk()).thenReturn(CATALOG_UID);
		when(otherCatalog.getUidPk()).thenReturn(0L);

		when(skuOption1.getCatalog()).thenReturn(dependentCatalog);
		when(skuOption2.getCatalog()).thenReturn(otherCatalog);
		when(skuOption3.getCatalog()).thenReturn(dependentCatalog);

		when(skuOptionService.findByKey(skuOption1Guid)).thenReturn(skuOption1);
		when(skuOptionService.findByKey(skuOption2Guid)).thenReturn(skuOption2);
		when(skuOptionService.findByKey(skuOption3Guid)).thenReturn(skuOption3);


		List<SkuOption> result = skuOptionExporter.findDependentObjects(CATALOG_UID);

		assertThat(result)
				.as("Missing skuOption1")
				.contains(skuOption1);
		assertThat(result)
				.as("Missing skuOption3")
				.contains(skuOption3);
		assertThat(result)
				.as("SkuOption2 is not a part of this catalog")
				.doesNotContain(skuOption2);
		assertThat(result)
				.size()
				.as("Other skuOptions returned?")
				.isEqualTo(2);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(skuOptionService, times(1)).findByKey(skuOption1Guid);
		verify(skuOptionService, times(1)).findByKey(skuOption2Guid);
		verify(skuOptionService, times(1)).findByKey(skuOption3Guid);
	}
}
