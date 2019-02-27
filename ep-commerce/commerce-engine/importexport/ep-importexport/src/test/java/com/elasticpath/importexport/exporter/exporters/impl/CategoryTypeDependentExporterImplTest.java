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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.CategoryTypeService;

/**
 * Test for {@link CategoryTypeDependentExporterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.NonStaticInitializer", "PMD.TooManyStaticImports"})
public class CategoryTypeDependentExporterImplTest {
	private final CategoryTypeDependentExporterImpl categoryTypeExporter = new CategoryTypeDependentExporterImpl();
	@Mock
	private CategoryTypeService categoryTypeService;
	@Mock
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private static int mockCounter;
	private static final long CATALOG_UID = 66478;

	/**
	 * Test initialization.
	 *
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		categoryTypeExporter.setCategoryTypeService(categoryTypeService);

		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		categoryTypeExporter.initialize(exportContext, dependentExporterFilter);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsFiltered() {
		CategoryType categoryType1 = mock(CategoryType.class, "categoryType-1");
		CategoryType categoryType2 = mock(CategoryType.class, "categoryType-2");
		final List<CategoryType> categoryTypeList = Arrays.asList(categoryType1, categoryType2);

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(true);
		when(categoryTypeService.findAllCategoryTypeFromCatalog(CATALOG_UID)).thenReturn(categoryTypeList);

		assertThat(categoryTypeExporter.findDependentObjects(CATALOG_UID)).isEqualTo(categoryTypeList);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(categoryTypeService, times(1)).findAllCategoryTypeFromCatalog(CATALOG_UID);
	}

	private Attribute mockAttribute(final String guid, final boolean global, final Catalog catalog) {
		final Attribute attribute = mock(Attribute.class, "attribute-" + ++mockCounter);

		when(attribute.getGuid()).thenReturn(guid);
		when(attribute.isGlobal()).thenReturn(global);
		when(attribute.getCatalog()).thenReturn(catalog);

		return attribute;
	}

	private void setupMocksForAttributes(final CategoryType categoryType, final Attribute... attributes) {
		Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				AttributeGroupAttribute attributeGroupAttribute = mock(AttributeGroupAttribute.class,
						"attributeGroupAttribute-" + ++mockCounter);
				attributeGroupAttributes.add(attributeGroupAttribute);

				when(attributeGroupAttribute.getAttribute()).thenReturn(attribute);
			}
		}

		AttributeGroup attributeGroup = mock(AttributeGroup.class, "attributeGroup-" + ++mockCounter);

		when(attributeGroup.getAttributeGroupAttributes()).thenReturn(attributeGroupAttributes);
		when(categoryType.getAttributeGroup()).thenReturn(attributeGroup);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.asList(CategoryType.class, Attribute.class));
		exportContext.setDependencyRegistry(registry);

		final String categoryType1Guid = "f64aeae3-a91c-4d79-9cae-c1edc88bfd09";
		final String categoryType2Guid = "e72265bd-4cde-4994-bd6a-129282448138";
		final String categoryType3Guid = "0e186fdd-2e27-4b8f-9495-ab5228a5dc57";

		registry.addGuidDependency(CategoryType.class, categoryType1Guid);
		registry.addGuidDependencies(CategoryType.class, new TreeSet<>(Arrays.asList(categoryType2Guid, categoryType3Guid)));

		final String commonAttributeGuid = "e264176e-1bcb-4202-be58-4e50e48068b9";
		final String dependentCatalogAttributeGuid = "bd0af125-d65b-49a4-92ba-e898ad53b2bb";
		final String otherCatalogAttributeGuid = "008ae3be-699c-4da3-8aef-18d036cb0c62";
		final String globalAttributeGuid = "be4583f4-31ad-4e48-89c7-64fc264c76f6";

		final CategoryType categoryType1 = mock(CategoryType.class, "categoryType-1");
		final CategoryType categoryType2 = mock(CategoryType.class, "categoryType-2");
		final CategoryType categoryType3 = mock(CategoryType.class, "categoryType-3");

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(false);

		Catalog dependentCatalog = mock(Catalog.class, "dependentCatalog");
		Catalog otherCatalog = mock(Catalog.class, "otherCatalog");

		when(dependentCatalog.getUidPk()).thenReturn(CATALOG_UID);
		when(otherCatalog.getUidPk()).thenReturn(0L);

		when(categoryType1.getCatalog()).thenReturn(dependentCatalog);
		when(categoryType2.getCatalog()).thenReturn(otherCatalog);
		when(categoryType3.getCatalog()).thenReturn(otherCatalog);

		Attribute commonAttribute = mockAttribute(commonAttributeGuid, false, dependentCatalog);
		Attribute dependentCatalogAttribute = mockAttribute(dependentCatalogAttributeGuid, false, dependentCatalog);
		Attribute otherCatalogAttribute = mockAttribute(otherCatalogAttributeGuid, false, otherCatalog);
		Attribute globalAttribute = mockAttribute(globalAttributeGuid, true, otherCatalog);

		setupMocksForAttributes(categoryType1, commonAttribute, dependentCatalogAttribute);
		setupMocksForAttributes(categoryType2, commonAttribute, otherCatalogAttribute);
		setupMocksForAttributes(categoryType3, commonAttribute, globalAttribute);

		when(categoryTypeService.findByGuid(categoryType1Guid)).thenReturn(categoryType1);
		when(categoryTypeService.findByGuid(categoryType2Guid)).thenReturn(categoryType2);
		when(categoryTypeService.findByGuid(categoryType3Guid)).thenReturn(categoryType3);


		List<CategoryType> result = categoryTypeExporter.findDependentObjects(CATALOG_UID);
		assertThat(result)
				.as("Missing categoryType1")
				.contains(categoryType1);
		assertThat(result)
				.as("categoryType2 is not a part of this catalog")
				.doesNotContain(categoryType2);
		assertThat(result)
				.as("categoryType3 is not a part of this catalog")
				.doesNotContain(categoryType3);
		assertThat(result)
				.size()
				.as("Other CategoryTypes returned?")
				.isEqualTo(1);

		Set<String> attributeDependencies = registry.getDependentGuids(Attribute.class);

		final int expectedAttributeSetSize = 3;

		assertThat(attributeDependencies)
				.as("Missing commonAttributeGuid")
				.contains(commonAttributeGuid);
		assertThat(attributeDependencies)
				.as("Missing dependentCatalogAttributeGuid")
				.contains(dependentCatalogAttributeGuid);
		assertThat(attributeDependencies)
				.as("OtherCatalogAttributeGuid is for a CategoryType in another catalog")
				.doesNotContain(otherCatalogAttributeGuid);
		assertThat(attributeDependencies)
				.as("Missing globalAttributeGuid even though its for another catalog")
				.contains(globalAttributeGuid);
		assertThat(attributeDependencies)
				.size()
				.as("Other attribute dependencies?")
				.isEqualTo(expectedAttributeSetSize);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(categoryTypeService, times(1)).findByGuid(categoryType1Guid);
		verify(categoryTypeService, times(1)).findByGuid(categoryType2Guid);
		verify(categoryTypeService, times(1)).findByGuid(categoryType3Guid);
	}
}
