/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.catalog.ProductAssociationRetrieveStrategy;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;
import com.elasticpath.service.catalogview.AvailabilityStrategy;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.catalogview.ProductAvailabilityService;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests {@link StoreProductServiceImpl}.
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.GodClass"})
public class StoreProductServiceImplTest {

	private static final int DEFAULT_CATALOG_UIDPK = 1234;
	private static final String TEST_STORE_CODE = "testStoreCode";
	private static final String SKU = "SKU";

	public static final StructuredErrorMessage STRUCTURED_ERROR_MESSAGE
			= new StructuredErrorMessage("error.id", "error.message", Collections.emptyMap());

	private final StoreProductServiceImpl storeProductServiceImpl = new StoreProductServiceImpl();
	private static final int SIXTY_SECONDS = 60 * 1000;
	private static final String DEFAULT_SKU_CODE = "defaultSku";
	private static final String OTHER_SKU_CODE = "otherSku";
	private static final Collection<Long> PRODUCT_UIDS = new HashSet<>(Arrays.asList(1234L, 2345L, 3456L));

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private AllocationService allocationService;
	private AvailabilityStrategy availabilityStrategy;
	private ProductAvailabilityService availabilityService;
	private ProductInventoryShoppingService productInventoryShoppingService;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final BundleIdentifierImpl bundleIdentifier = new BundleIdentifierImpl();
	private ProductService productService;
	private ProductLookup productLookup;

	/**
	 * Sets up the test case.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		allocationService = context.mock(AllocationService.class);
		availabilityService = context.mock(ProductAvailabilityService.class);
		availabilityStrategy = context.mock(AvailabilityStrategy.class);
		productService = context.mock(ProductService.class);
		productLookup = context.mock(ProductLookup.class);
		productInventoryShoppingService = context.mock(ProductInventoryShoppingService.class);

		BeanFactory beanFactory;
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("productSkuConstituent", ProductSkuConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRODUCT_ASSOCIATION, ProductAssociationImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.BUNDLE_CONSTITUENT, BundleConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		storeProductServiceImpl.setAvailabilityStrategies(Collections.singletonList(availabilityStrategy));
		storeProductServiceImpl.setBundleIdentifier(bundleIdentifier);
		storeProductServiceImpl.setProductAvailabilityService(availabilityService);
		storeProductServiceImpl.setProductInventoryShoppingService(productInventoryShoppingService);
		storeProductServiceImpl.setProductLookup(productLookup);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests setStoreProductAssociations().
	 * Checks if the product associations' target products
	 * get promotions applied and if associations are set properly.
	 */
	@Test
	public void testSetStoreProductAssociations() {
		final ProductAssociationType assocType = ProductAssociationType.ACCESSORY;

		final Product targetProduct = createProductWithSku();

		final ProductSku targetSku = targetProduct.getDefaultSku();
		targetSku.setSkuCode("ABC");

		final ProductAssociationImpl assoc1 = new ProductAssociationImpl();
		assoc1.setStartDate(new Date(System.currentTimeMillis() - SIXTY_SECONDS));
		assoc1.setAssociationType(assocType);
		assoc1.setTargetProduct(targetProduct);

		CatalogImpl catalogImpl = new CatalogImpl();
		catalogImpl.setMaster(true);
		catalogImpl.setCode("catalog1");

		Category category = new CategoryImpl();
		category.setCatalog(catalogImpl);

		Set<Category> categories = new HashSet<>();
		categories.add(category);

		targetProduct.setCode("productCode");
		targetProduct.setCategories(categories);
		StoreProduct storeProduct = new StoreProductImpl(targetProduct);

		final SkuInventoryDetails inventoryDetails = new SkuInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(true);

		List<Warehouse> warehouseList = new ArrayList<>();
		final WarehouseImpl warehouse = new WarehouseImpl();
		warehouseList.add(warehouse);

		final Store store = new StoreImpl();
		store.setCatalog(catalogImpl);
		store.setWarehouses(warehouseList);

		final HashSet<ProductAssociation> assocSet = new HashSet<>();
		assocSet.add(assoc1);

		final ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode("productCode");
		criteria.setCatalogCode("catalog1");
		criteria.setWithinCatalogOnly(true);

		storeProductServiceImpl.setAvailabilityStrategies(Collections.<AvailabilityStrategy>emptyList());

		final ProductAssociationRetrieveStrategy productAssociationRetrieveStrategy = context.mock(ProductAssociationRetrieveStrategy.class);
		storeProductServiceImpl.setProductAssociationRetrieveStrategy(productAssociationRetrieveStrategy);

		context.checking(new Expectations() {
			{
				Map<String, SkuInventoryDetails> skuInventoryMap = Collections.singletonMap("ABC", inventoryDetails);

				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(targetProduct, store);
				will(returnValue(skuInventoryMap));

				oneOf(productAssociationRetrieveStrategy).getAssociations(criteria);
				will(returnValue(assocSet));

				allowing(allocationService).hasSufficientUnallocatedQty(targetSku, warehouse.getUidPk(), targetProduct.getMinOrderQty());
				will(returnValue(true));

				allowing(availabilityService).isProductAvailable(targetProduct, skuInventoryMap, true);
				will(returnValue(true));
				allowing(availabilityService).isProductDisplayable(
						targetProduct, store, skuInventoryMap, true);
				will(returnValue(true));
				allowing(availabilityService).isSkuAvailable(targetProduct, targetSku, inventoryDetails);
				will(returnValue(true));
				allowing(availabilityService).isSkuDisplayable(
						targetProduct, targetSku, store, inventoryDetails);
				will(returnValue(true));
			}
		});

		storeProductServiceImpl.setStoreProductAssociations(storeProduct, store, "catalog1");

		final Set<ProductAssociation> associationsByType = storeProduct.getAssociationsByType(assocType);
		assertEquals("Expected exactly one ProductAssociation", 1, associationsByType.size());

		final ProductAssociation actualProductAssociation = associationsByType.iterator().next();
		assertTrue("Expected ProductAssociation to have targetProduct that is a StoreProduct",
				actualProductAssociation.getTargetProduct() instanceof StoreProduct);
		assertEquals("Expected StoreProduct to wrap targetProduct", targetProduct,
				((StoreProduct) actualProductAssociation.getTargetProduct()).getWrappedProduct());
	}


