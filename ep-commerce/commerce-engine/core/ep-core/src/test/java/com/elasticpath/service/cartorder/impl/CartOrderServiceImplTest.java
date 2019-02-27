/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.cartorder.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.cartorder.CartOrderPopulationStrategy;
import com.elasticpath.service.cartorder.CartOrderShippingInformationSanitizer;
import com.elasticpath.service.cartorder.dao.CartOrderDao;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * New JUnit4 tests for {@code CartOrderServiceImplTest}.
 */
public class CartOrderServiceImplTest {

	private static final String NAMED_QUERY_CART_ORDER_GUID_BY_SHOPPING_CART_GUID = "CART_ORDER_GUID_BY_SHOPPING_CART_GUID";
	private static final String NAMED_QUERY_ACTIVE_CART_ORDER_BY_STORECODE_AND_GUID = "ACTIVE_CART_ORDER_BY_STORECODE_AND_GUID";
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String STORE_CODE = "storeCode";
	private static final String CART_GUID = "CART_GUID";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CartOrderServiceImpl cartOrderService;
	private CartOrderPopulationStrategy strategy;
	private CartOrderDao cartOrderDao;
	private CartOrder cartOrder;
	private ShoppingCartService shoppingCartService;
	private CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer;
	private CustomerAddressDao addressDao;
	private PersistenceEngine persistenceEngine;
	private ShoppingCart shoppingCart;

	/**
	 * Mock required services and objects.
	 */
	@Before
	public void mockRequiredServicesAndObjects() {
		cartOrderService = new CartOrderServiceImpl();
		strategy = context.mock(CartOrderPopulationStrategy.class);
		cartOrderDao = context.mock(CartOrderDao.class);
		addressDao = context.mock(CustomerAddressDao.class);
		cartOrder = context.mock(CartOrder.class);
		shoppingCartService = context.mock(ShoppingCartService.class);
		cartOrderShippingInformationSanitizer = context.mock(CartOrderShippingInformationSanitizer.class);
		shoppingCart = context.mock(ShoppingCart.class);
		persistenceEngine = context.mock(PersistenceEngine.class);
		cartOrderService.setCartOrderPopulationStrategy(strategy);
		cartOrderService.setCartOrderDao(cartOrderDao);
		cartOrderService.setCustomerAddressDao(addressDao);
		cartOrderService.setShoppingCartService(shoppingCartService);
		cartOrderService.setCartOrderShippingInformationSanitizer(cartOrderShippingInformationSanitizer);
		cartOrderService.setPersistenceEngine(persistenceEngine);

	}

	/**
	 * Test expected response when a cart order does not exist.
	 */
	@Test
	public void testCreateIfNotExists() {
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(NAMED_QUERY_CART_ORDER_GUID_BY_SHOPPING_CART_GUID, CART_GUID);
				will(returnValue(Collections.emptyList()));
				oneOf(cartOrder).getShoppingCartGuid();
				will(returnValue(null));
				allowing(shoppingCart).getGuid();
				will(returnValue(CART_GUID));
				oneOf(strategy).createCartOrder(shoppingCart);
				will(returnValue(cartOrder));
				oneOf(cartOrderDao).saveOrUpdate(cartOrder);
			}
		});

		boolean response = cartOrderService.createOrderIfPossible(shoppingCart);
		assertTrue("Expect that cart order did not exist.", response);
	}

	/**
	 * Test expected response when a cart order exists.
	 */
	@Test
	public void testCartOrderExistsOnCreateIfNotExists() {
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(NAMED_QUERY_CART_ORDER_GUID_BY_SHOPPING_CART_GUID, CART_GUID);
				will(returnValue(Arrays.asList(CART_ORDER_GUID)));
				allowing(shoppingCart).getGuid();
				will(returnValue(CART_GUID));
			}
		});

		boolean response = cartOrderService.createOrderIfPossible(shoppingCart);
		assertFalse("Expect that cart order did exist.", response);
	}

	
	
	/**
	 * Ensure sanitation of retrieved cart order.
	 */
	@Test
	public void ensureSanitationAndPersistenceOfRetrievedCartOrder() {
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(NAMED_QUERY_ACTIVE_CART_ORDER_BY_STORECODE_AND_GUID, STORE_CODE, CART_ORDER_GUID);
				will(returnValue(Arrays.asList(cartOrder)));
				oneOf(cartOrderShippingInformationSanitizer).sanitize(cartOrder);
				will(returnValue(true));
				oneOf(cartOrderDao).saveOrUpdate(cartOrder);
			}
		});
		
		cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID);
	}
	
	/**
	 * Ensure sanitation of retrieved cart order.
	 */
	@Test
	public void ensureSanitationAndNoPersistenceOfUnchangedRetrievedCartOrder() {
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(NAMED_QUERY_ACTIVE_CART_ORDER_BY_STORECODE_AND_GUID, STORE_CODE, CART_ORDER_GUID);
				will(returnValue(Arrays.asList(cartOrder)));
				oneOf(cartOrderShippingInformationSanitizer).sanitize(cartOrder);
				will(returnValue(false));
			}
		});
		
		cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID);
	}
	
	/**
	 * Ensure non existent cart order not sanitized.
	 */
	@Test
	public void ensureNonExistentCartOrderNotSanitized() {
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(NAMED_QUERY_ACTIVE_CART_ORDER_BY_STORECODE_AND_GUID, STORE_CODE, CART_ORDER_GUID);
				will(returnValue(Collections.emptyList()));
				never(cartOrderShippingInformationSanitizer).sanitize(cartOrder);
			}
		});
		
		cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID);
	}
}
