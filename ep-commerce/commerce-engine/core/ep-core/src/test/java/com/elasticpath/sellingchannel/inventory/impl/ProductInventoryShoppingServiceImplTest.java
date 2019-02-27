/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.inventory.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.impl.InventoryFacadeImpl;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;
import com.elasticpath.inventory.strategy.InventoryStrategy;
import com.elasticpath.inventory.strategy.impl.InventoryJournalRollupImpl;
import com.elasticpath.inventory.strategy.impl.JournalingInventoryStrategy;
import com.elasticpath.sellingchannel.inventory.impl.ProductInventoryShoppingServiceImpl.NodeInventory;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.impl.ProductInventoryManagementServiceImpl;
import com.elasticpath.service.catalogview.impl.InventoryMessage;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Unit test for the {@code ProductInventoryShoppingServiceImplTest}.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public class ProductInventoryShoppingServiceImplTest {

	private static final String SKU_CODE = "SKU";
	private static final String BUNDLE_ROOT_SKU = "skuRoot";
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int FOUR = 4;
	private static final int FIVE = 5;
	private static final long WAREHOUSE_UIDPK = 1234;

	private ProductInventoryShoppingServiceImpl productInventoryShoppingService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
		BeanFactory beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("productSkuConstituent", ProductSkuConstituentImpl.class);

		productInventoryShoppingService = new ProductInventoryShoppingServiceImpl();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test looking up a sku's inventory details.
	 */
	@Test
	public void ensureSkuWithInventoryIsInStock() {
		String inStockSkuCode = "InStockSku";

		Product product = createOnlyAvailableWhenInStockProductWithSkus(inStockSkuCode);

		Collection<String> skuCodes = listSkuCodesForInventoryLookup(product);
		Map<String, Inventory> inventoryMap = assignInventoryToSku(inStockSkuCode);

		ProductInventoryManagementServiceImpl pims = mockInventoryLookupInPIMS(skuCodes, inventoryMap);
		productInventoryShoppingService.setProductInventoryManagementService(pims);

		Store store = setUpStoreWithWarehouse();

		SkuInventoryDetails inStockInventoryDetails = productInventoryShoppingService.getSkuInventoryDetails(product.getSkuByCode(inStockSkuCode),
				store);

		assertEquals("The sku should be in stock.", InventoryMessage.IN_STOCK, inStockInventoryDetails.getMessageCode());
	}

	/**
	 * Test looking up a bundle sku's inventory details with a shoppingItemDto uses only the selected constituent skus in inventory calculation.
	 */
	@Test
	public void ensureInventoryCalculationOnlyUsesSkusInShoppingItemDto() {
		String skuCodeInShoppingItemDto = "selectedSku";
		String anotherSkuCode = "notSelectedSku";

		ShoppingItemDto rootDto = createShoppingItemDtoForBundle(skuCodeInShoppingItemDto);

		Product constituentProduct = createOnlyAvailableWhenInStockProductWithSkus(skuCodeInShoppingItemDto, anotherSkuCode);
		ProductBundle rootBundle = createBundleWithConstituentProduct(constituentProduct);

		Collection<String> skuCodes = listSkuCodesForInventoryLookup(rootBundle);
		Map<String, Inventory> inventoryMap = assignInventoryToSku(anotherSkuCode);

		ProductInventoryManagementServiceImpl pims = mockInventoryLookupInPIMS(skuCodes, inventoryMap);
		productInventoryShoppingService.setProductInventoryManagementService(pims);

		Store store = setUpStoreWithWarehouse();

		SkuInventoryDetails inventoryDetails = productInventoryShoppingService.getSkuInventoryDetails(
				rootBundle.getDefaultSku(), store, rootDto);

		assertEquals("The bundle should be out of stock.", InventoryMessage.OUT_OF_STOCK, inventoryDetails.getMessageCode());
	}

	/**
	 * Test minOrderQuantity of products in bundles are ignored by addInventoryRequirementsToMap.
	 */
	@Test
	public void ensureAddInventoryRequirementsToMapIgnoresMinOrderQuantityOfConstituentProductInBundle() {
		Product product = createOnlyAvailableWhenInStockProductWithSkus(SKU_CODE);
		product.setMinOrderQty(2);

		ProductBundle bundle = createBundleWithConstituentProduct(product);

		int bundleQuantityRequired = 1;
		HashMap <String, Integer> inventoryRequirementsMap = new HashMap<>();
		productInventoryShoppingService.addInventoryRequirementsToMap(bundle, inventoryRequirementsMap, bundleQuantityRequired, true);

		assertEquals("The product's minOrderQuantity should be ignored.", Integer.valueOf(1), inventoryRequirementsMap.get(SKU_CODE));
	}

	/**
	 * Test minOrderQuantity of products in bundles are ignored by getMessageCode.
	 */
	@Test
	public void ensureMessageCodeIgnoresMinOrderQuantityOfConstituentProductInBundle() {
		Product product = createOnlyAvailableWhenInStockProductWithSkus(SKU_CODE);
		product.setMinOrderQty(2);

		NodeInventory nodeInventory = new NodeInventory(1, 1, new Date());
		boolean inBundle = true;
		InventoryMessage message = productInventoryShoppingService.getMessageCode(product.getSkuByCode(SKU_CODE), nodeInventory, inBundle, 0);

		assertEquals("The sku should be in stock.", InventoryMessage.IN_STOCK, message);
	}

	/**
	 * Test getMessageCode takes product minOrderQuantity into account when product is not in a bundle.
	 */
	@Test
	public void ensureMessageCodeChecksMinOrderQtyWhenNotInBundle() {
		Product product = createOnlyAvailableWhenInStockProductWithSkus(SKU_CODE);
		product.setMinOrderQty(2);

		NodeInventory nodeInventory = new NodeInventory(1, 1, new Date());
		boolean inBundle = false;
		InventoryMessage message = productInventoryShoppingService.getMessageCode(product.getSkuByCode(SKU_CODE), nodeInventory, inBundle, 0);

		assertEquals("The sku should be out of stock.", InventoryMessage.OUT_OF_STOCK_WITH_RESTOCK_DATE, message);
	}

	/**
	 * Test that the larger value between minOrderQuantity and the sum of the current required amount plus
	 * the additional quantity required is returned as the required sku quanitity.
	 *
	 * The minimum order quantity should be considered for products and skus not in a bundle.
	 */
	@Test
	public void ensureLargerValueIsReturnedByGetRequiredSkuQuantity() {
		int productMinOrderQuantity = 2;
		int currentRequiredAmount = 1;
		int additionalQuantityRequired = THREE;

		assertEquals("Required sku quantity should be the currentRequiredAmount plus the additionalQuantityRequired.", FOUR,
				productInventoryShoppingService.getRequiredSkuQuantity(productMinOrderQuantity, currentRequiredAmount, additionalQuantityRequired,
						false));

		productMinOrderQuantity = FIVE;

		assertEquals("Required sku quantity should be the productMinOrderQuantity.", FIVE, productInventoryShoppingService.getRequiredSkuQuantity(
				productMinOrderQuantity, currentRequiredAmount, additionalQuantityRequired, false));
	}

	/**
	 * Test that the larger value between minOrderQuantity and the sum of the current required amount plus
	 * the additional quantity required is returned as the required sku quanitity.
	 *
	 * The product minimum order quantity should be ignored for bundle constituents.
	 */
	@Test
	public void ensureGetRequiredSkuQuantityIgnoresMinOrderQuantityWhenInBundle() {
		int productMinOrderQuantity = 2;
		int currentRequiredAmount = 1;
		int additionalQuantityRequired = THREE;

		assertEquals("Required sku quantity should be the currentRequiredAmount plus the additionalQuantityRequired.", FOUR,
				productInventoryShoppingService.getRequiredSkuQuantity(productMinOrderQuantity, currentRequiredAmount, additionalQuantityRequired,
						true));

		productMinOrderQuantity = FIVE;

		assertEquals("Required sku quantity should be the currentRequiredAmount plus the additionalQuantityRequired.", FOUR,
				productInventoryShoppingService.getRequiredSkuQuantity(productMinOrderQuantity, currentRequiredAmount, additionalQuantityRequired,
						true));
	}

	/**
	 * Tests that getSkuCodesForInventoryLookup, for a single sku product, returns one skuCode.
	 */
	@Test
	public void testGetSkuCodesForInventoryLookup1Sku() {
		Product product = createOnlyAvailableWhenInStockProductWithSkus(SKU_CODE);
		Set<String> resultSet = productInventoryShoppingService.getSkuCodesForInventoryLookup(product);
		assertEquals("1 sku code for the sole sku", 1, resultSet.size());
		assertEquals(SKU_CODE, resultSet.iterator().next());
	}

	/**
	 * Tests that getSkuCodesForInventoryLookup, for a bundle with three sku constituents, returns four skuCodes (the constituents plus the
	 * bundle's root sku).
	 */
	@Test
	public void testGetSkuCodesForInventoryLookupBundle1Level() {
		String constituentSkuCode1 = "Constituent_1";
		String constituentSkuCode2 = "Constituent_2";
		String constituentSkuCode3 = "Constituent_3";

		ProductBundle bundle = createABundleWithThreeConstituents(constituentSkuCode1, constituentSkuCode2, constituentSkuCode3);

		Set<String> resultSet = productInventoryShoppingService.getSkuCodesForInventoryLookup(bundle);
		assertEquals("4 sku codes (1 for root + 1 for each child", FOUR, resultSet.size());
		assertTrue("Contains root sku code", resultSet.contains(BUNDLE_ROOT_SKU));
		assertTrue("Contains constituent1", resultSet.contains(constituentSkuCode1));
		assertTrue("Contains constituent2", resultSet.contains(constituentSkuCode2));
		assertTrue("Contains constituent3", resultSet.contains(constituentSkuCode3));
	}

	/**
	 * Tests that getSkuCodesForInventoryLookup, for a bundle with two sku constituents, one available when in stock and the other always available.
	 * SKU codes from always available products will never be added to the set, thus inventory will not be queried and the overall performance
	 * will be increased.
	 *
	 * The test asserts whether bundle's and first (available-when-in-stock) constituent's SKUs are in the returned set.
	 */
	@Test
	public void testGetSkuCodesForInventoryLookupBundleOneLevelTwoConstituentsOneIsAlwaysAvailable() {
		String constituentSkuCode1 = "Constituent_1";
		String constituentSkuCode2 = "Constituent_2";

		ProductBundle bundle = createABundleWithTwoMixedConstituents(constituentSkuCode1, constituentSkuCode2);

		Set<String> resultSet = productInventoryShoppingService.getSkuCodesForInventoryLookup(bundle);
		assertEquals("2 sku codes (1 for root + 1 for available-when-in-stock child", TWO, resultSet.size());
		assertTrue("Must contain root sku code", resultSet.contains(BUNDLE_ROOT_SKU));
		assertTrue("Must contain constituent1", resultSet.contains(constituentSkuCode1));
		assertFalse("Must not contain constituent2", resultSet.contains(constituentSkuCode2));
	}

	/**
	 * Tests that getSkuCodesForInventoryLookup, for a bundle with 2 levels, gets all the skus in all levels.
	 */
	@Test
	public void testGetSkuCodesForInventoryLookupBundle2Levels() {
		Product leafProduct = new ProductImpl();
		ProductSku leafProductSku = createProductSku("level_2", "level_2");
		leafProduct.addOrUpdateSku(leafProductSku);
		leafProduct.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		ProductBundle nestedBundle = createBundleWithConstituentProduct(leafProduct);
		nestedBundle.getDefaultSku().setSkuCode("level_1");
		nestedBundle.getDefaultSku().setGuid("level_1");
		ProductBundle rootBundle = createBundleWithConstituentProduct(nestedBundle);

		Set<String> resultSet = productInventoryShoppingService.getSkuCodesForInventoryLookup(rootBundle);
		assertEquals("3 sku codes (1 for root + 1 for each descendant", THREE, resultSet.size());
		assertTrue("Contains root sku code", resultSet.contains(BUNDLE_ROOT_SKU));
		assertTrue("Contains constituent1", resultSet.contains("level_1"));
		assertTrue("Contains constituent2", resultSet.contains("level_2"));
	}

	/**
	 * Test for when there is no products for getSkuInventoryDetailsForAllSkus.
	 */
	@Test
	public void testGetSkuInventoryDetailsForAllSkusNoProducts() {
		List<Product> products = new ArrayList<>();

		Set<String> skuCodes = Collections.emptySet();
		Map<String, Inventory> inventoryMap = new HashMap<>();
		ProductInventoryManagementServiceImpl pims = mockInventoryLookupInPIMS(skuCodes, inventoryMap);
		productInventoryShoppingService.setProductInventoryManagementService(pims);

		Store store = setUpStoreWithWarehouse();
		Map<String, Map<String, SkuInventoryDetails>> productSkuInventoryMap = productInventoryShoppingService.getSkuInventoryDetailsForAllSkus(
				products, store);
		assertEquals("There should be 0 sku codes in the productSkuInventoryMap.", 0, productSkuInventoryMap.keySet().size());
	}

	/**
	 * Test for getSkuInventoryDetailsForAllSkus when there are multiple different products being looked up.
	 */
	@Test
	public void testGetSkuInventoryDetailsForAllSkusSomeProducts() {
		Product product1 = createOnlyAvailableWhenInStockProductWithSkus("sku1");
		product1.setCode("code1");

		Product product2 = createOnlyAvailableWhenInStockProductWithSkus("sku2");
		product2.setCode("code2");

		Product product3 = createOnlyAvailableWhenInStockProductWithSkus("sku3", "sku4");
		product3.setCode("code3");
		List<Product> products = Arrays.asList(product1, product2, product3);

		Set<String> skuCodes = new HashSet<>(Arrays.asList("sku1", "sku2", "sku3", "sku4"));
		Map<String, Inventory> inventoryMap = new HashMap<>();
		ProductInventoryManagementServiceImpl pims = mockInventoryLookupInPIMS(skuCodes, inventoryMap);
		productInventoryShoppingService.setProductInventoryManagementService(pims);

		final Store store = setUpStoreWithWarehouse();

		Map<String, Map<String, SkuInventoryDetails>> productSkuInventoryMap = productInventoryShoppingService.getSkuInventoryDetailsForAllSkus(
				products, store);
		assertEquals("3 sku code to SkuInventoryDetail map for all products", THREE, productSkuInventoryMap.keySet().size());
		assertEquals("1 sku code to SkuInventoryDetail map for product with code code1", 1, productSkuInventoryMap.get("code1").keySet().size());
		assertNotNull("Inventory details for product with code code1 and sku sku1 not be null", productSkuInventoryMap.get("code1").get("sku1"));
		assertEquals("1 sku code to SkuInventoryDetail map for product with code code2", 1, productSkuInventoryMap.get("code2").keySet().size());
		assertNotNull("Inventory details for product with code code2 and sku sku1 not be null", productSkuInventoryMap.get("code2").get("sku2"));
		assertEquals("2 sku code to SkuInventoryDetail map for product with code code3", 2, productSkuInventoryMap.get("code3").keySet().size());
		assertNotNull("Inventory details for product with code code3 and sku sku4 not be null", productSkuInventoryMap.get("code3").get("sku4"));
	}



	private ProductBundle createABundleWithThreeConstituents(final String constituentSkuCode1, final String constituentSkuCode2,
			final String constituentSkuCode3) {
		ProductBundle bundle = new ProductBundleImpl();
		ProductSku bundleSku = createProductSku(BUNDLE_ROOT_SKU, BUNDLE_ROOT_SKU);
		bundle.addOrUpdateSku(bundleSku);

		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(createOnlyAvailableWhenInStockProductWithSkus(constituentSkuCode1));
		bundle.addConstituent(constituent1);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(createOnlyAvailableWhenInStockProductWithSkus(constituentSkuCode2));
		bundle.addConstituent(constituent2);

		BundleConstituent constituent3 = new BundleConstituentImpl();
		constituent3.setConstituent(createOnlyAvailableWhenInStockProductWithSkus(constituentSkuCode3));
		bundle.addConstituent(constituent3);
		return bundle;
	}

	//create a bundle with 2 constituents of different availability criteria - one is available when in stock, the other is always available
	private ProductBundle createABundleWithTwoMixedConstituents(final String constituentSkuCode1, final String constituentSkuCode2) {
		ProductBundle bundle = new ProductBundleImpl();
		ProductSku bundleSku = createProductSku(BUNDLE_ROOT_SKU, BUNDLE_ROOT_SKU);
		bundle.addOrUpdateSku(bundleSku);

		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(createProductWithSkus(false, constituentSkuCode1));
		bundle.addConstituent(constituent1);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(createProductWithSkus(true, constituentSkuCode2));
		bundle.addConstituent(constituent2);

		return bundle;
	}

	private Product createOnlyAvailableWhenInStockProductWithSkus(final String... skuCodes) {
		return createProductWithSkus(false, skuCodes);
	}

	private Product createProductWithSkus(final boolean isAlwaysAvailable, final String... skuCodes) {
		final Product product = isAlwaysAvailable ? createAlwaysAvailableProduct() : createOnlyAvailableWhenInStockProduct();
		for (String skuCode : skuCodes) {
			ProductSku sku = createProductSku(skuCode, skuCode);
			product.addOrUpdateSku(sku);
		}
		return product;
	}

	private ProductBundle createBundleWithConstituentProduct(final Product constituentProduct) {
		ProductSku bundleSku = createProductSku(BUNDLE_ROOT_SKU, BUNDLE_ROOT_SKU);
		ProductBundle bundle = new ProductBundleImpl();
		bundleSku.setProduct(bundle);

		BundleConstituent childConstituent = new BundleConstituentImpl();
		childConstituent.setConstituent(constituentProduct);
		childConstituent.setQuantity(1);
		bundle.addConstituent(childConstituent);

		return bundle;
	}

	private Store setUpStoreWithWarehouse() {
		Warehouse warehouse = new WarehouseImpl();
		warehouse.setUidPk(WAREHOUSE_UIDPK);
		List<Warehouse> warehouses = new ArrayList<>();
		warehouses.add(warehouse);

		Store store = new StoreImpl();
		store.setWarehouses(warehouses);
		return store;
	}

	private ProductSku createProductSku(final String skuCode, final String skuGuid) {
		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		sku.setGuid(skuGuid);
		return sku;
	}

	private Inventory createInventoryWithQuantityOfOne() {
		Inventory inventory = new InventoryImpl();
		inventory.setWarehouseUid(WAREHOUSE_UIDPK);
		inventory.setQuantityOnHand(1);
		return inventory;
	}

	private Product createOnlyAvailableWhenInStockProduct() {
		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		return product;
	}

	private Product createAlwaysAvailableProduct() {
		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		return product;
	}

	private Map<String, Inventory> assignInventoryToSku(final String skuWithInventory) {
		Map<String, Inventory> skuInventories = new HashMap<>();
		Inventory inventory = createInventoryWithQuantityOfOne();
		skuInventories.put(skuWithInventory, inventory);
		return skuInventories;
	}

	private ShoppingItemDto createShoppingItemDtoForBundle(final String skuCodeInShoppingItemDto) {
		ShoppingItemDto rootDto = new ShoppingItemDto(BUNDLE_ROOT_SKU, 1);
		ShoppingItemDto childDto = new ShoppingItemDto(skuCodeInShoppingItemDto, 1);
		rootDto.addConstituent(childDto);
		return rootDto;
	}

	private Set<String> listSkuCodesForInventoryLookup(final Product product) {
		final Set<String> skuCodesForInventoryLookup = new HashSet<>();

		if (product instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) product;
			for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
				ConstituentItem constituent = bundleConstituent.getConstituent();
				if (constituent.isProductSku()) {
					skuCodesForInventoryLookup.add(constituent.getCode());
				} else {
					Set<String> constituentSet = listSkuCodesForInventoryLookup(constituent.getProduct());
					skuCodesForInventoryLookup.addAll(constituentSet);
				}
			}
		}

		for (ProductSku sku : product.getProductSkus().values()) {
			skuCodesForInventoryLookup.add(sku.getSkuCode());
		}
		return skuCodesForInventoryLookup;
	}

	private ProductInventoryManagementServiceImpl mockInventoryLookupInPIMS(final Collection<String> skuCodesForInventoryLookup,
			final Map<String, Inventory> inventoryMap) {

		final Map<String, InventoryJournalRollup> rollupMap = new HashMap<>();
		for (String skuCode  : skuCodesForInventoryLookup) {
			rollupMap.put(skuCode, new InventoryJournalRollupImpl());
		}

		final InventoryDao inventoryDao = context.mock(InventoryDao.class);
		final InventoryJournalDao inventoryJournalDao = context.mock(InventoryJournalDao.class);
		final ProductSkuService productSkuService = context.mock(ProductSkuService.class);

		context.checking(new Expectations() {
			{
				atMost(1).of(inventoryDao).getInventoryMap(skuCodesForInventoryLookup, WAREHOUSE_UIDPK);
				will(returnValue(inventoryMap));

				allowing(inventoryJournalDao).getRollup(with(any(InventoryKey.class)));
				will(returnValue(new InventoryJournalRollupImpl()));

				allowing(inventoryJournalDao).getInventoryRollupsForSkusInWarehouse(new HashSet<>(skuCodesForInventoryLookup), WAREHOUSE_UIDPK);
				will(returnValue(rollupMap));
			}
		});

		return createPIMS(inventoryDao, inventoryJournalDao, productSkuService);
	}

	private ProductInventoryManagementServiceImpl createPIMS(final InventoryDao inventoryDao,
			final InventoryJournalDao inventoryJournalDao,
			final ProductSkuService productSkuService) {

		JournalingInventoryStrategy journalingInventoryStrategy = new JournalingInventoryStrategy();
		journalingInventoryStrategy.setInventoryDao(inventoryDao);
		journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao);
		journalingInventoryStrategy.setProductSkuService(productSkuService);

		InventoryFacadeImpl inventoryFacade = new InventoryFacadeImpl();
		Map<String, InventoryStrategy> strategies = new HashMap<>();
		strategies.put("allocatedjournaling", journalingInventoryStrategy);
		inventoryFacade.setStrategies(strategies);
		inventoryFacade.selectStrategy("allocatedjournaling");

		ProductInventoryManagementServiceImpl productInventoryManagementService = new ProductInventoryManagementServiceImpl();
		productInventoryManagementService.setInventoryFacade(inventoryFacade);
		return productInventoryManagementService;
	}
}
