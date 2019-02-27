/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for categories.
 */
public class ImportCategoriesTest extends ImportJobTestCase {

	@Autowired
	private Utility utility;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	@Qualifier("categoryLookup")
	private CategoryLookup categoryLookup;

	/**
	 * Test import categories insert.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCategoriesInsert() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());

		Category category = categoryLookup.findByCategoryCodeAndCatalog("100001", scenario.getCatalog());
		assertNotNull("Category should have been found", category);

		Locale locale = Locale.ENGLISH;
		LocaleDependantFields localeDependantFields = category.getLocaleDependantFields(locale);
		assertEquals("100001", category.getCode());
		assertEquals(null, category.getParentGuid());
		assertEquals("Office Desks", localeDependantFields.getDisplayName());
		assertEquals(string2Date("Thu Mar 1 12:00:00 2007", locale), category.getStartDate());
		assertEquals(string2Date("Sat Mar 1 12:00:00 2008", locale), category.getEndDate());
		assertFalse(category.isHidden());
		assertEquals(1, category.getOrdering());
		assertEquals(null, localeDependantFields.getUrl());
		assertEquals("Office Desks", localeDependantFields.getTitle());
		assertEquals("Office desks for your office.", localeDependantFields.getKeyWords());
		assertEquals(null, localeDependantFields.getDescription());
		// assertEquals(null, category.getAttributeValueMap().get("catImage"));
		// assertEquals("This is a category for office desks.", category.getAttributeValueMap().get("catDescription_en").getStringValue());
	}

	/**
	 * Test import categories insert/update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCategoriesInsertUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertUpdateCategoriesImportJob());

		Category category1 = categoryLookup.findByCategoryCodeAndCatalog("100001", scenario.getCatalog());

		// assert existing category was not changed during update
		Locale locale = Locale.ENGLISH;
		LocaleDependantFields localeDependantFields1 = category1.getLocaleDependantFields(locale);
		assertEquals("100001", category1.getCode());
		assertEquals(null, category1.getParentGuid());
		assertEquals("Office Desks", localeDependantFields1.getDisplayName());
		assertEquals(string2Date("Thu Mar 1 12:00:00 2007", locale), category1.getStartDate());
		assertEquals(string2Date("Sat Mar 1 12:00:00 2008", locale), category1.getEndDate());
		assertFalse(category1.isHidden());
		assertEquals(1, category1.getOrdering());
		assertEquals(null, localeDependantFields1.getUrl());
		assertEquals("Office Desks", localeDependantFields1.getTitle());
		assertEquals("Office desks for your office.", localeDependantFields1.getKeyWords());
		assertEquals(null, localeDependantFields1.getDescription());
		// assertEquals(null, category1.getAttributeValueMap().get("catImage"));
		// assertEquals("This is a category for office desks.", category1.getAttributeValueMap().get("catDescription_en").getStringValue());

		// assert existing category has been updated during import
		Category category2 = categoryLookup.findByCategoryCodeAndCatalog("100002", scenario.getCatalog());
		LocaleDependantFields localeDependantFields2 = category2.getLocaleDependantFields(locale);
		assertEquals("100002", category2.getCode());
		assertEquals(null, category2.getParentGuid());
		assertEquals("Contemporary Dining Tables", localeDependantFields2.getDisplayName());
		assertEquals("Contemporary Dining Tables", localeDependantFields2.getTitle());
		assertEquals("Contemporary Dining tables for your dining room.", localeDependantFields2.getKeyWords());
		assertEquals(null, localeDependantFields2.getDescription());
		// assertEquals("This is a category for contemporary dining tables.", category2.getAttributeValueMap().get("catDescription_en")
		// .getStringValue());

		// assert new category has been created during import
		Category category3 = categoryLookup.findByCategoryCodeAndCatalog("100004", scenario.getCatalog());
		LocaleDependantFields localeDependantFields3 = category3.getLocaleDependantFields(locale);
		assertEquals("100004", category3.getCode());
		assertEquals(null, category3.getParentGuid());
		assertEquals("Dining Chairs", localeDependantFields3.getDisplayName());
		assertTrue(category3.isHidden());
		assertEquals(3, category3.getOrdering());
		assertEquals(null, localeDependantFields3.getUrl());
		assertEquals("Dining Chairs", localeDependantFields3.getTitle());
		assertEquals("Dining chairs for your dining room.", localeDependantFields3.getKeyWords());
		assertEquals(null, localeDependantFields3.getDescription());
		// assertEquals(null, category3.getAttributeValueMap().get("catImage"));
		// assertEquals("This is a category for dining chairs.", category3.getAttributeValueMap().get("catDescription_en").getStringValue());
	}

	/**
	 * Test import categories update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCategoriesUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createUpdateCategoriesImportJob());

		Category category = categoryLookup.findByCategoryCodeAndCatalog("100001", scenario.getCatalog());
		assertNotNull("Category should have been found", category);

		Locale locale = Locale.ENGLISH;
		LocaleDependantFields localeDependantFields = category.getLocaleDependantFields(locale);
		assertEquals("100001", category.getCode());
		assertEquals(null, category.getParentGuid());
		assertEquals("Large Office Desks", localeDependantFields.getDisplayName());
		assertEquals(string2Date("Thu Mar 1 12:00:00 2007", locale), category.getStartDate());
		assertEquals(string2Date("Sat Mar 1 12:00:00 2008", locale), category.getEndDate());
		assertFalse(category.isHidden());
		assertEquals(1, category.getOrdering());
		assertEquals(null, localeDependantFields.getUrl());
		assertEquals("Office Desks", localeDependantFields.getTitle());
		assertEquals("Office desks for your office.", localeDependantFields.getKeyWords());
		assertEquals(null, localeDependantFields.getDescription());
		// assertEquals(null, category.getAttributeValueMap().get("catImage"));
		// assertEquals("This is a category for large office desks.", category.getAttributeValueMap().get("catDescription_en").getStringValue());
	}

	/**
	 * Test input categories delete.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCategoriesDelete() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createDeleteCategoriesImportJob());

		assertNotNull(categoryLookup.findByCategoryCodeAndCatalog("100001", scenario.getCatalog()));
		assertNull(categoryLookup.findByCategoryCodeAndCatalog("100002", scenario.getCatalog()));
		assertNotNull(categoryLookup.findByCategoryCodeAndCatalog("100003", scenario.getCatalog()));
	}
}
