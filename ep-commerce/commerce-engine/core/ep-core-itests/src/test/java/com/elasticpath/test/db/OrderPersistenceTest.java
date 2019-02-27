/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.money.Money;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests order persisting.
 */
public class OrderPersistenceTest extends DbTestCase {

	private static final Currency CURRENCY_USD = Currency.getInstance("USD");

	/**
	 * Tests order saving.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveOrder() {
		final Order order = createSimpleOrder();

		final Order savedOrder = persistOrder(order);

		assertTrue(savedOrder.isPersisted());
		assertSame("Saved order not the same object.", order, savedOrder);
	}
	
	/**
	 * Tests order retrieving.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveOrder() {
		final Order order = createSimpleOrder();

		persistOrder(order);

		assertNotNull("Order not retrieved - null", getPersistenceEngine().get(OrderImpl.class, order.getUidPk()));
	}
	
	/**
	 * Tests order deleting.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteOrder() {
		final Order order = createSimpleOrder();

		persistOrder(order);

		getTxTemplate().execute(new TransactionCallback<Order>() {
			@Override
			public Order doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().delete(order);
				return null;
			}
		});

		final Order retrievedOrder = getPersistenceEngine().get(OrderImpl.class, order.getUidPk());

		assertNull("Saved order not the same object.", retrievedOrder);
	}
	
	/**
	 * Tests order merging.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeOrder() {
		final Order order = createSimpleOrder();

		persistOrder(order);

		final Order retrievedOrder = getPersistenceEngine().get(OrderImpl.class, order.getUidPk());

		final Order mergedOrder = getTxTemplate().execute(new TransactionCallback<Order>() {
			@Override
			public Order doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().merge(retrievedOrder);
			}
		});

		assertNotSame("Merged order is the same object.", retrievedOrder, mergedOrder);
	}
	
	/**
	 * Tests cascade saving operation.
	 */
	@DirtiesDatabase
	@Test
	public void testSimpleCascadeSave() {
		final Order order = createOrderWithAddress();

		final Order savedOrder = persistOrder(order);

		assertTrue("Order Address not saved by cascade.", savedOrder.getBillingAddress().getUidPk() > 0);
	}
	
	/**
	 * Tests that loaded fields are still the same after merging. 
	 */
	@DirtiesDatabase
	@Test
	public void testLoadedFieldSameAfterMerge() {
		final Order order = createOrderWithAddress();

		persistOrder(order);

		final Order retrievedOrder = getPersistenceEngine().get(OrderImpl.class, order.getUidPk());

		final Order mergedOrder = getTxTemplate().execute(new TransactionCallback<Order>() {
			@Override
			public Order doInTransaction(final TransactionStatus arg0) {
				return getPersistenceEngine().merge(retrievedOrder);
			}
		});

		assertNotNull("Order billing address lost during merge.", mergedOrder.getBillingAddress());
		assertEquals("Order billing address has changed after merge.", mergedOrder.getBillingAddress(), retrievedOrder.getBillingAddress());
	}
	
	/**
	 * Tests that shipment skus are still the same after merging.
	 */
	@DirtiesDatabase
	@Test
	public void testShipmentSkusSameAfterMerge() {
		final Order order = persistOrder(createFullOrder());

		final OrderShipment orderShipment = createOrderShipment();
		order.addShipment(orderShipment);

		final Order savedOrder = updateOrder(order);

		assertNotNull(savedOrder.getAllShipments());
		assertFalse(savedOrder.getAllShipments().isEmpty());

		assertNotNull(savedOrder.getAllShipments().get(0).getShipmentOrderSkus());
		assertFalse(savedOrder.getAllShipments().get(0).getShipmentOrderSkus().isEmpty());

		final Order retrievedOrder = getPersistenceEngine().get(OrderImpl.class, order.getUidPk());

		assertNotNull(retrievedOrder.getAllShipments());
		assertFalse(retrievedOrder.getAllShipments().isEmpty());

		assertNotNull(retrievedOrder.getAllShipments().get(0).getShipmentOrderSkus());
		assertFalse(retrievedOrder.getAllShipments().get(0).getShipmentOrderSkus().isEmpty());

		final OrderSku preMergeOrderSku = retrievedOrder.getAllShipments().get(0).getShipmentOrderSkus().iterator().next();

		final Order mergedOrder = updateOrder(retrievedOrder);

		final OrderSku mergedOrderSku = mergedOrder.getAllShipments().get(0).getShipmentOrderSkus().iterator().next();
		assertNotNull("Order skus lost during merge.", mergedOrderSku);
		assertTrue("Order sku has changed after merge.", areSame(mergedOrderSku, preMergeOrderSku));
	}
	
