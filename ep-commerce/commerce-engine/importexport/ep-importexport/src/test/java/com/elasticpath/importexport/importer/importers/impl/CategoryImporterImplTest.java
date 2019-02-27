/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.category.CategoryAdapter;
import com.elasticpath.importexport.common.adapters.pricing.TestAdapterHelper;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Category importer test.
 */
public class CategoryImporterImplTest {

	private static final String CATEGORY_CODE = "category code";
	public static final String VIRTUAL_CATALOG_CODE = "virtualCatalog";
	public static final String LINKED_CATEGORY_GUID = "Linked Category GUID";
	private CategoryImporterImpl categoryImporter;

	private TestAdapterHelper helper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock private CategoryService mockCategoryService;
	@Mock private CategoryLookup mockCategoryLookup;
	@Mock private DomainAdapter<Category, LinkedCategoryDTO> mockLinkedCategoryAdapter;

	private CategoryDTO categoryDTO;

	private Category category;

	private CachingService mockCachingService;

	@Before
	public void setUp() throws Exception {
		helper = new TestAdapterHelper();
		categoryDTO = new CategoryDTO();
		categoryDTO.setCategoryCode(CATEGORY_CODE);
		categoryDTO.setCatalogCode("catalog");
		category = helper.createCategory(CATEGORY_CODE);
		category.setUidPk(1L);

		categoryImporter = new CategoryImporterImpl();
		categoryImporter.setCategoryLookup(mockCategoryLookup);
		categoryImporter.setCategoryService(mockCategoryService);
		categoryImporter.setCategoryAdapter(new MockCategoryAdapter());
		categoryImporter.setLinkedCategoryAdapter(mockLinkedCategoryAdapter);
		categoryImporter.setStatusHolder(new ImportStatusHolder());

		mockCachingService = context.mock(CachingService.class);
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findCategoryByCode(CATEGORY_CODE, "catalog");
				will(returnValue(category));
			}
		});
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteNonInitializedImport() {
		categoryImporter.executeImport(categoryDTO);
	}

	/**
	 * Check an import of one category.
	 */
	@Test
	public void testExecuteImport() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());
		SavingStrategy<Category, CategoryDTO> importStrategy = AbstractSavingStrategy.createStrategy(
				ImportStrategyType.INSERT_OR_UPDATE, null);

		categoryImporter.setCachingService(mockCachingService);
		categoryImporter.initialize(new ImportContext(importConfiguration), importStrategy);

		context.checking(new Expectations() {
			{
				oneOf(mockCategoryLookup).findByUid(1L);
				will(returnValue(category));
				oneOf(mockCategoryLookup).findByCategoryAndCatalogCode(CATEGORY_CODE, VIRTUAL_CATALOG_CODE);
				will(returnValue(null));
				allowing(mockCategoryLookup).findParent(category);
				will(returnValue(null));

				oneOf(mockCategoryService).saveOrUpdate(category);
				will(returnValue(category));
			}
		});

		List<LinkedCategoryDTO> linkedCategoryDTOList = new ArrayList<>();
		final LinkedCategoryDTO linkedCategoryDTO = new LinkedCategoryDTO();
		linkedCategoryDTO.setGuid(LINKED_CATEGORY_GUID);
		linkedCategoryDTO.setOrder(1);
		linkedCategoryDTO.setVirtualCatalogCode(VIRTUAL_CATALOG_CODE);
		linkedCategoryDTOList.add(linkedCategoryDTO);
		categoryDTO.setLinkedCategoryDTOList(linkedCategoryDTOList);

		final Catalog catalog = new CatalogImpl();
		catalog.setUidPk(1L);
		final LinkedCategoryImpl linkedCategory = new LinkedCategoryImpl();
		linkedCategory.setGuid(LINKED_CATEGORY_GUID);
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findCatalogByCode(VIRTUAL_CATALOG_CODE);
				will(returnValue(catalog));

				oneOf(mockLinkedCategoryAdapter).createDomainObject();
				will(returnValue(linkedCategory));

				oneOf(mockLinkedCategoryAdapter).buildDomain(linkedCategoryDTO, linkedCategory);
				will(returnValue(linkedCategory));

				oneOf(mockCategoryService).saveOrUpdate(linkedCategory);
				will(returnValue(linkedCategory));
			}
		});

		categoryImporter.executeImport(categoryDTO);
		assertEquals("category", categoryImporter.getImportedObjectName());
		assertEquals("linked category should have the master category set", category, linkedCategory.getMasterCategory());
		assertNotNull(categoryImporter.getCategoryService());
		assertNotNull(categoryImporter.getSavingStrategy());
	}


	@Test
	public void testImportSetsLinkedCategoryParent() {
		final Category parent = helper.createCategory("parent");
		parent.initialize();
		parent.setUidPk(2L);

		final Category linkedParent = new LinkedCategoryImpl();
		linkedParent.initialize();
		linkedParent.setUidPk(parent.getUidPk() + 1);
		linkedParent.setMasterCategory(parent);

		categoryDTO.setParentCategoryCode(parent.getCode());

		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());
		SavingStrategy<Category, CategoryDTO> importStrategy = AbstractSavingStrategy.createStrategy(
				ImportStrategyType.INSERT_OR_UPDATE, null);

		categoryImporter.setCachingService(mockCachingService);
		categoryImporter.initialize(new ImportContext(importConfiguration), importStrategy);

		context.checking(new Expectations() {
			{
				oneOf(mockCategoryLookup).findByUid(1L);
				will(returnValue(category));
				oneOf(mockCategoryLookup).findByCategoryAndCatalogCode(CATEGORY_CODE, VIRTUAL_CATALOG_CODE);
				will(returnValue(null));
				allowing(mockCategoryLookup).findParent(category);
				will(returnValue(parent));

				oneOf(mockCategoryService).saveOrUpdate(category);
				will(returnValue(category));
			}
		});

		List<LinkedCategoryDTO> linkedCategoryDTOList = new ArrayList<>();
		final LinkedCategoryDTO linkedCategoryDTO = new LinkedCategoryDTO();
		linkedCategoryDTO.setGuid(LINKED_CATEGORY_GUID);
		linkedCategoryDTO.setOrder(1);
		linkedCategoryDTO.setVirtualCatalogCode(VIRTUAL_CATALOG_CODE);
		linkedCategoryDTOList.add(linkedCategoryDTO);
		categoryDTO.setLinkedCategoryDTOList(linkedCategoryDTOList);

		final Catalog catalog = new CatalogImpl();
		catalog.setUidPk(1L);
		catalog.setCode(VIRTUAL_CATALOG_CODE);

		final LinkedCategoryImpl linkedCategory = new LinkedCategoryImpl();
		linkedCategory.setGuid(LINKED_CATEGORY_GUID);
		linkedCategory.setCatalog(catalog);
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findCatalogByCode(VIRTUAL_CATALOG_CODE);
				will(returnValue(catalog));

				oneOf(mockCachingService).findCategoryByCode(parent.getCode(), VIRTUAL_CATALOG_CODE);
				will(returnValue(linkedParent));

				oneOf(mockLinkedCategoryAdapter).createDomainObject();
				will(returnValue(linkedCategory));

				oneOf(mockLinkedCategoryAdapter).buildDomain(linkedCategoryDTO, linkedCategory);
				will(returnValue(linkedCategory));

				oneOf(mockCategoryService).saveOrUpdate(linkedCategory);
				will(returnValue(linkedCategory));
			}
		});

		categoryImporter.executeImport(categoryDTO);
		assertEquals("linked category should have the master category set", category, linkedCategory.getMasterCategory());
		assertEquals("linked category should have its parent category set", linkedParent.getGuid(), linkedCategory.getParentGuid());
	}

	/**
	 * Test clear collection strategy for category attributes.
	 */
	@Test
	public void testCollectionsStrategy() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());
		SavingStrategy<Category, CategoryDTO> importStrategy = AbstractSavingStrategy.createStrategy(
				ImportStrategyType.INSERT_OR_UPDATE, null);

		categoryImporter.initialize(new ImportContext(importConfiguration), importStrategy);

		CollectionsStrategy<Category, CategoryDTO> collectionsStrategy = categoryImporter.getCollectionsStrategy();
		assertTrue(collectionsStrategy.isForPersistentObjectsOnly());

		Category newCategory = new CategoryImpl();
		Map<String, AttributeValue> attributeValueMap = new HashMap<>();
		attributeValueMap.put("attr_key", new CategoryAttributeValueImpl());
		newCategory.setAttributeValueMap(attributeValueMap);

		assertEquals(newCategory.getAttributeValueMap().size(), 1);
		collectionsStrategy.prepareCollections(newCategory, new CategoryDTO());
		assertTrue(newCategory.getAttributeValueMap().isEmpty());
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", CategoryDTO.class, categoryImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(categoryImporter.getAuxiliaryJaxbClasses());
	}

	/**
	 * Mock category adapter.
	 */
	private class MockCategoryAdapter extends CategoryAdapter {

		@Override
		public void populateDomain(final CategoryDTO categoryDTO, final Category category) {
			// do nothing
		}

		@Override
		public void populateDTO(final Category category, final CategoryDTO categoryDTO) {
			// do nothing
		}

		@Override
		public Category createDomainObject() {
			return new CategoryImpl();
		}
	}
}
