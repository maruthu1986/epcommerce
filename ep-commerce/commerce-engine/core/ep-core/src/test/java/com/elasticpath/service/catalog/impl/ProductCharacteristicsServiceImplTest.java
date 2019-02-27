/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test that {@link ProductCharacteristicsServiceImpl} behaves as expected.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports" })
public class ProductCharacteristicsServiceImplTest {

	private static final String SKU_CODE = "sku001";
	private static final Long PRODUCT_UID = 1L;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactoryExpectationsFactory expectationsFactory;
	private BeanFactory beanFactory;

	private final ProductCharacteristicsServiceImpl productCharacteristicsService = new ProductCharacteristicsServiceImpl();
	private ProductBundleService productBundleService;
	private ProductTypeService productTypeService;
	private ProductLookup productLookup;

	/**
	 * Sets up the test case.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);

		productBundleService = context.mock(ProductBundleService.class);
		productLookup = context.mock(ProductLookup.class);
		productTypeService = context.mock(ProductTypeService.class);

		productCharacteristicsService.setProductBundleService(productBundleService);
		productCharacteristicsService.setProductLookup(productLookup);
		productCharacteristicsService.setProductTypeService(productTypeService);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Should return false when non-bundle product is provided.
	 */
	@Test
	public void testIsDynamicBundleReturnsFalseWhenWrappedProductIsNotBundle() {
		Product product = createSingleSkuProduct();
		StoreProduct storeProduct = new StoreProductImpl(product);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(storeProduct);

		assertFalse("A normal product is not a dynamic bundle", productCharacteristics.isDynamicBundle());
	}

	/**
	 * isDynamicBundle() should return true when bundle itself is a dynamic bundle.
	 */
	@Test
	public void testIsDynamicBundleReturnsTrueWhenBundleItselfIsDynamic() {
		StoreProduct storeProduct = new StoreProductImpl(createDynamicBundle());
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(storeProduct);

		assertTrue("A dynamic bundle should be recognized as such", productCharacteristics.isDynamicBundle());
	}

	/**
	 * isDynamicBundle() should return true when bundle itself has a dynamic bundle in any level.
	 */
	@Test
	public void testIsDynamicBundleReturnsTrueWhenBundleHasDynamicBundleInAnyLevel() {
		ProductBundle bundle = createNestedDynamicBundle();
		StoreProduct storeProduct = new StoreProductImpl(bundle);
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(storeProduct);

		assertTrue("A nested dynamic bundle should be recognized as dynamic", productCharacteristics.isDynamicBundle());
	}
	/**
	 * Ensures a dynamic bundle is marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionReturnsTrueForDynamicBundle() {
		ProductBundle bundle = createDynamicBundle();

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);
		ProductCharacteristics storeProductCharacteristics = productCharacteristicsService.getProductCharacteristics(new StoreProductImpl(bundle));


		assertTrue("A dynamic bundle requires selection.", productCharacteristics.offerRequiresSelection());
		assertTrue("A store product of a dynamic bundle requires selection", storeProductCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a nested dynamic bundle is marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionReturnsTrueForNestedDynamicBundle() {
		ProductBundle bundle = createNestedDynamicBundle();

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);
		ProductCharacteristics storeProductCharacteristics = productCharacteristicsService.getProductCharacteristics(new StoreProductImpl(bundle));

		assertTrue("A bundle containing a nested dynamic bundle requires selection.", productCharacteristics.offerRequiresSelection());
		assertTrue("A store product of a bundle containing a nested dynamic bundle requires selection",
				storeProductCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a single sku product is not marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionReturnsFalseForSingleSkuProduct() {
		Product product = createSingleSkuProduct();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(product);

		assertFalse("a single sku product requires no selection.", productCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a multi sku product is marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionReturnsTrueForMultiSkuProduct() {
		Product product = createMultiSkuProduct();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(product);

		assertTrue("a multi-sku product requires selection.", productCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a fixed bundle with no multi sku is not marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionReturnsFalseForFixedBundle() {
		ProductBundle bundle = createBundle();
		BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(createSingleSkuProduct());
		bundle.addConstituent(constituent);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertFalse("a fixed bundle with no multi sku does not require selection.", productCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a fixed bundle with a multi sku is marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionReturnsTrueForFixedBundleMultiSku() {
		ProductBundle bundle = createBundleWithMultiSkuAndGivenConstituents(createSingleSkuProduct());
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertTrue("a fixed bundle with a multi sku constituent requires selection.", productCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a fixed bundle with a nested bundle with a multi sku is marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionReturnsTrueForMultiSkuNestedBundle() {
		Product singleSkuProduct = createSingleSkuProduct();
		ProductBundle nestedBundle = createBundleWithMultiSkuAndGivenConstituents(singleSkuProduct);

		ProductBundle bundle = createBundle();
		BundleConstituent constituent3 = new BundleConstituentImpl();
		constituent3.setConstituent(singleSkuProduct);
		bundle.addConstituent(constituent3);

		BundleConstituent constituent4 = new BundleConstituentImpl();
		constituent4.setConstituent(nestedBundle);
		bundle.addConstituent(constituent4);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertTrue("a fixed bundle with single- and multi-sku constituents requires selection.",
				productCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a fixed bundle with a nested dynamic bundle is marked as "offer requires selection".
	 */
	@Test
	public void offerRequiresSelectionForDynamicNestedBundle() {
		ProductBundle bundle = createBundle();
		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(createSingleSkuProduct());
		bundle.addConstituent(constituent1);

		ProductBundle nestedBundle = createNestedDynamicBundle();
		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(nestedBundle);
		bundle.addConstituent(constituent2);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertTrue("a fixed bundle with a nested dynamic bundle requires selection.", productCharacteristics.offerRequiresSelection());
	}

