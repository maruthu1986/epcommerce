/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.TaxTestPersister;

/**
 * An integration test for ProductLookupImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class ProductLookupImplITest extends DbTestCase {

	@Autowired
	@Qualifier("nonCachingProductLookup")
	private ProductLookup productLookup;

	@Autowired
	private ProductService productService;

	@Autowired
	private BeanFactory beanFactory;


	/**
	 * Test searching for products given the guid.
	 */
	@Test
	public void testFindByGuid() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product foundProduct = productLookup.findByGuid(product.getGuid());
		assertEquals("The found product should be the one we looked for", product, foundProduct);

		try {
			productLookup.findByGuid(null);
			fail("Trying to find a null guid should throw an exception");
		} catch (final EpServiceException expected) {
			// This exception should have been thrown
		}

		assertNull("Searching for an invalid guid should return null", productLookup.findByGuid("badGuid"));
	}

	@Test
	public void testFindByUid() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();
		final Product loadedProduct = productLookup.findByUid(productUid);

		assertEquals("The loaded product should match the product we asked for", product, loadedProduct);
		assertEquals("The Uid should match", productUid, loadedProduct.getUidPk());
	}

	@Test
	public void testFindByUids() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();
		final List<Product> loadedProducts = productLookup.findByUids(Collections.singletonList(productUid));

		assertEquals("The loaded product should match the product we asked for",
				Collections.singletonList(product), loadedProducts);
	}

	@Test
	public void testRecursiveLoadingOfProductBundles() {
		Product product = persisterFactory.getCatalogTestPersister().createMultiSkuProduct(scenario.getCatalog(), scenario.getCategory(),
				"brandCode", "productCode", "productType", "Product Name", TaxTestPersister.TAX_CODE_GOODS, AvailabilityCriteria.ALWAYS_AVAILABLE,
				1, "sku1", "sku2");
		product = productService.saveOrUpdate(product);

		ProductBundle innerBundle = persisterFactory.getCatalogTestPersister().createSimpleProductBundle(
				"productBundleType", "innerBundle", scenario.getCatalog(), scenario.getCategory(),
				persisterFactory.getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
		ProductSku innerBundleSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
		innerBundleSku.setProduct(innerBundle);
		innerBundleSku.setSkuCode("productBundle");
		BundleConstituent constituent = beanFactory.getBean(ContextIdNames.BUNDLE_CONSTITUENT);
		constituent.setConstituent(product);
		constituent.setQuantity(1);
		innerBundle.addConstituent(constituent);
		innerBundle = (ProductBundle) productService.saveOrUpdate(innerBundle);

		ProductBundle outerBundle = persisterFactory.getCatalogTestPersister().createSimpleProductBundle(
				"productBundleType", "outerBundle", scenario.getCatalog(), scenario.getCategory(),
				persisterFactory.getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
		ProductSku bundleSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
		bundleSku.setProduct(outerBundle);
		bundleSku.setSkuCode("outerBundle");
		BundleConstituent outerConstituent = beanFactory.getBean(ContextIdNames.BUNDLE_CONSTITUENT);
		outerConstituent.setConstituent(innerBundle.getDefaultSku());
		outerConstituent.setQuantity(1);
		outerBundle.addConstituent(outerConstituent);
		outerBundle = (ProductBundle) productService.saveOrUpdate(outerBundle);

		ProductBundle loadedBundle = productLookup.findByUid(outerBundle.getUidPk());
		assertEquals("Sanity Check", loadedBundle.getGuid(), outerBundle.getGuid());

		ProductBundle loadedConstituentBundle = (ProductBundle) loadedBundle.getConstituents().get(0).getConstituent().getProduct();
		Product loadedConstituentProduct = loadedConstituentBundle.getConstituents().get(0).getConstituent().getProduct();
		ProductSku lcpSku1 = loadedConstituentProduct.getSkuByCode("sku1");
		Product lcpSkuProduct = lcpSku1.getProduct();
		assertEquals("Bundle Constituent's Product->Sku->Product relationship should be fully loaded",
				product, lcpSkuProduct);

		ProductSku lcpSku2 = loadedConstituentProduct.getSkuByCode("sku2");
		Product lcp2SkuProduct = lcpSku2.getProduct();
		assertEquals("Bundle Constituent's Product->Sku2->Product relationship should be fully loaded",
				product, lcp2SkuProduct);
	}

}
