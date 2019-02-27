/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.catalogview;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

public class StoreProductServiceImplTest extends BasicSpringContextTest {

	private SimpleStoreScenario scenario;
	private CatalogTestPersister catalogPersister;

	@Autowired
	private StoreService storeService;

	@Autowired
	private StoreProductService storeProductService;

	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		catalogPersister = getTac().getPersistersFactory().getCatalogTestPersister();
	}

	@Test
	@DirtiesDatabase
	public void ensureBundleIsIndexedWhenConstituentsAreInStockAndStoreDoesNotDisplayOutOfStock() {
		setStoreDisplayOutOfStock(false);

		Product product = generateProductWithInventory();
		ProductBundle bundle = generateProductBundle();
		addProductConstituentToBundle(bundle, product);

		Store store = scenario.getStore();
		IndexProduct indexProduct = storeProductService.createIndexProduct(bundle, Collections.singleton(store));

		assertTrue("The bundle should be available for purchase", indexProduct.isAvailable(store.getCode()));
		assertTrue("The bundle should be displayable", indexProduct.isDisplayable(store.getCode()));
	}

	@Test
	@DirtiesDatabase
	public void ensureBundleIsNotIndexedWhenConstituentsAreOutOfStockAndStoreDoesNotDisplayOutOfStock() {
		setStoreDisplayOutOfStock(false);

		Product product = generateProductWithoutInventory();
		ProductBundle bundle = generateProductBundle();
		addProductConstituentToBundle(bundle, product);

		Store store = scenario.getStore();
		IndexProduct indexProduct = storeProductService.createIndexProduct(bundle, Collections.singleton(store));

		assertFalse("The bundle should not be available for purchase", indexProduct.isAvailable(store.getCode()));
		assertFalse("The bundle should not be displayable", indexProduct.isDisplayable(store.getCode()));
	}

	@Test
	@DirtiesDatabase
	public void ensureBundleIsIndexedWhenConstituentsAreOutOfStockAndStoreDisplaysOutOfStock() {
		setStoreDisplayOutOfStock(true);

		Product product = generateProductWithoutInventory();
		ProductBundle bundle = generateProductBundle();
		addProductConstituentToBundle(bundle, product);

		Store store = scenario.getStore();
		IndexProduct indexProduct = storeProductService.createIndexProduct(bundle, Collections.singleton(store));

		assertFalse("The bundle should not be available for purchase", indexProduct.isAvailable(store.getCode()));
		assertTrue("The bundle should be displayable", indexProduct.isDisplayable(store.getCode()));
	}

	private ProductBundle generateProductBundle() {
		ProductBundle bundle = catalogPersister.createSimpleProductBundle("productType", "bundle", scenario.getCatalog(),
				scenario.getCategory(), getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
		catalogPersister.persistSimpleProductSku("bundle_sku", 10.00, "CAD", false, bundle, scenario.getWarehouse());
		return bundle;
	}

	private Product generateProductWithInventory() {
		Product product = generateProductWithoutInventory();
		catalogPersister.persistInventory(product.getDefaultSku().getSkuCode(), scenario.getWarehouse(), 10, 0, 0, "Add inventory to product.");
		return product;
	}

	private Product generateProductWithoutInventory() {
		return catalogPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
				new BigDecimal(10.00), TestDataPersisterFactory.DEFAULT_CURRENCY, "brand", "productCode", "product", "skuCode",
				TaxTestPersister.TAX_CODE_GOODS,
				AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, 1);
	}

	private void addProductConstituentToBundle(ProductBundle bundle, Product constituent) {
		bundle.addConstituent(catalogPersister.createSimpleBundleConstituent(constituent, 1));
	}

	private void setStoreDisplayOutOfStock(boolean displayOutOfStock) {
		Store store = scenario.getStore();
		store.setDisplayOutOfStock(displayOutOfStock);
		storeService.saveOrUpdate(store);
	}
}
