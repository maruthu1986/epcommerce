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
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.ProductTypeService;

/**
 * Test for {@link ProductTypeDependentExporterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.NonStaticInitializer", "PMD.TooManyStaticImports", "PMD.ExcessiveMethodLength"})
public class ProductTypeDependentExporterImplTest {
	private final ProductTypeDependentExporterImpl productTypeExporter = new ProductTypeDependentExporterImpl();
	@Mock
	private ProductTypeService productTypeService;
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
		productTypeExporter.setProductTypeService(productTypeService);

		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		productTypeExporter.initialize(exportContext, dependentExporterFilter);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsFiltered() {
		ProductType productType1 = mock(ProductType.class, "productType-1");
		ProductType productType2 = mock(ProductType.class, "productType-2");
		final List<ProductType> productTypeList = Arrays.asList(productType1, productType2);

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(true);
		when(productTypeService.findAllProductTypeFromCatalog(CATALOG_UID)).thenReturn(productTypeList);

		assertThat(productTypeExporter.findDependentObjects(CATALOG_UID))
				.isEqualTo(productTypeList);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(productTypeService, times(1)).findAllProductTypeFromCatalog(CATALOG_UID);
	}

	private SkuOption mockSkuOption(final String guid) {
		final SkuOption skuOption = mock(SkuOption.class, "skuOption-" + ++mockCounter);

		when(skuOption.getGuid()).thenReturn(guid);

		return skuOption;
	}

	private Attribute mockAttribute(final String guid, final boolean global, final Catalog catalog) {
		final Attribute attribute = mock(Attribute.class, "attribute-" + ++mockCounter);

		when(attribute.getGuid()).thenReturn(guid);
		when(attribute.isGlobal()).thenReturn(global);
		when(attribute.getCatalog()).thenReturn(catalog);

		return attribute;
	}

	private void setupMocksForProductAttributes(final ProductType productType, final Attribute... attributes) {
		Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				AttributeGroupAttribute attributeGroupAttribute = mock(AttributeGroupAttribute.class,
						"attributeGroupAttribute-" + ++mockCounter);
				attributeGroupAttributes.add(attributeGroupAttribute);
				when(attributeGroupAttribute.getAttribute()).thenReturn(attribute);
			}
		}

		when(productType.getProductAttributeGroupAttributes()).thenReturn(attributeGroupAttributes);
	}

	private void setupMocksForSkuAttributes(final ProductType productType, final Attribute... attributes) {
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
		when(productType.getSkuAttributeGroup()).thenReturn(attributeGroup);
		when(attributeGroup.getAttributeGroupAttributes()).thenReturn(attributeGroupAttributes);
	}

	private ProductType mockProductType(final String name, final String guid, final Catalog catalog, final SkuOption... skuOption) {
		final ProductType productType = mock(ProductType.class, name);

		when(productType.getCatalog()).thenReturn(catalog);
		when(productType.getSkuOptions()).thenReturn(new HashSet<>(Arrays.asList(skuOption)));
		when(productTypeService.findByGuid(guid)).thenReturn(productType);

		return productType;
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>>asList(ProductType.class, Attribute.class, SkuOption.class));
		exportContext.setDependencyRegistry(registry);

		final String productType1Guid = "a8c730ab-cf91-4178-a538-d4135c31ddad";
		final String productType2Guid = "abe10132-a7be-4d36-8028-7be90cac9788";
		final String productType3Guid = "d31bc372-8ed9-4a80-9818-40a0783f58da";
		registry.addGuidDependency(ProductType.class, productType1Guid);
		registry.addGuidDependencies(ProductType.class, new TreeSet<>(Arrays.asList(productType2Guid, productType3Guid)));

		final String commonSkuOptionGuid = "95668a08-8acb-46ea-9a32-e21d0e8782be";
		final String skuOption1Guid = "4d64a198-5c75-4d03-a733-ee2dda395fa3";
		final String skuOption2Guid = "76999ff0-cd5f-46b1-be5e-f14b037109d9";
		final String skuOption3Guid = "65ba7560-045c-4243-8e9f-bfae861003d4";

		final String commonAttributeGuid = "f9723f17-57e6-4105-9e3e-edc63e87d44e";
		final String dependentCatalogProductAttributeGuid = "d4b33acc-2f91-4760-b8c7-ada23f402047";
		final String otherCatalogProductAttributeGuid = "4ff3fffb-1b20-4a1e-8402-bc5334fba802";
		final String globalProductAttributeGuid = "6e27797d-90f3-4fa5-ae71-e9d2a475a622";
		final String dependentCatalogSkuAttributeGuid = "aeb65e03-f94d-42c7-b774-6fe6d83cbadb";
		final String otherCatalogSkuAttributeGuid = "335bed00-424e-4e64-82c4-29dcf819b8d2";
		final String globalSkuAttributeGuid = "5a97d7e4-cb84-424c-85c1-9084d7e1e66f";

		final Catalog dependentCatalog = mock(Catalog.class, "dependentCatalog");
		final Catalog otherCatalog = mock(Catalog.class, "otherCatalog");

		SkuOption commonSkuOption = mockSkuOption(commonSkuOptionGuid);
		SkuOption skuOption1 = mockSkuOption(skuOption1Guid);
		SkuOption skuOption2 = mockSkuOption(skuOption2Guid);
		SkuOption skuOption3 = mockSkuOption(skuOption3Guid);

		final ProductType productType1 = mockProductType("productType-1", productType1Guid, dependentCatalog, commonSkuOption, skuOption1);
		final ProductType productType2 = mockProductType("productType-2", productType2Guid, otherCatalog, commonSkuOption, skuOption2);
		final ProductType productType3 = mockProductType("productType-3", productType3Guid, otherCatalog, commonSkuOption, skuOption3);

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(false);

		when(dependentCatalog.getUidPk()).thenReturn(CATALOG_UID);
		when(otherCatalog.getUidPk()).thenReturn(0L);

		Attribute commonAttribute = mockAttribute(commonAttributeGuid, false, dependentCatalog);
		Attribute dependentCatalogProductAttribute = mockAttribute(dependentCatalogProductAttributeGuid, false, dependentCatalog);
		Attribute otherCatalogProductAttribute = mockAttribute(otherCatalogProductAttributeGuid, false, otherCatalog);
		Attribute globalProductAttribute = mockAttribute(globalProductAttributeGuid, true, otherCatalog);
		Attribute dependentCatalogSkuAttribute = mockAttribute(dependentCatalogSkuAttributeGuid, false, dependentCatalog);
		Attribute otherCatalogSkuAttribute = mockAttribute(otherCatalogSkuAttributeGuid, false, otherCatalog);
		Attribute globalSkuAttribute = mockAttribute(globalSkuAttributeGuid, true, otherCatalog);

		setupMocksForProductAttributes(productType1, commonAttribute, dependentCatalogProductAttribute);
		setupMocksForProductAttributes(productType2, commonAttribute, otherCatalogProductAttribute);
		setupMocksForProductAttributes(productType3, commonAttribute, globalProductAttribute);
		setupMocksForSkuAttributes(productType1, commonAttribute, dependentCatalogSkuAttribute);
		setupMocksForSkuAttributes(productType2, commonAttribute, otherCatalogSkuAttribute);
		setupMocksForSkuAttributes(productType3, commonAttribute, globalSkuAttribute);

		List<ProductType> result = productTypeExporter.findDependentObjects(CATALOG_UID);
		assertThat(result)
				.as("Missing productType1")
				.contains(productType1);

		assertThat(result)
				.as("ProductType3 is not a part of this catalog")
				.doesNotContain(productType3);

		assertThat(result)
				.as("ProductType2 is not a part of this catalog")
				.doesNotContain(productType2);

		assertThat(result)
				.size()
				.as("Other ProductTypes returned?")
				.isEqualTo(1);

		Set<String> attributeDependencies = registry.getDependentGuids(Attribute.class);

		final int expectedAttributeSetSize = 5;
		assertThat(attributeDependencies)
				.as("Missing commonAttributeGuid")
				.contains(commonAttributeGuid);
		assertThat(attributeDependencies)
				.as("Missing dependentCatalogProductAttributeGuid")
				.contains(dependentCatalogProductAttributeGuid);
		assertThat(attributeDependencies)
				.as("OtherCatalogProductAttributeGuid is for a ProductType in another catalog")
				.doesNotContain(otherCatalogProductAttributeGuid);
		assertThat(attributeDependencies)
				.as("Missing globalProductAttributeGuid even though its for another catalog")
				.contains(globalProductAttributeGuid);
		assertThat(attributeDependencies)
				.as("Missing dependentCatalogSkuAttributeGuid")
				.contains(dependentCatalogSkuAttributeGuid);
		assertThat(attributeDependencies)
				.as("OtherCatalogSkuAttributeGuid is for a ProductType in another catalog")
				.doesNotContain(otherCatalogSkuAttributeGuid);
		assertThat(attributeDependencies)
				.as("Missing globalSkuAttributeGuid even though its for another catalog")
				.contains(globalSkuAttributeGuid);
		assertThat(attributeDependencies)
				.size()
				.as("Other attribute dependencies?")
				.isEqualTo(expectedAttributeSetSize);

		Set<String> skuOptionDependencies = registry.getDependentGuids(SkuOption.class);

		final int expectedSkuSetSize = 4;
		assertThat(skuOptionDependencies)
				.as("Missing commonSkuOptionGuid")
				.contains(commonSkuOptionGuid);
		assertThat(skuOptionDependencies)
				.as("Missing skuOption1Guid")
				.contains(skuOption1Guid);
		assertThat(skuOptionDependencies)
				.as("Missing skuOption2Guid")
				.contains(skuOption2Guid);
		assertThat(skuOptionDependencies)
				.as("Missing skuOption3Guid")
				.contains(skuOption3Guid);
		assertThat(skuOptionDependencies)
				.size()
				.as("Other sku option dependencies?")
				.isEqualTo(expectedSkuSetSize);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
	}
}
