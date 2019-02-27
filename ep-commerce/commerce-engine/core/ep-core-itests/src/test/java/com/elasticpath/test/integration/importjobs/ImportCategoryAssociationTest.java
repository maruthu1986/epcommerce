/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for CategoryAssociation.
 */
public class ImportCategoryAssociationTest extends ImportJobTestCase {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CategoryLookup categoryLookup;
	@Autowired
	private ProductLookup productLookup;

	/**
	 * Test import CategoryAssociation insert/update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCategoryAssociationInsertUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertProductImportJob());

		/** execute category association import job. */
		executeImportJob(createInsertUpdateCategoryAssociationImportJob());

		final Category category100001 = categoryLookup.findByCategoryCodeAndCatalog("100001", scenario.getCatalog());
		final Category category100002 = categoryLookup.findByCategoryCodeAndCatalog("100002", scenario.getCatalog());
		final Category category100003 = categoryLookup.findByCategoryCodeAndCatalog("100003", scenario.getCatalog());

		final Product product1 = productLookup.findByGuid("102");
		assertNotNull("Product should have been found", product1);

		final Set<Category> allCategories1 = product1.getCategories();

		assertEquals(3, allCategories1.size());

		assertTrue(allCategories1.remove(category100001)); // default category
		assertTrue(allCategories1.remove(category100002));
		assertTrue(allCategories1.remove(category100003));

		final Product product2 = productLookup.findByGuid("103");

		final Set<Category> allCategories2 = product2.getCategories();

		assertEquals(2, allCategories2.size());

		assertTrue(allCategories2.remove(category100001)); // default category
		assertTrue(allCategories2.remove(category100003));
	}
}
