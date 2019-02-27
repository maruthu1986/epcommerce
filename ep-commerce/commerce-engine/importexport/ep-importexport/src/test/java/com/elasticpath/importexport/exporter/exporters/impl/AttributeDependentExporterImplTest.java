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
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.CatalogService;

/**
 * Test for {@link AttributeDependentExporterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.NonStaticInitializer", "PMD.TooManyStaticImports"})
public class AttributeDependentExporterImplTest {

	private final AttributeDependentExporterImpl attributeExporter = new AttributeDependentExporterImpl();
	@Mock
	private AttributeService attributeService;
	@Mock
	private CatalogService catalogService;
	@Mock
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	@Mock
	private Catalog catalog;
	private static final long CATALOG_UID = 885141;

	/**
	 * Test initialization.
	 *
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		attributeExporter.setAttributeService(attributeService);

		attributeExporter.setCatalogService(catalogService);

		when(catalogService.getCatalog(CATALOG_UID)).thenReturn(catalog);
		when(catalog.getUidPk()).thenReturn(CATALOG_UID);

		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		attributeExporter.initialize(exportContext, dependentExporterFilter);

		when(catalog.isMaster()).thenReturn(true);
	}

	/**
	 * Virtual catalogs should not have their attributes exported.
	 */
	@Test
	public void testCatalogIsNotMaster() {

		when(catalog.isMaster()).thenReturn(false);

		List<Attribute> result = attributeExporter.findDependentObjects(CATALOG_UID);
		assertThat(result).isNotNull();
		assertThat(result)
				.as("Attributes shouldn't be exported with virtual catalogs")
				.isEmpty();
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsFiltered() {
		final Attribute attribute1 = mock(Attribute.class, "attribute-1");
		final Attribute attribute2 = mock(Attribute.class, "attribute-2");
		final Attribute attribute3 = mock(Attribute.class, "attribute-3");

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(true);
		Catalog otherCatalog = mock(Catalog.class, "otherCatalog");
		when(otherCatalog.getUidPk()).thenReturn(0L);
		when(attribute1.getCatalog()).thenReturn(catalog);
		when(attribute2.getCatalog()).thenReturn(otherCatalog);
		when(attribute3.getCatalog()).thenReturn(catalog);

		when(attributeService.findAllCatalogOrGlobalAttributes(CATALOG_UID)).thenReturn(Arrays.asList(attribute1, attribute2, attribute3));

		List<Attribute> result = attributeExporter.findDependentObjects(CATALOG_UID);
		assertThat(result)
				.as("Missing attribute1")
				.contains(attribute1);

		assertThat(result)
				.as("Missing attribute3")
				.contains(attribute3);

		assertThat(result)
				.as("Attribute2 is not a part of this catalog")
				.doesNotContain(attribute2);

		assertThat(result)
				.size()
				.as("Other attributes in list?")
				.isEqualTo(2);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Collections.<Class<?>>singletonList(Attribute.class));
		exportContext.setDependencyRegistry(registry);

		final String attribute1Guid = "56b5aa17-2978-42bc-a5a9-d84b8be6a89d";
		final String attribute2Guid = "29466588-d328-4caf-91c1-8e841636decc";
		final String attribute3Guid = "a266f8b6-1dfc-4c34-a4a7-7fd22d4e4fb8";
		final String attribute4Guid = "1c230cad-eb53-453f-ab61-c0cfd228832a";

		registry.addGuidDependency(Attribute.class, attribute1Guid);
		registry.addGuidDependencies(Attribute.class, new TreeSet<>(Arrays.asList(attribute2Guid, attribute3Guid)));
		registry.addGuidDependency(Attribute.class, attribute4Guid);

		final Attribute attribute1 = mock(Attribute.class, "attribute-globalDepCatalog");
		final Attribute attribute2 = mock(Attribute.class, "attribute-otherCatalog");
		final Attribute attribute3 = mock(Attribute.class, "attribute-depCatalog");
		final Attribute attribute4 = mock(Attribute.class, "attribute-globalOtherCatalog");

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(false);

		Catalog dependentCatalog = mock(Catalog.class, "dependentCatalog");
		Catalog otherCatalog = mock(Catalog.class, "otherCatalog");

		when(dependentCatalog.getUidPk()).thenReturn(CATALOG_UID);
		when(otherCatalog.getUidPk()).thenReturn(0L);
		when(attribute1.getCatalog()).thenReturn(dependentCatalog);

		when(attribute2.getCatalog()).thenReturn(otherCatalog);
		when(attribute2.isGlobal()).thenReturn(false);

		when(attribute3.getCatalog()).thenReturn(dependentCatalog);

		when(attribute4.getCatalog()).thenReturn(otherCatalog);
		when(attribute4.isGlobal()).thenReturn(true);

		when(attributeService.findByKey(attribute1Guid)).thenReturn(attribute1);
		when(attributeService.findByKey(attribute2Guid)).thenReturn(attribute2);
		when(attributeService.findByKey(attribute3Guid)).thenReturn(attribute3);
		when(attributeService.findByKey(attribute4Guid)).thenReturn(attribute4);

		List<Attribute> result = attributeExporter.findDependentObjects(CATALOG_UID);
		final int expectedAttributeListSize = 3;
		assertThat(result)
				.as("Missing attribute1")
				.contains(attribute1);

		assertThat(result)
				.as("Missing attribute3")
				.contains(attribute3);

		assertThat(result)
				.as("Attribute2 is not a part of this catalog")
				.doesNotContain(attribute2);

		assertThat(result)
				.as("Attribute4 is not a part of this catalog")
				.contains(attribute4);

		assertThat(result)
				.size()
				.as("Other attributes in list?")
				.isEqualTo(expectedAttributeListSize);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(attributeService, times(1)).findByKey(attribute1Guid);
		verify(attributeService, times(1)).findByKey(attribute2Guid);
		verify(attributeService, times(1)).findByKey(attribute3Guid);
		verify(attributeService, times(1)).findByKey(attribute4Guid);
	}
}
