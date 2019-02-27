/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.TaxTestPersister;

/**
 * An integration test for ProductSkuLookupImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class ProductSkuLookupImplITest extends DbTestCase {

	@Autowired
	@Qualifier("nonCachingProductSkuLookup")
	private ProductSkuLookup productSkuLookup;

	@Autowired
	private ProductService productService;

	@Autowired
	private BeanFactory beanFactory;

	/**
	 * Test searching for a sku given the sku code.
	 */
	@Test
	public void testFindBySkuCode() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final ProductSku found = productSkuLookup.findBySkuCode(product.getDefaultSku().getSkuCode());
		assertEquals("The found sku should be the one we looked for", product.getDefaultSku(), found);

		assertNull("Searching for an invalid sku code should return null", productSkuLookup.findBySkuCode("bad-code"));
	}

	/**
	 * Test searching for skus given sku codes.
	 */
	@Test
	public void testFindBySkuCodes() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final List<ProductSku> found = productSkuLookup.findBySkuCodes(Collections.singleton(product.getDefaultSku().getSkuCode()));

		assertEquals("The found sku should be the one we looked for",
				Collections.singletonList(product.getDefaultSku()),
				found);
	}

	/**
	 * Test searching for a sku given the guid.
	 */
	@Test
	public void testFindByGuidHappyPath() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final ProductSku found = productSkuLookup.findByGuid(product.getDefaultSku().getGuid());
		assertEquals("The found sku should be the one we looked for", product.getDefaultSku(), found);
	}

	@Test
	public void testFindByUid() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long skuUid = product.getDefaultSku().getUidPk();
		final ProductSku loaded = productSkuLookup.findByUid(skuUid);

		assertEquals("The loaded sku should match the sku we asked for", product.getDefaultSku(), loaded);
		assertEquals("The Uid should match", skuUid, loaded.getUidPk());
	}

	@Test
	public void testFindByUids() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long skuUid = product.getDefaultSku().getUidPk();
		final List<ProductSku> loaded = productSkuLookup.findByUids(Collections.singletonList(skuUid));

		assertEquals("The loaded skus should match the skus we asked for",
				Collections.singletonList(product.getDefaultSku()), loaded);
	}

	@Test
	public void testRecursiveLoadingOfProductBundles() {
		Product product = persisterFactory.getCatalogTestPersister().createMultiSkuProduct(scenario.getCatalog(), scenario.getCategory(),
				"brandCode", "productCode", "productType", "Product Name", TaxTestPersister.TAX_CODE_GOODS, AvailabilityCriteria.ALWAYS_AVAILABLE,
				1, "sku1", "sku2");
		product = productService.saveOrUpdate(product);

		ProductBundle bundle = persisterFactory.getCatalogTestPersister().createSimpleProductBundle(
				"productBundleType", "productBundle", scenario.getCatalog(), scenario.getCategory(),
				persisterFactory.getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
		ProductSku bundleSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
		bundleSku.setProduct(bundle);
		bundleSku.setSkuCode("productBundle");
		BundleConstituent constituent = beanFactory.getBean(ContextIdNames.BUNDLE_CONSTITUENT);
		constituent.setConstituent(product);
		constituent.setQuantity(1);
		bundle.addConstituent(constituent);

		productService.saveOrUpdate(bundle);

		ProductSku loadedBundleSku = productSkuLookup.findBySkuCode("productBundle");
		assertEquals("Sanity Check", loadedBundleSku.getGuid(), bundleSku.getGuid());

		ProductBundle loadedBundle = (ProductBundle) loadedBundleSku.getProduct();
		Product loadedConstituentProduct = loadedBundle.getConstituents().get(0).getConstituent().getProduct();
		ProductSku lcpSku = loadedConstituentProduct.getDefaultSku();
		Product lcpSkuProduct = lcpSku.getProduct();

		assertEquals("Bundle Constituent's Product->Sku->Product relationship should be fully loaded",
				product, lcpSkuProduct);
	}

	@Test
	public void testIsProductSkuExists() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
			scenario.getCategory(), scenario.getWarehouse());
		final Boolean result = productSkuLookup.isProductSkuExist(product.getDefaultSku().getSkuCode());

		assertTrue("Product sku must exist", result);
	}
}