	/**
	 * Tests that a product included in a first catalog is not included in a second catalog.
	 */
	@Test
	public void testIsNotIncludedInSecondCatalog() {
		Catalog catalog1 = new CatalogImpl();
		catalog1.setMaster(true);
		catalog1.setUidPk(1);

		Catalog catalog2 = new CatalogImpl();
		catalog2.setMaster(true);
		catalog2.setUidPk(2);

		Set<Category> categories = new HashSet<>();
		Category category = new CategoryImpl();
		category.setCatalog(catalog1);
		categories.add(category);

		Product product = createProductWithSku();
		product.setCategories(categories);

		assertTrue("The product should be in the first catalog.", product.isInCatalog(catalog1, true));
		assertFalse("The product should not be in the second catalog.", product.isInCatalog(catalog2, true));
	}

	/**
	 * Tests that a non-master catalog with a non-linked category could have a product that is included by default.
	 */
	@Test
	public void testIsIncludedNonLinkedCategoryVirtualCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setMaster(false);

		Product product = createProductWithSku();
		Set<Category> categories = new HashSet<>();
		Category category = new CategoryImpl();

		category.setCatalog(catalog);

		categories.add(category);
		product.setCategories(categories);
		assertTrue("Product should be included in a non-linked category in a virtual catalog", product.isInCatalog(catalog, true));
	}

	/**
	 * Tests that a virtual catalog with a linked category could have a product
	 * that is included after setting the included flag on the category.
	 */
	@Test
	public void testIsIncludedLinkedCategoryVirtualCatalog() {
		Catalog catalog = new CatalogImpl();
		Catalog masterCatalog = new CatalogImpl();
		masterCatalog.setMaster(true);
		catalog.setMaster(false);

		Product product = createProductWithSku();
		Set<Category> categories = new HashSet<>();
		Category masterCategory = new CategoryImpl();
		masterCategory.setCatalog(masterCatalog);
		Category category = new LinkedCategoryImpl();
		category.setCatalog(catalog);
		category.setMasterCategory(masterCategory);

		categories.add(category);
		product.setCategories(categories);
		assertFalse("Product should not be included by default in a linked category in a virtual catalog", product.isInCatalog(catalog, true));

		// set category included in catalog
		category.setIncluded(true);

		assertTrue("Product should be included in a linked category in a virtual catalog", product.isInCatalog(catalog, true));
	}

	/**
	 * Tests that a master catalog with a category could have a product
	 * that is included by default.
	 */
	@Test
	public void testIsIncludedCategoryMasterCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setMaster(true);

		Product product = createProductWithSku();
		Set<Category> categories = new HashSet<>();
		Category category = new CategoryImpl();
		category.setCatalog(catalog);
		categories.add(category);
		product.setCategories(categories);

		assertTrue("Product should be included in a category in a master catalog", product.isInCatalog(catalog, true));
	}

	/**
	 * Tests {@link StoreProductServiceImpl#determineSkusAvailability()}. Check
	 * that the SkuVailability map is populated correctly
	 */
	@Test
	public void testDetermineSkusAvailabilitySkuAvailableWithInventoryAndWithinDateRange() {
		final Map<String, SkuInventoryDetails> skuInventoryDetails = new LinkedHashMap<>();
		SkuInventoryDetails inventoryDetails = new SkuInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(true);

		skuInventoryDetails.put(SKU, inventoryDetails);

		final Product product = createProductWithNoSku();
		ProductSku sku = createProductSkuWithinDateRange(SKU, true);
		product.setDefaultSku(sku);

		StoreProductImpl storeProduct = new StoreProductImpl(product);
		storeProductServiceImpl.determineSkusAvailability(Collections.singleton(sku),
				skuInventoryDetails, storeProduct);

		assertTrue(storeProduct.isSkuAvailable(SKU));
	}

	/**
	 * Tests {@link StoreProductServiceImpl#determineSkusAvailability()}. Check
	 * that the SkuVailability map is populated correctly
	 */
	@Test
	public void testDetermineSkusAvailabilityProductSkuNotAvailabeWithInventoryButWithinDateRange() {
		final Map<String, SkuInventoryDetails> skuInventoryDetails = new LinkedHashMap<>();
		final Map<String, ProductSku> skus = new HashMap<>();

		SkuInventoryDetails inventoryDetails = new SkuInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(false);

		skuInventoryDetails.put(SKU, inventoryDetails);
		skus.put(SKU, createProductSkuWithinDateRange(SKU, true));

		Product product = new ProductImpl();
		product.setStartDate(new Date(System.currentTimeMillis() - 1));
		product.setProductSkus(skus);

		StoreProductImpl storeProduct = new StoreProductImpl(product);
		storeProductServiceImpl.determineSkusAvailability(skus.values(),
				skuInventoryDetails, storeProduct);

		assertFalse(storeProduct.isSkuAvailable(SKU));
	}

	/**
	 * Tests {@link StoreProductServiceImpl#determineSkusAvailability()}. Check
	 * that the SkuVailability map is populated correctly
	 */
	@Test
	public void testDetermineSkusAvailabilitySkuAvailabeWithInventoryButNotWithinDateRange() {
		final Map<String, SkuInventoryDetails> skuInventoryDetails = new LinkedHashMap<>();
		final Map<String, ProductSku> skus = new HashMap<>();

		SkuInventoryDetails inventoryDetails = new SkuInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(true);

		skuInventoryDetails.put(SKU, inventoryDetails);
		skus.put(SKU, createProductSkuWithinDateRange(SKU, false));

		Product product = new ProductImpl();
		product.setStartDate(new Date(System.currentTimeMillis() - 1));
		product.setProductSkus(skus);

		StoreProductImpl storeProduct = new StoreProductImpl(product);
		storeProductServiceImpl.determineSkusAvailability(skus.values(),
				skuInventoryDetails, storeProduct);

		assertFalse(storeProduct.isSkuAvailable(SKU));
	}

	@Test
	public void testGetProductForStoreAvailabilityDetailsWithProductWithNoSkus() {
		final Product product = new ProductImpl();
		final Store store = new StoreImpl();

		context.checking(new Expectations() {
			{
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(Collections.emptyMap()));
			}
		});
		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);

		assertFalse(storeProduct.isProductAvailable());
		assertFalse(storeProduct.isProductDisplayable());
	}

	@Test
	public void testGetProductForStoreAvailabilityDetailsWithSpecifiedSku() {
		final ProductSku sku = new ProductSkuImpl();
		sku.setUidPk(1L);
		sku.setSkuCode("sku");

		final Product product = new ProductImpl();
		product.setUidPk(1L);
		product.addOrUpdateSku(sku);

		final Store store = new StoreImpl();

		final SkuInventoryDetails skuInventory = new SkuInventoryDetails();
		final ImmutableMap<String, SkuInventoryDetails> skuInventoryMap = ImmutableMap.of("sku", skuInventory);

		context.checking(new Expectations() {
			{
				oneOf(productLookup).findByUid(with(product.getUidPk()));
				will(returnValue(product));

				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(skuInventoryMap));

				oneOf(availabilityService).isProductAvailable(product, skuInventoryMap, true);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(product, store, skuInventoryMap, true);
				will(returnValue(true));
				oneOf(availabilityService).isSkuAvailable(product, sku, skuInventory);
				will(returnValue(true));
				oneOf(availabilityService).isSkuDisplayable(product, sku, store, skuInventory);
				will(returnValue(true));

				exactly(2).of(availabilityStrategy).getAvailability(product, true, true);
				will(returnValue(Availability.AVAILABLE));
			}
		});

		final boolean loadProductAssociations = false;
		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(
				product.getUidPk(), store, loadProductAssociations);
		assertTrue(storeProduct.isSkuAvailable(sku.getSkuCode()));
		assertTrue(storeProduct.isSkuDisplayable(sku.getSkuCode()));
	}

	/**
	 * Test getting {@link IndexProduct} with no {@link ProductSku)s.
	 */
	@Test
	public void testGetIndexProductsWithoutSkus() {
		final Collection<Product> retrievedProducts = createManyProducts(PRODUCT_UIDS);
		final SkuInventoryDetails defaultInventoryDetails = new SkuInventoryDetails();

		StoreProductServiceImpl service = new StoreProductServiceImpl() {
			@Override
			Map<String, SkuInventoryDetails> calculateInventoryDetailsForAllSkus(final Product product, final Store store) {
				Map<String, SkuInventoryDetails> returnMap = new HashMap<>();
				returnMap.put("ABC", defaultInventoryDetails);
				return returnMap;
			}
		};

		service.setProductService(productService);

		context.checking(new Expectations() {
			{
				oneOf(productService).findByUidsWithFetchGroupLoadTuner(PRODUCT_UIDS, null);
				will(returnValue(retrievedProducts));
				allowing(productInventoryShoppingService).getSkuInventoryDetails(null, null);
				will(returnValue(defaultInventoryDetails));
			}
		});

		final Collection<Store> stores = Collections.singleton(createTestStore());
		final Collection<IndexProduct> retrievedIndexProducts = service.getIndexProducts(PRODUCT_UIDS, stores, null);
		assertEquals("IndexProduct count is incorrect: ", retrievedIndexProducts.size(), retrievedIndexProducts.size());

		for (IndexProduct retrievedIndexProduct : retrievedIndexProducts) {
			Long retrievedIndexProductUid = retrievedIndexProduct.getUidPk();
			assertTrue("IndexProduct uid is missing: " + retrievedIndexProductUid, PRODUCT_UIDS.contains(retrievedIndexProductUid));
			assertFalse("Product should not be available.", retrievedIndexProduct.isAvailable(TEST_STORE_CODE));
			assertFalse("Product should not be displayable.", retrievedIndexProduct.isDisplayable(TEST_STORE_CODE));
		}
	}

	/**
	 * Test getting {@link IndexProduct} with {@link ProductSku)s.
	 */
	@Test
	public void testGetIndexProductsWithMultipleSkus() {
		final List<Store> stores = Collections.singletonList(createTestStore());
		final List<Product> retrievedProductsWithSkus = createManyProductsWithSkus(PRODUCT_UIDS, 3);
		final Map<String, SkuInventoryDetails> defaultInventoryDetails = new HashMap<>();

		final SkuInventoryDetails emptyInventory = new SkuInventoryDetails();
		emptyInventory.setAvailableQuantityInStock(0);
		emptyInventory.setHasSufficientUnallocatedQty(false);

		final SkuInventoryDetails hasInventory = new SkuInventoryDetails();
		hasInventory.setAvailableQuantityInStock(1);
		hasInventory.setHasSufficientUnallocatedQty(true);

		defaultInventoryDetails.put("testSkuCode0", emptyInventory);
		defaultInventoryDetails.put("testSkuCode1", emptyInventory);
		defaultInventoryDetails.put("testSkuCode2", hasInventory);

		storeProductServiceImpl.setProductService(productService);

		context.checking(new Expectations() {
			{
				oneOf(productService).findByUidsWithFetchGroupLoadTuner(PRODUCT_UIDS, null);
				will(returnValue(retrievedProductsWithSkus));

				allowing(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(with(any(Product.class)), with(createTestStore()));
				will(returnValue(defaultInventoryDetails));

				Store store = stores.get(0);
				oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(0), defaultInventoryDetails, false);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
				will(returnValue(true));

				oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(1), defaultInventoryDetails, false);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
				will(returnValue(true));

				oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(2), defaultInventoryDetails, false);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
				will(returnValue(true));
			}
		});

		final Collection<IndexProduct> retrievedIndexProducts = storeProductServiceImpl.getIndexProducts(PRODUCT_UIDS, stores, null);

		assertEquals("IndexProduct count is incorrect: ", retrievedIndexProducts.size(), retrievedIndexProducts.size());
		for (IndexProduct retrievedIndexProduct : retrievedIndexProducts) {
			Long retrievedIndexProductUid = retrievedIndexProduct.getUidPk();
			assertTrue("IndexProduct uid is missing: " + retrievedIndexProductUid, PRODUCT_UIDS.contains(retrievedIndexProductUid));
			assertTrue("Product should be available.", retrievedIndexProduct.isAvailable(TEST_STORE_CODE));
			assertTrue("Product should be displayable.", retrievedIndexProduct.isDisplayable(TEST_STORE_CODE));
		}
	}


	/**
	 * Test getting {@link IndexProduct} with {@link ProductSku)s.
	 */
	@Test
	public void testGetIndexProductsWithMultipleSkusAndNoInventory() {
		final List<Store> stores = Collections.singletonList(createTestStore());
		final List<Product> retrievedProductsWithSkus = createManyProductsWithSkus(PRODUCT_UIDS, 3);
		final Map<String, SkuInventoryDetails> defaultInventoryDetails = new HashMap<>();

		final SkuInventoryDetails emptyInventory = new SkuInventoryDetails();
		emptyInventory.setAvailableQuantityInStock(0);
		emptyInventory.setHasSufficientUnallocatedQty(false);

		defaultInventoryDetails.put("testSkuCode0", emptyInventory);
		defaultInventoryDetails.put("testSkuCode1", emptyInventory);
		defaultInventoryDetails.put("testSkuCode2", emptyInventory);

		storeProductServiceImpl.setProductService(productService);

		context.checking(new Expectations() {
			{
				oneOf(productService).findByUidsWithFetchGroupLoadTuner(PRODUCT_UIDS, null);
				will(returnValue(retrievedProductsWithSkus));

				allowing(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(with(any(Product.class)), with(createTestStore()));
				will(returnValue(defaultInventoryDetails));

				Store store = stores.get(0);
				oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(0), defaultInventoryDetails, false);
				will(returnValue(false));
				oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
				will(returnValue(false));
				oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(1), defaultInventoryDetails, false);
				will(returnValue(false));
				oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(1), store, defaultInventoryDetails, false);
				will(returnValue(false));
				oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(2), defaultInventoryDetails, false);
				will(returnValue(false));
				oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(2), store, defaultInventoryDetails, false);
				will(returnValue(false));
			}
		});

		final Collection<IndexProduct> retrievedIndexProducts = storeProductServiceImpl.getIndexProducts(PRODUCT_UIDS, stores, null);
		for (IndexProduct retrievedIndexProduct : retrievedIndexProducts) {
			Long retrievedIndexProductUid = retrievedIndexProduct.getUidPk();
			assertTrue("IndexProduct uid is missing: " + retrievedIndexProductUid, PRODUCT_UIDS.contains(retrievedIndexProductUid));
			assertFalse("Product should not be available.", retrievedIndexProduct.isAvailable(TEST_STORE_CODE));
			assertFalse("Product should not displayable.", retrievedIndexProduct.isDisplayable(TEST_STORE_CODE));
		}
	}

	/**
	 * Test that the availability is set on the store product.
	 */
	@Test
	public void testAvailabilityStatus() {
		final Product product = createProductWithNoSku();
		final ProductSku sku = createProductSkuWithinDateRange(SKU, true);
		product.setDefaultSku(sku);
		final Store store = new StoreImpl();
		final SkuInventoryDetails skuInventoryDetails = new SkuInventoryDetails();

		context.checking(new Expectations() {
			{
				final Map<String, SkuInventoryDetails> skuInventoryMap = Collections.singletonMap(SKU, skuInventoryDetails);
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(skuInventoryMap));

				oneOf(availabilityService).isProductAvailable(product, skuInventoryMap, true);
				will(returnValue(true));
				oneOf(availabilityService).isSkuAvailable(product, sku, skuInventoryDetails);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(product, store, skuInventoryMap, true);
				will(returnValue(false));
				oneOf(availabilityService).isSkuDisplayable(product, sku, store, skuInventoryDetails);
				will(returnValue(false));

				exactly(2).of(availabilityStrategy).getAvailability(product, true, false);
				will(returnValue(Availability.AVAILABLE));
			}
		});

		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);
		assertEquals("The availability should have been set", Availability.AVAILABLE, storeProduct.getProductAvailability());
	}

	/**
	 * Test that the product is not available when it has no skus.
	 */
	@Test
	public void testProductNotAvailableWhenNoSkus() {
		final Product product = createProductWithNoSku();
		final Store store = new StoreImpl();

		context.checking(new Expectations() {
			{
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(Collections.emptyMap()));
			}
		});

		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);
		assertEquals("A product with no skus should not be available", Availability.NOT_AVAILABLE, storeProduct.getProductAvailability());
	}

	/**
	 * Test that availability is null when no relevant availability strategy was set.
	 */
	@Test
	public void testNullAvailabilityWhenNoRelevantStrategy() {
		final Product product = createProductWithNoSku();
		final ProductSku sku = createProductSkuWithinDateRange(SKU, true);
		product.setDefaultSku(sku);
		final Store store = new StoreImpl();
		final SkuInventoryDetails skuInventoryDetails = createExistingInventoryDetails();

		context.checking(new Expectations() {
			{
				final Map<String, SkuInventoryDetails> skuInventoryMap = Collections.singletonMap(SKU, skuInventoryDetails);
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(skuInventoryMap));

				oneOf(availabilityService).isProductAvailable(product, skuInventoryMap, true);
				will(returnValue(true));
				oneOf(availabilityService).isSkuAvailable(product, sku, skuInventoryDetails);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(product, store, skuInventoryMap, true);
				will(returnValue(false));
				oneOf(availabilityService).isSkuDisplayable(product, sku, store, skuInventoryDetails);
				will(returnValue(false));
				exactly(2).of(availabilityStrategy).getAvailability(product, true, false);
				will(returnValue(null));
			}
		});

		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);
		assertNull("The availability is null when no appropriate strategy found", storeProduct.getProductAvailability());
	}

	/**
	 * Test that a product constituent's default sku is changed to an available sku if the default is unavailable.
	 */
	@Test
	public void testChangeBundleConstituentDefaultSkuIfDefaultSkuIsUnavailable() {
		final Product multiSkuProduct = createProductWithSkus(Arrays.asList(DEFAULT_SKU_CODE, OTHER_SKU_CODE));
		multiSkuProduct.setDefaultSku(multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE));

		final BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(multiSkuProduct);

		final ProductBundle bundle = createBundleWithConstituent(constituent);
		final SkuInventoryDetails defaultSkuSkuInventoryDetails = new SkuInventoryDetails();
		final SkuInventoryDetails otherSkuSkuInventoryDetails = new SkuInventoryDetails();
		final Store store = createTestStore();

		context.checking(new Expectations() {
			{
				allowing(productInventoryShoppingService).getSkuInventoryDetails(multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE), store);
				will(returnValue(defaultSkuSkuInventoryDetails));

				allowing(availabilityService).isSkuAvailable(multiSkuProduct,
						multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE), defaultSkuSkuInventoryDetails);
				will(returnValue(false));

				oneOf(productInventoryShoppingService).getSkuInventoryDetails(multiSkuProduct.getSkuByCode(OTHER_SKU_CODE), store);
				will(returnValue(otherSkuSkuInventoryDetails));

				oneOf(availabilityService).isSkuAvailable(multiSkuProduct, multiSkuProduct.getSkuByCode(OTHER_SKU_CODE),
						otherSkuSkuInventoryDetails);
				will(returnValue(true));
			}
		});

		storeProductServiceImpl.updateBundleConstituentDefaultSkus(bundle, store);

		assertEquals("The constituent's default sku should have changed.", OTHER_SKU_CODE,
				constituent.getConstituent().getProductSku().getSkuCode());
	}

	/**
	 * Test that a product constituent's default sku is not changed if the default is available.
	 */
	@Test
	public void testDoNotChangeBundleConstituentDefaultSkuIfDefaultSkuIsAvailable() {
		final Product multiSkuProduct = createProductWithSkus(Arrays.asList(DEFAULT_SKU_CODE, OTHER_SKU_CODE));
		multiSkuProduct.setDefaultSku(multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE));

		final BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(multiSkuProduct);

		final ProductBundle bundle = createBundleWithConstituent(constituent);
		final SkuInventoryDetails defaultSkuSkuInventoryDetails = new SkuInventoryDetails();
		final Store store = createTestStore();

		context.checking(new Expectations() {
			{
				oneOf(productInventoryShoppingService).getSkuInventoryDetails(multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE), store);
				will(returnValue(defaultSkuSkuInventoryDetails));

				oneOf(availabilityService).isSkuAvailable(multiSkuProduct,
						multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE), defaultSkuSkuInventoryDetails);
				will(returnValue(true));
			}
		});

		storeProductServiceImpl.updateBundleConstituentDefaultSkus(bundle, store);

		assertEquals("The constituent's default sku should not have changed.", DEFAULT_SKU_CODE,
				constituent.getConstituent().getProductSku().getSkuCode());
	}

	/**
	 * Test that a product constituent's default sku is not changed if the constituent has no available skus.
	 */
	@Test
	public void testDoNotChangeBundleConstituentDefaultSkuIfNoSkuIsAvailable() {
		final Product multiSkuProduct = createProductWithSkus(Arrays.asList(DEFAULT_SKU_CODE, OTHER_SKU_CODE));
		multiSkuProduct.setDefaultSku(multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE));

		final BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(multiSkuProduct);

		final ProductBundle bundle = createBundleWithConstituent(constituent);
		final SkuInventoryDetails defaultSkuSkuInventoryDetails = new SkuInventoryDetails();
		final SkuInventoryDetails otherSkuSkuInventoryDetails = new SkuInventoryDetails();
		final Store store = createTestStore();

		context.checking(new Expectations() {
			{
				allowing(productInventoryShoppingService).getSkuInventoryDetails(multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE), store);
				will(returnValue(defaultSkuSkuInventoryDetails));

				allowing(availabilityService).isSkuAvailable(multiSkuProduct,
						multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE), defaultSkuSkuInventoryDetails);
				will(returnValue(false));

				oneOf(productInventoryShoppingService).getSkuInventoryDetails(multiSkuProduct.getSkuByCode(OTHER_SKU_CODE), store);
				will(returnValue(otherSkuSkuInventoryDetails));

				oneOf(availabilityService).isSkuAvailable(multiSkuProduct, multiSkuProduct.getSkuByCode(OTHER_SKU_CODE),
						otherSkuSkuInventoryDetails);
				will(returnValue(false));
			}
		});

		storeProductServiceImpl.updateBundleConstituentDefaultSkus(bundle, store);

		assertEquals("The constituent's default sku should not have changed.", DEFAULT_SKU_CODE,
				constituent.getConstituent().getProductSku().getSkuCode());
	}

	/**
	 * Test that a fixed sku constituent is not changed.
	 */
	@Test
	public void testDoNotChangeBundleSkuConstituentSku() {
		final Product multiSkuProduct = createProductWithSkus(Arrays.asList(DEFAULT_SKU_CODE, OTHER_SKU_CODE));
		multiSkuProduct.setDefaultSku(multiSkuProduct.getSkuByCode(DEFAULT_SKU_CODE));

		final BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(multiSkuProduct.getSkuByCode(OTHER_SKU_CODE));

		final ProductBundle bundle = createBundleWithConstituent(constituent);
		final Store store = createTestStore();

		storeProductServiceImpl.updateBundleConstituentDefaultSkus(bundle, store);

		assertEquals("The sku constituent's sku should not have changed.", OTHER_SKU_CODE,
				constituent.getConstituent().getProductSku().getSkuCode());
	}

	/**
	 * Test that IndexProduct marks a product as not available for purchase and not displayable when the product has no skus.
	 */
	@Test
	public void ensureProductIsNotIndexedWhenProductHasNoSkus() {
		Store store = createAStoreConfiguredToNotDisplayOutOfStock();
		Product product = createAvailableAvailableWhenInStockProductWithNoSku();

		IndexProduct indexProduct = storeProductServiceImpl.createIndexProduct(product, Collections.singletonList(store));

		assertFalse("The product should not be available for purchase", indexProduct.isAvailable(store.getCode()));
		assertFalse("The product should not be displayable", indexProduct.isDisplayable(store.getCode()));
	}

	/**
	 * When the bundle is in stock, test that the IndexProduct is available and displayable.
	 */
	@Test
	public void ensureAvailableBundleIsIndexedWhenStoreDoesNotDisplayOutOfStock() {
		Product product = createAvailableAvailableWhenInStockProductWithSku();
		BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(product);
		ProductBundle bundle = createBundleWithConstituent(constituent);

		Store store = createAStoreConfiguredToNotDisplayOutOfStock();

		givenBundleIsAvailable(bundle, store);

		IndexProduct indexProduct = storeProductServiceImpl.createIndexProduct(bundle, Collections.singletonList(store));

		assertTrue("The bundle should be available for purchase", indexProduct.isAvailable(store.getCode()));
		assertTrue("The bundle should be displayable", indexProduct.isDisplayable(store.getCode()));
	}

	//=================================================================================================
	// Data setup methods
	//=================================================================================================

	private void givenBundleIsAvailable(final ProductBundle bundle, final Store store) {
		SkuInventoryDetails inventoryDetails = createExistingInventoryDetails();
		inventoryDetails.setAvailableQuantityInStock(1);
		inventoryDetails.setMessageCode(InventoryMessage.IN_STOCK);
		inventoryDetails.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		final Map<String, SkuInventoryDetails> skuInventoryDetailsMap = new HashMap<>();
		skuInventoryDetailsMap.put(bundle.getDefaultSku().getSkuCode(), inventoryDetails);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(bundle, store);
				will(returnValue(skuInventoryDetailsMap));

				oneOf(availabilityService).isProductAvailable(bundle, skuInventoryDetailsMap, false);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(bundle, store, skuInventoryDetailsMap, false);
				will(returnValue(true));
			}
		});
	}

	private SkuInventoryDetails createExistingInventoryDetails() {
		SkuInventoryDetails validSkuInventoryDetails = new SkuInventoryDetails();
		validSkuInventoryDetails.setHasSufficientUnallocatedQty(true);
		validSkuInventoryDetails.setStockDate(new Date());
		validSkuInventoryDetails.setAvailableQuantityInStock(1);
		return validSkuInventoryDetails;
	}

	private Store createTestStore() {
		Store store = new StoreImpl();
		store.setCode(TEST_STORE_CODE);
		store.setCatalog(createDefaultCatalog());
		return store;
	}

	private Store createAStoreConfiguredToNotDisplayOutOfStock() {
		final Store store = createTestStore();
		store.setDisplayOutOfStock(false);
		return store;
	}

	private Category createDefaultCategory() {
		Category category = new CategoryImpl();
		category.setCatalog(createDefaultCatalog());
		return category;
	}

	private Catalog createDefaultCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setUidPk(DEFAULT_CATALOG_UIDPK);
		return catalog;
	}

	private ProductBundle createBundleWithConstituent(final BundleConstituent constituent) {
		ProductBundle bundle = new ProductBundleImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isInCatalog(final Catalog catalog, final boolean checkForLinkedCategories) {
				return true;
			}

			@Override
			public boolean isWithinDateRange(final Date currentDate) {
				return true;
			}
		};
		bundle.setDefaultSku(createProductSkuWithinDateRange(true));
		bundle.addConstituent(constituent);
		return bundle;
	}

	private Product createProductWithNoSku() {
		Product product = new ProductImpl();
		product.initialize();
		return product;
	}

	private Product createProductWithSku() {
		Product product = createProductWithNoSku();
		product.setDefaultSku(createProductSku());
		return product;
	}

	private ProductSku createProductSku() {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		return productSku;
	}

	private Product createAvailableAvailableWhenInStockProductWithNoSku() {
		Product product = new ProductImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isInCatalog(final Catalog catalog, final boolean checkForLinkedCategories) {
				return true;
			}

			@Override
			public boolean isWithinDateRange(final Date currentDate) {
				return true;
			}
		};
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		product.initialize();
		return product;
	}

	private Product createAvailableAvailableWhenInStockProductWithSku() {
		Product product = createAvailableAvailableWhenInStockProductWithNoSku();
		product.setDefaultSku(createProductSku());
		return product;
	}

	private ProductSku createProductSkuWithinDateRange(final boolean isWithinDateRange) {
		ProductSku productSku = new ProductSkuImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isWithinDateRange(final Date currentDate) {
				return isWithinDateRange;
			}
		};
		productSku.initialize();
		return productSku;
	}

	private ProductSku createProductSkuWithinDateRange(final String code, final boolean isWithinDateRange) {
		ProductSku productSku = createProductSkuWithinDateRange(isWithinDateRange);
		productSku.setSkuCode(code);
		return productSku;
	}

	private Product createProductWithSkus(final Collection<String> skuCodes) {
		Product product = new ProductImpl();
		product.setCode("productCode");
		for (String skuCode : skuCodes) {
			ProductSku sku = createProductSku();
			sku.setSkuCode(skuCode);
			product.addOrUpdateSku(sku);
		}
		return product;
	}

	private List<Product> createManyProductsWithSkus(final Collection<Long> productUids, final int skusPerProduct) {
		final List<Product> products = createManyProducts(productUids);
		for (Product product : products) {
			Map<String, ProductSku> productSkus = new HashMap<>();
			for (int i = 0; i < skusPerProduct; i++) {
				String productSkuCode = "testSkuCode" + i;
				ProductSku productSku = createProductSku();
				productSkus.put(productSkuCode, productSku);
			}
			product.setProductSkus(productSkus);
		}
		return products;
	}

	private List<Product> createManyProducts(final Collection<Long> productUids) {
		final List<Product> products = new ArrayList<>();

		for (Long productUid : productUids) {
			Product newProduct = createProductWithSku();
			newProduct.setUidPk(productUid);
			newProduct.setProductSkus(new HashMap<>());
			newProduct.setCategoryAsDefault(createDefaultCategory());
			products.add(newProduct);
		}

		return products;
	}
}