	private ProductBundle createBundle() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setUidPk(PRODUCT_UID);
		bundle.setCalculated(false);
		bundle.setCode("BUNDLE");
		return bundle;
	}

	private ProductBundle createCalculatedBundle() {
		ProductBundle bundle = createBundle();
		bundle.setCalculated(true);
		return bundle;
	}

	/**
	 * Ensures a fixed bundle with nested, non-dynamic bundles and no multi-sku constituents does not require selection.
	 */
	@Test
	public void testOfferRequiresSelectionNestedFixedBundle() {
		Product singleSkuProduct = createSingleSkuProduct();
		ProductBundle nestedBundle = createBundle();
		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(singleSkuProduct);
		nestedBundle.addConstituent(constituent1);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(singleSkuProduct);
		nestedBundle.addConstituent(constituent2);

		ProductBundle bundle = createBundle();
		BundleConstituent constituent3 = new BundleConstituentImpl();
		constituent3.setConstituent(singleSkuProduct);
		bundle.addConstituent(constituent3);

		BundleConstituent constituent4 = new BundleConstituentImpl();
		constituent4.setConstituent(nestedBundle);
		bundle.addConstituent(constituent4);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertFalse("a fixed bundle with nested fixed bundles and only single-sku constituents does not require selection",
				productCharacteristics.offerRequiresSelection());
	}

	/**
	 * Ensures a calculated bundle is marked as calculated.
	 */
	@Test
	public void testCalculatedBundle() {
		ProductBundle bundle = createCalculatedBundle();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);
		assertTrue("a calculated bundle should be identified as such", productCharacteristics.isCalculatedBundle());
	}

	/**
	 * Test the behavior of get characteristics for product sku.
	 */
	@Test
	public void testGetCharacteristicsForProductSku() {
		Product singleSkuProduct = createSingleSkuProduct();
		ProductSku productSku = new ProductSkuImpl();
		productSku.setProduct(singleSkuProduct);
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(productSku);

		assertFalse("Single sku product does not have multiple skus",
				productCharacteristicsService.hasMultipleSkus(productSku.getProduct()));
		assertFalse("Basic product is not a bundle", productCharacteristics.isBundle());
		assertNull("The bundle uid should be null", productCharacteristics.getBundleUid());
	}

	/**
	 * Test the behavior of get characteristics for sku code with a single sku product.
	 */
	@Test
	public void testGetCharacteristicsForSkuCodeWithSingleSkuProduct() {
		final ProductType productType = new ProductTypeImpl();
		productType.setMultiSku(false);
		context.checking(new Expectations() {
			{
				oneOf(productBundleService).findBundleCodeBySkuCode(SKU_CODE); will(returnValue(null));
				oneOf(productTypeService).findBySkuCode(SKU_CODE); will(returnValue(productType));
			}
		});

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristicsForSkuCode(SKU_CODE);

		assertFalse("Basic product is not a bundle", productCharacteristics.isBundle());
		assertFalse("Single sku product has only 1 sku", productCharacteristics.hasMultipleSkus());
		assertNull("The bundle uid should be null", productCharacteristics.getBundleUid());
	}

	/**
	 * Test the behavior of get characteristics for sku code with a multi-sku product.
	 */
	@Test
	public void testGetCharacteristicsForSkuCodeWithMultiSkuProduct() {
		final ProductType productType = new ProductTypeImpl();
		productType.setMultiSku(true);
		context.checking(new Expectations() {
			{
				oneOf(productBundleService).findBundleCodeBySkuCode(SKU_CODE); will(returnValue(null));
				oneOf(productTypeService).findBySkuCode(SKU_CODE); will(returnValue(productType));
			}
		});

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristicsForSkuCode(SKU_CODE);

		assertTrue("Multi sku product should be identified as such", productCharacteristics.hasMultipleSkus());
		assertFalse("Basic product is not a bundle", productCharacteristics.isBundle());
		assertNull("The bundle uid should be null", productCharacteristics.getBundleUid());
	}

	/**
	 * Test the behavior of get characteristics for sku code with a bundle.
	 */
	@Test
	public void testGetCharacteristicsForSkuCodeWithBundle() {
		final ProductBundle bundle = createDynamicBundle();
		context.checking(new Expectations() {
			{
				oneOf(productBundleService).findBundleCodeBySkuCode(SKU_CODE); will(returnValue(bundle.getCode()));
				oneOf(productLookup).findByGuid(bundle.getCode()); will(returnValue(bundle));
			}
		});

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristicsForSkuCode(SKU_CODE);

		assertTrue("A bundle is a bundle", productCharacteristics.isBundle());
		assertTrue("The bundle is dynamic", productCharacteristics.isDynamicBundle());
		assertTrue("A dynamic bundle requires selection", productCharacteristics.offerRequiresSelection());
		assertEquals("The bundle uid should match", PRODUCT_UID, productCharacteristics.getBundleUid());
	}

	/**
	 * Test get characteristics for collection.
	 */
	@Test
	public void testGetCharacteristicsForCollection() {
		Product product = createSingleSkuProduct();
		ProductBundle bundle = createDynamicBundle();
		StoreProduct storeProduct1 = new StoreProductImpl(product);
		StoreProduct storeProduct2 = new StoreProductImpl(bundle);

		Map<String, ProductCharacteristics> productCharacteristicsMap = productCharacteristicsService.getProductCharacteristicsMap(
				Arrays.asList(storeProduct1, storeProduct2));

		assertEquals("There should be two map entries", 2, productCharacteristicsMap.size());

		ProductCharacteristics characteristics1 = productCharacteristicsMap.get(product.getCode());
		assertNotNull("There should be an entry for the first product code", characteristics1);
		assertFalse("A normal product is not a dynamic bundle", characteristics1.isDynamicBundle());

		ProductCharacteristics characteristics2 = productCharacteristicsMap.get(bundle.getCode());
		assertNotNull("There should be an entry for the second product code", characteristics2);
		assertTrue("A dynamic bundle should be recognized as such", characteristics2.isDynamicBundle());
	}

	/**
	 * Test get characteristics for a null collection.
	 */
	@Test
	public void testGetCharacteristicsForNullCollection() {
		Map<String, ProductCharacteristics> productCharacteristicsMap = productCharacteristicsService.getProductCharacteristicsMap(null);
		assertNotNull("The result should not be null", productCharacteristicsMap);
		assertTrue("The result should be empty", productCharacteristicsMap.isEmpty());
	}

	/**
	 * Test check for multiple skus on single-sku Product and Product Type.
	 */
	@Test
	public void shouldReturnFalseIfProductIsSingleSku() {
		Product singleSkuProduct = createSingleSkuProduct();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(singleSkuProduct);

		assertFalse("Single sku product should not be multi sku", productCharacteristics.hasMultipleSkus());
	}

	/**
	 * Test check for multiple skus on multi-sku Product and Product Type.
	 */
	@Test
	public void shouldReturnTrueIfProductIsMultiSku() {
		Product multipleSkuProduct = createMultiSkuProduct();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(multipleSkuProduct);

		assertTrue("Multiple sku product should be multi sku", productCharacteristics.hasMultipleSkus());
	}

	/**
	 * Test check for multiple skus on static and dynamic bundles with only single-sku constituents.
	 */
	@Test
	public void shouldReturnFalseIfProductIsBundleWithOnlySingleSkuConstituents() {
		ProductBundle staticBundle = createBundle();
		ProductCharacteristics productCharacteristicsStatic = productCharacteristicsService.getProductCharacteristics(staticBundle);

		ProductBundle dynamicBundle = createDynamicBundle();
		ProductCharacteristics productCharacteristicsDynamic = productCharacteristicsService.getProductCharacteristics(dynamicBundle);

		assertFalse("Single sku bundles should not be multi sku", productCharacteristicsStatic.hasMultipleSkus());
		assertFalse("Single sku dynamic bundles should not be multi sku", productCharacteristicsDynamic.hasMultipleSkus());
	}

	/**
	 * Test for multiple skus on bundle with multi-sku constituent.
	 */
	@Test
	public void shouldReturnTrueIfProductIsBundleWithMultiSkuConstituent() {
		Product multiSkuProduct = createMultiSkuProduct();
		ProductBundle multiSkuStaticBundle = createCustomBundle(multiSkuProduct);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(multiSkuStaticBundle);

		assertTrue("Bundle with multi-sku product should be multi sku", productCharacteristics.hasMultipleSkus());
	}

	/**
	 * Test for multiple skus on nested bundle with multi-sku constituent.
	 */
	@Test
	public void shouldReturnTrueIfProductIsNestedBundleWithMultiSkuConstituent() {
		Product multiSkuProduct = createMultiSkuProduct();
		ProductBundle multiSkuStaticBundleInner = createCustomBundle(multiSkuProduct);
		multiSkuStaticBundleInner.setSelectionRule(new SelectionRuleImpl(1));
		ProductBundle multiSkuStaticBundleOuter = createCustomBundle(multiSkuStaticBundleInner);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(multiSkuStaticBundleOuter);

		assertTrue("Nested dynamic bundle returns dynamic", productCharacteristics.isDynamicBundle());
		assertTrue("Nested bundle with multi-sku product should be multi-sku", productCharacteristics.hasMultipleSkus());
		assertTrue("Nested dynamic bundle with multi-sku product requires selection", productCharacteristics.offerRequiresSelection());
	}

	/**
	 * Test that a non-gift certificate ProductType is not a gift certificate.
	 */
	@Test
	public void shouldReturnFalseIfProductTypeIsNotGiftCertificate() {
		ProductType nullNameProductType = createProductType(true, false);
		ProductType nonGiftCertificate = new ProductTypeImpl();
		nonGiftCertificate.setMultiSku(true);
		nonGiftCertificate.setName(SKU_CODE);

		assertFalse("Non-gift certificate product with null name is not a gift certificate",
				nullNameProductType.isGiftCertificate());
		assertFalse("Non-gift certificate product with name not starting with 'Gift Certificate' is not a gift certificate",
				nonGiftCertificate.isGiftCertificate());
	}

	/**
	 * Test that a gift certificate ProductType is a gift certificate.
	 */
	@Test
	public void shouldReturnTrueIfProductTypeIsGiftCertificate() {
		ProductType giftCertificate = createProductType(false, true);

		assertTrue("Non-gift certificate product is not a gift certificate",
				giftCertificate.isGiftCertificate());
	}

	/**
	 * Creates the dynamic bundle.
	 *
	 * @return the product bundle
	 */
	private ProductBundle createDynamicBundle() {
		ProductBundle bundle = createBundle();
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		return bundle;
	}

	/**
	 * Creates the nested dynamic bundle.
	 *
	 * @return the product bundle
	 */
	private ProductBundle createNestedDynamicBundle() {
		ProductBundle nested = createDynamicBundle();
		BundleConstituent bundleConstituent = new BundleConstituentImpl();
		bundleConstituent.setConstituent(nested);

		ProductBundle bundle = createBundle();
		bundle.addConstituent(bundleConstituent);
		return bundle;
	}

	/**
	 * Creates the single sku product.
	 *
	 * @return the product
	 */
	private Product createSingleSkuProduct() {
		ProductType singleSkuProductType = createProductType(false, false);
		Product product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);
		product.setProductType(singleSkuProductType);
		product.setCode("PRODUCT");

		return product;
	}

	private ProductBundle createCustomBundle(final Product... products) {
		ProductBundle nestedBundle = createBundle();
		for (Product product : products) {
			BundleConstituent constituent = new BundleConstituentImpl();
			constituent.setConstituent(product);
			nestedBundle.addConstituent(constituent);
		}
		return nestedBundle;
	}

	/**
	 * Creates a bundle with a given constituent and a multi-sku constituent.
	 *
	 * @param singleSkuProduct the single sku product that will become one of the bundle constituents
	 * @return the product bundle
	 */
	private ProductBundle createBundleWithMultiSkuAndGivenConstituents(final Product singleSkuProduct) {
		ProductBundle bundle = createBundle();
		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(singleSkuProduct);
		bundle.addConstituent(constituent1);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(createMultiSkuProduct());
		bundle.addConstituent(constituent2);
		return bundle;
	}

	/**
	 * Creates the multi sku product.
	 *
	 * @return the product
	 */
	private Product createMultiSkuProduct() {
		ProductType multiSkuProductType = createProductType(true, false);
		Product product = new ProductImpl();
		product.setProductType(multiSkuProductType);
		return product;
	}

	/**
	 * Mock product type.
	 *
	 * @param multiSku the multi sku boolean
	 * @return the product type
	 */
	private ProductType createProductType(final boolean multiSku, final boolean giftCertificate) {
		final ProductTypeImpl productType = new ProductTypeImpl();
		productType.setMultiSku(multiSku);
		if (giftCertificate) {
			productType.setName(GiftCertificate.KEY_PRODUCT_TYPE);
		}
		return productType;
	}

}