	private Order updateOrder(final Order order) throws TransactionException {
		return getTxTemplate().execute(new TransactionCallback<Order>() {
			@Override
			public Order doInTransaction(final TransactionStatus arg0) {
				Order result = getPersistenceEngine().merge(order);
				return result;
			}
		});
	}
	
	/**
	 * Tests order saving.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveOrderChanges() {
		final Order order = persistOrder(createFullOrder());

		final OrderShipment orderShipment = createOrderShipment();
		order.addShipment(orderShipment);

		final Order savedOrder = updateOrder(order);

		assertTrue(savedOrder.isPersisted());
		assertEquals(savedOrder.getAllShipments().size(), 1);
		assertTrue(savedOrder.getAllShipments().get(0).isPersisted());

		OrderAddress address = savedOrder.getBillingAddress();
		address.setFirstName("Test");

		final Order updatedOrder = updateOrder(savedOrder);

		assertTrue(updatedOrder.isPersisted());
		assertEquals(updatedOrder.getAllShipments().size(), 1);
		assertTrue(updatedOrder.getAllShipments().get(0).isPersisted());
		assertEquals(updatedOrder.getBillingAddress().getFirstName(), "Test");

	}
	
	/**
	 * Tests multi item order saving.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveMultiItemOrder() {
		createFullOrder();
	}

	private Order persistOrder(final Order order) throws TransactionException {
		return getTxTemplate().execute(new TransactionCallback<Order>() {
			@Override
			public Order doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(order);
				return order;
			}
		});
	}
	
	private boolean areSame(final OrderSku mergedOrderSku, final OrderSku preMergeOrderSku) {
		return mergedOrderSku.getUidPk() == preMergeOrderSku.getUidPk();
	}

	private Order createSimpleOrder() {
		final Order order = new OrderImpl();
		Date orderDate = new Date();
		order.setCreatedDate(orderDate);
		order.setLocale(Locale.ENGLISH);
		order.setCurrency(CURRENCY_USD);
		order.setStoreCode(scenario.getStore().getCode());
		order.setLastModifiedDate(orderDate);
		return order;
	}

	private Order createOrderWithAddress() {
		final Order order = createSimpleOrder();
		final OrderAddressImpl billingAddress = new OrderAddressImpl();
		billingAddress.initialize();
		order.setBillingAddress(billingAddress);
		return order;
	}

	private Order createFullOrder() {
		final Order order = createOrderWithAddress();
		final Customer customer = createCustomer();
		order.setCustomer(customer);
		return order;
	}

	private Customer createCustomer() {
		return getTac().getPersistersFactory().getStoreTestPersister().createDefaultCustomer(scenario.getStore());
	}

	private OrderShipment createOrderShipment() {
		final PhysicalOrderShipmentImpl orderShipment = new PhysicalOrderShipmentImpl();
		orderShipment.initialize();
		Date shipmentDate = new Date();
		orderShipment.setCreatedDate(shipmentDate);
		orderShipment.addShipmentOrderSku(getSimpleOrderSku("testsku", "GOODS"));
		return orderShipment;
	}
	
	private OrderSku getSimpleOrderSku(final String orderSkuCode, final String taxCode) {
	
		Product newProduct = getTac().getPersistersFactory().getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(),
				scenario.getCategory(), 
				scenario.getWarehouse());
		final ProductSku productSku = newProduct.getDefaultSku();
		
		
		final OrderSku orderSku = getBeanFactory().getBean(ContextIdNames.ORDER_SKU);
		orderSku.initialize();
		orderSku.setOrdering(0);

		final Price price = getBeanFactory().getBean(ContextIdNames.PRICE);
		final Money amount = Money.valueOf(BigDecimal.ONE, Currency.getInstance("USD"));
		price.setListPrice(amount);
		orderSku.setPrice(1, price);
		orderSku.setUnitPrice(BigDecimal.ONE);
		
		final Date now = new Date();
		orderSku.setCreatedDate(now);
		
		final int qty = 1;
		orderSku.setQuantity(qty);
		orderSku.setSkuCode(orderSkuCode);
		orderSku.setTaxCode(taxCode);
		orderSku.setTaxAmount(BigDecimal.ONE);
		orderSku.setDisplayName("name_" + orderSkuCode);
		
		orderSku.setSkuGuid(productSku.getGuid());
		
		return orderSku;
	}
	
}
