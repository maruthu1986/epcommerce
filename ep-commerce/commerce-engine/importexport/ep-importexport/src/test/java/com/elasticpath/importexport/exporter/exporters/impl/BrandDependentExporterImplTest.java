/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.BrandService;

/**
 * Test for {@link BrandDependentExporterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.NonStaticInitializer", "PMD.TooManyStaticImports"})
public class BrandDependentExporterImplTest {
	private static final long CATALOG_UID = 14441;

	private final BrandDependentExporterImpl brandExporter = new BrandDependentExporterImpl();
	@Mock
	private BrandService brandService;
	@Mock
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;

	/**
	 * Test initialization.
	 *
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		brandExporter.setBrandService(brandService);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		brandExporter.initialize(exportContext, dependentExporterFilter);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsFiltered() {
		Brand brand1 = mock(Brand.class, "brand-1");
		Brand brand2 = mock(Brand.class, "brand-2");
		final List<Brand> brandsList = Arrays.asList(brand1, brand2);

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(true);
		when(brandService.findAllBrandsFromCatalog(CATALOG_UID)).thenReturn(brandsList);

		assertThat(brandExporter.findDependentObjects(CATALOG_UID)).isEqualTo(brandsList);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(brandService, times(1)).findAllBrandsFromCatalog(CATALOG_UID);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Collections.singletonList(Brand.class));

		exportContext.setDependencyRegistry(registry);

		final String brand1Guid = "062b1ae3-c769-4791-9a03-af72ff5215d1";
		final String brand2Guid = "62229118-6bca-4fc2-b08f-94619255d131";
		final String brand3Guid = "64cbe9c3-e51a-49f7-975d-5e87619f4956";

		registry.addGuidDependencies(Brand.class, new TreeSet<>(Arrays.asList(brand1Guid, brand2Guid, brand3Guid)));

		final Brand brand1 = mock(Brand.class, "brand-1");
		final Brand brand2 = mock(Brand.class, "brand-2");
		final Brand brand3 = mock(Brand.class, "brand-3");

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(false);

		Catalog dependentCatalog = mock(Catalog.class, "dependentCatalog");
		Catalog otherCatalog = mock(Catalog.class, "otherCatalog");

		when(dependentCatalog.getUidPk()).thenReturn(CATALOG_UID);
		when(otherCatalog.getUidPk()).thenReturn(0L);

		when(brand1.getCatalog()).thenReturn(dependentCatalog);
		when(brand2.getCatalog()).thenReturn(otherCatalog);
		when(brand3.getCatalog()).thenReturn(dependentCatalog);

		when(brandService.findByCode(brand1Guid)).thenReturn(brand1);
		when(brandService.findByCode(brand2Guid)).thenReturn(brand2);
		when(brandService.findByCode(brand3Guid)).thenReturn(brand3);

		List<Brand> result = brandExporter.findDependentObjects(CATALOG_UID);

		assertThat(result)
				.as("Missing brand1")
				.contains(brand1);

		assertThat(result)
				.as("Missing brand3")
				.contains(brand3);

		assertThat(result)
				.as("Brand2 is not a part of this catalog")
				.doesNotContain(brand2);

		assertThat(result)
				.size()
				.as("Other brands returned?")
				.isEqualTo(2);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(brandService, times(1)).findByCode(same(brand1Guid));
		verify(brandService, times(1)).findByCode(same(brand2Guid));
		verify(brandService, times(1)).findByCode(same(brand3Guid));
	}
}
