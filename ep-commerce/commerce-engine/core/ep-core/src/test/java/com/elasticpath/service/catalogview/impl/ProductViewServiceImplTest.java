/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.StoreProductService;

/**
 * ProductViewServiceImpl unit tests.
 */
public class ProductViewServiceImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final ProductService productService = context.mock(ProductService.class);
	private final StoreProductService storeProductService = context.mock(StoreProductService.class);
	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
	private final Store store = context.mock(Store.class);
	
	private static final String TEST_PRODUCT_CODE = "test-product-code";
	
	private ProductViewServiceImpl service;
	private static final boolean LOAD_PRODUCT_ASSOCIATIONS = true;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		service = new ProductViewServiceImpl();
		service.setStoreProductService(storeProductService);
		service.setProductService(productService);
		context.checking(new Expectations() { {
			allowing(shoppingCart).getStore(); will(returnValue(store));
		} });
	}
	
	/**
	 * ProductService returning a product UIDPK of zero, returns null product.
	 */
	@Test
	public void testGetProductZeroProductUid() {
		context.checking(new Expectations() { {
			oneOf(productService).findUidById(TEST_PRODUCT_CODE); will(returnValue(0L));
		} });
		assertNull(service.getProduct(TEST_PRODUCT_CODE, shoppingCart, LOAD_PRODUCT_ASSOCIATIONS));
	}
	
	/**
	 * StoreProductService returning a NULL product, returns null product.
	 */
	@Test
	public void testGetProductNullProduct() {
		context.checking(new Expectations() { {
			oneOf(productService).findUidById(TEST_PRODUCT_CODE); will(returnValue(1L));
			oneOf(storeProductService).getProductForStore(1L, shoppingCart.getStore(), LOAD_PRODUCT_ASSOCIATIONS); will(returnValue(null));
		} });
		assertNull(service.getProduct(TEST_PRODUCT_CODE, shoppingCart, LOAD_PRODUCT_ASSOCIATIONS));
	}
}