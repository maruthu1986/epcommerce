/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.util.CategoryGuidUtil;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.util.Utils;

public class CategoryLookupImplITest extends BasicSpringContextTest {
	@Autowired
	@Qualifier("categoryService")
	private CategoryService categoryService;

	@Autowired
	@Qualifier("categoryLookup")
	private CategoryLookup categoryLookup;

	@Autowired
	private CategoryGuidUtil categoryGuidUtil;

	private Catalog masterCatalog, virtualCatalog;
	private CategoryType categoryType;
	private TestDataPersisterFactory persisterFactory;
	private String parentCode, child1Code, child2Code;

	@Before
	public void setUp() throws Exception {
		persisterFactory = getTac().getPersistersFactory();

		masterCatalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), true);
		categoryType = createUniqueCategoryType(masterCatalog, "catType", "catTypeTemplate");
		virtualCatalog = persisterFactory.getCatalogTestPersister().persistCatalog(Utils.uniqueCode("Canada"), false);

		parentCode = Utils.uniqueCode("parent");
		child1Code = Utils.uniqueCode("child1");
		child2Code = Utils.uniqueCode("child2");
	}

	@Test
	public void testFindChildrenByParentWithMasterCategories() {
		Category parent = persisterFactory.getCatalogTestPersister().persistCategory(
				parentCode, masterCatalog, categoryType, "Parent", Locale.CANADA.toString());
		Category child1 = persisterFactory.getCatalogTestPersister().persistCategory(
				child1Code, masterCatalog, categoryType, parent);
		Category child2 = persisterFactory.getCatalogTestPersister().createMasterCategory(
				child2Code, masterCatalog, categoryType, parent);
		child2.setOrdering(1);
		child2 = categoryService.saveOrUpdate(child2);

		List<Category> children = categoryLookup.findChildren(parent);
		assertEquals("Parent should have two child categories", Arrays.asList(child1, child2), children);
	}

	@Test
	public void testFindChildrenByParentWithLinkedCategories() {
		Category parent = persisterFactory.getCatalogTestPersister().persistCategory(
				parentCode, masterCatalog, categoryType, "Parent", Locale.CANADA.toString());
		Category child1 = persisterFactory.getCatalogTestPersister().persistCategory(
				child1Code, masterCatalog, categoryType, parent);
		Category child2 = persisterFactory.getCatalogTestPersister().createMasterCategory(
				child2Code, masterCatalog, categoryType, parent);
		child2.setOrdering(1);
		child2 = categoryService.saveOrUpdate(child2);
		Category linkedParent = categoryService.addLinkedCategory(parent.getUidPk(), -1, virtualCatalog.getUidPk());

		List<Category> children = categoryLookup.findChildren(linkedParent);
		assertEquals("Parent should have two child categories", 2, children.size());
		assertEquals("Should have linked category #1",
				categoryGuidUtil.get(child1.getCode(), virtualCatalog.getCode()), children.get(0).getCompoundGuid());
		assertEquals("Should have linked category #2",
				categoryGuidUtil.get(child2.getCode(), virtualCatalog.getCode()), children.get(1).getCompoundGuid());
	}

	private CategoryType createUniqueCategoryType(final Catalog catalog, final String name, final String template) {
		return persisterFactory.getCatalogTestPersister().persistCategoryType(
				Utils.uniqueCode(name), catalog);
	}
}
