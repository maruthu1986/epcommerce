/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.db.DbTestCase;

/**
 * Integration tests for {@link ProductService} which require a clean database.
 */
@DirtiesDatabase
public class IsolatedProductServiceImplTest extends DbTestCase {
	@Autowired
	@Qualifier("productService")
	/** The main object under test. */
	private ProductService service;

	/**
	 * Testing finding a top seller product.
	 */
	@Test
	public void testFindProductTopSeller() {
		Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		product1.setSalesCount(10);
		product2.setSalesCount(1);
		product1 = service.saveOrUpdate(product1);
		product2 = service.saveOrUpdate(product2);

		List<Product> topSellers = service.findProductTopSeller(1, null);
		assertEquals("We should only receive one as that is how many we requested", 1, topSellers.size());
		assertEquals("The top seller should be the product we expected", product1, topSellers.get(0));

		topSellers = service.findProductTopSeller(2, null);
		assertEquals("We should receive two as that is how many we requested", 2, topSellers.size());
		assertEquals("The first top seller should be the first product", product1, topSellers.get(0));
		assertEquals("The second top seller should be the first product", product2, topSellers.get(1));
		assertFalse("The third product should not be in the list", topSellers.contains(product3));

	}
}
