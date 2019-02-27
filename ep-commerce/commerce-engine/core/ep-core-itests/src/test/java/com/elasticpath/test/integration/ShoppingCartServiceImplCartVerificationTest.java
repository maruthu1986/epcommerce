/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.integration.cart.AbstractCartIntegrationTestParent;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * An integration test for ShoppingCartService.  We are testing service calls that verify certain properties of a shopping cart.
 */
public class ShoppingCartServiceImplCartVerificationTest extends AbstractCartIntegrationTestParent {

	private static final String NONEXISTENT_GUID = "NONEXISTENT_GUID";
	private static final String NONEXISTENT_STORECODE = "NONEXISTENT_STORECODE";
	@Autowired
	private ShoppingCartService shoppingCartService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductSkuService productSkuService;
	@Autowired
	private CartDirector cartDirector;
	@Autowired
	private TestDataPersisterFactory persisterFactory;
	private final Locale DEFAULT_LOCALE = Locale.US;
	
	@DirtiesDatabase
	@Test
	public void testShoppingCartExists() {
		CustomerSession customerSession = createCustomerSession();
		ShoppingCart shoppingCart = createShoppingCart(customerSession);
		ShoppingCart updatedCart = shoppingCartService.saveOrUpdate(shoppingCart);
		assertTrue("The shoppingCart should exist.", shoppingCartService.shoppingCartExists(updatedCart.getGuid()));
	}

	@DirtiesDatabase
	@Test
	public void testShoppingCartExistsForNonExistentCart() {
		assertFalse("The shoppingCart should not exist.", shoppingCartService.shoppingCartExists(NONEXISTENT_GUID));
	}
	
	@DirtiesDatabase
	@Test
	public void testShoppingCartExistsForStore() {
		Store store = ((SimpleStoreScenario)getTac().getScenario(SimpleStoreScenario.class)).getStore();
		CustomerSession customerSession = createCustomerSession();
		ShoppingCart shoppingCart = createShoppingCart(customerSession);
		ShoppingCart updatedCart = shoppingCartService.saveOrUpdate(shoppingCart);
		assertTrue("The shoppingCart with store code should exist.", 
				shoppingCartService.shoppingCartExistsForStore(updatedCart.getGuid(), store.getCode()));
	}

	@DirtiesDatabase
	@Test
	public void expectShoppingCartExistsForStoreCodeWithCaseInsensitivity() {
		Store store = ((SimpleStoreScenario)getTac().getScenario(SimpleStoreScenario.class)).getStore();
		CustomerSession customerSession = createCustomerSession();
		ShoppingCart shoppingCart = createShoppingCart(customerSession);
		ShoppingCart updatedCart = shoppingCartService.saveOrUpdate(shoppingCart);
		String caseDifferingStoreCode = createCaseDifferingStoreCode(store.getCode());
		assertTrue("The shoppingCart with store code should exist.", 
				shoppingCartService.shoppingCartExistsForStore(updatedCart.getGuid(), caseDifferingStoreCode));
	}
	
	@DirtiesDatabase	
	@Test
	public void testShoppingCartExistsForStoreWithNonExistentShoppingCartGuid() {
		Store store = ((SimpleStoreScenario)getTac().getScenario(SimpleStoreScenario.class)).getStore();
		assertFalse("The shoppingCart with store code should not exist.", 
				shoppingCartService.shoppingCartExistsForStore(NONEXISTENT_GUID, store.getCode()));
	}
	
	@DirtiesDatabase
	@Test
	public void testShoppingCartExistsForStoreWithNonExistentStoreCode() {
		CustomerSession customerSession = createCustomerSession();
		ShoppingCart shoppingCart = createShoppingCart(customerSession);
		ShoppingCart updatedCart = shoppingCartService.saveOrUpdate(shoppingCart);
		assertFalse("The shoppingCart with store code should not exist.", 
				shoppingCartService.shoppingCartExistsForStore(updatedCart.getGuid(), NONEXISTENT_STORECODE));
	}
	
	private String createCaseDifferingStoreCode(final String code) {
		String result = code.toLowerCase(DEFAULT_LOCALE);
		if (result.equals(code)) {
			result = code.toUpperCase(DEFAULT_LOCALE);
		}
		return result;
	}

	private ProductBundle createTestProductBundle() {
		CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();
		ProductBundle productBundle = catalogTestPersister.createSimpleProductBundle("testtype", "testbundle", 
				getScenario().getCatalog(),	getScenario().getCategory(), 
				persisterFactory.getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
		final Product product = catalogTestPersister.createDefaultProductWithSkuAndInventory(getScenario().getCatalog(),
				getScenario().getCategory(), getScenario().getWarehouse());

		productBundle.addConstituent(catalogTestPersister.createSimpleBundleConstituent(product, 1));
		
		productBundle.setCalculated(true);
		final ProductSkuImpl bundleSku = new ProductSkuImpl();
		bundleSku.setStartDate(new Date());
		bundleSku.setSkuCode("testbundleSku");
		bundleSku.setDefaultValues();
		productBundle.addOrUpdateSku(bundleSku);
		productService.saveOrUpdate(productBundle);
		
		return productBundle;
	}
	
	
	private void addSkuToCart(final ShoppingCart shoppingCart, final String skuCode, final int quantity) {
		final ShoppingItemDto dto = new ShoppingItemDto(skuCode, quantity);
		cartDirector.addItemToCart(shoppingCart, dto);
	}
}
