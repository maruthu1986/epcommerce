/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.tax;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.misc.PropertyBased;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.OrderReturnImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration test for TaxOperationService.
 */
public class ReturnTaxOperationTest extends AbstractBasicTaxOperationTest {

	@Autowired
	private ReturnAndExchangeService returnAndExchangeService;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Override
	@Before
	public void setUp() {
		super.setUp();
		
		setupPropertyForReason();
	}
	
	/**
	 * setupPropertyForReason.
	 */
	private void setupPropertyForReason() {
		final PropertyBased propertyBased = getBeanFactory().getBean(ContextIdNames.ORDER_RETURN_SKU_REASON);

		Map<String, Properties> propertiesMap = new HashMap<>();

		Properties value = new Properties();
		value.put("OrderReturnSkuReason_UnwantedGift", "Unwanted Gift");
		value.put("OrderReturnSkuReason_IncorrectItem", "Incorrect Item");
		value.put("OrderReturnSkuReason_Faulty", "Faulty");
		propertiesMap.put("orderReturnSkuReason.properties", value);

		propertyBased.setPropertiesMap(propertiesMap);
	}

	/**
	 * Test creating an order return for an order shipment.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateOrderReturn() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());
		
		ShoppingItemDto physicalDto = new ShoppingItemDto(shippableProducts.get(0).getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, physicalDto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new PaymentTokenImpl.TokenBuilder()
				.build());

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		// checkout
		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		Order order = ordersList.iterator().next();

		// one shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals(1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		assertEquals(order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);
		
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
		phShipment.setStatus(OrderShipmentStatus.SHIPPED);
		orderService.update(order);
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		
		phShipment = order.getPhysicalShipments().iterator().next();
		OrderReturn orderReturn = createOrderShipmentReturn(order, phShipment);
		
		returnAndExchangeService.createShipmentReturn(
				orderReturn, 
				ReturnExchangeType.PHYSICAL_RETURN_REQUIRED, 
				orderReturn.getOrderShipmentForReturn(), 
				orderReturn.getOrder().getModifiedBy());
		
		verifyTaxDocumentForOrderReturn(orderReturn, store);
	}

	/**
	 * Test cancelling an order return for an order shipment.
	 */
	@DirtiesDatabase
	@Test
	public void testCancelOrderReturn() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());
		
		ShoppingItemDto physicalDto = new ShoppingItemDto(shippableProducts.get(0).getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, physicalDto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new PaymentTokenImpl.TokenBuilder()
				.build());

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		// checkout
		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		Order order = ordersList.iterator().next();

		// one shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals(1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		assertEquals(order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);
		
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
		phShipment.setStatus(OrderShipmentStatus.SHIPPED);
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		orderService.update(order);
		
		phShipment = order.getPhysicalShipments().iterator().next();
		
		OrderReturn orderReturn = createOrderShipmentReturn(order, phShipment);
		orderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		
		OrderReturn persistedOrderReturn = returnAndExchangeService.createShipmentReturn(
				orderReturn, 
				ReturnExchangeType.PHYSICAL_RETURN_REQUIRED, 
				orderReturn.getOrderShipmentForReturn(), 
				orderReturn.getOrder().getModifiedBy());
		
		verifyTaxDocumentForOrderReturn(persistedOrderReturn, store);
		
		// cancle the order return
		FetchGroupLoadTuner tuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		tuner.addFetchGroup(FetchGroupConstants.ORDER_INDEX, 
			FetchGroupConstants.ORDER_NOTES, 
			FetchGroupConstants.ALL);
		
		OrderReturn retrievedOrderReturn = returnAndExchangeService.get(persistedOrderReturn.getUidPk(), tuner);
		retrievedOrderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		OrderReturn cancelledOrderReturn = returnAndExchangeService.cancelReturnExchange(retrievedOrderReturn);
		
		verifyTaxDocumentReversal(cancelledOrderReturn.getTaxDocumentId());
	}
	
	/**
	 * Test cancelling an order return for an order shipment.
	 */
	@DirtiesDatabase
	@Test
	public void testEditOrderReturn() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());
		
		ShoppingItemDto physicalDto = new ShoppingItemDto(shippableProducts.get(0).getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, physicalDto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new PaymentTokenImpl.TokenBuilder()
				.build());

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		// checkout
		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		Order order = ordersList.iterator().next();

		// one shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals(1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		assertEquals(order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);
		
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
		phShipment.setStatus(OrderShipmentStatus.SHIPPED);
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		orderService.update(order);
		
		phShipment = order.getPhysicalShipments().iterator().next();
		
		OrderReturn orderReturn = createOrderShipmentReturn(order, phShipment);
		orderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		
		OrderReturn persistedOrderReturn = returnAndExchangeService.createShipmentReturn(
						orderReturn, 
						ReturnExchangeType.PHYSICAL_RETURN_REQUIRED, 
						orderReturn.getOrderShipmentForReturn(), 
						orderReturn.getOrder().getModifiedBy());
		
		verifyTaxDocumentForOrderReturn(persistedOrderReturn, store);
		
		// cancle the order return
		FetchGroupLoadTuner tuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		tuner.addFetchGroup(FetchGroupConstants.ORDER_INDEX, 
			FetchGroupConstants.ORDER_NOTES, 
			FetchGroupConstants.ALL);
		
		OrderReturn retrievedOrderReturn = returnAndExchangeService.get(persistedOrderReturn.getUidPk(), tuner);
		retrievedOrderReturn.setShipmentDiscount(BigDecimal.TEN);
		retrievedOrderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		retrievedOrderReturn.recalculateOrderReturn();
		
		OrderReturn modifiedOrderReturn = returnAndExchangeService.editReturn(retrievedOrderReturn);
		
		verifyTaxDocumentReversal(retrievedOrderReturn.getTaxDocumentId());
		
		modifiedOrderReturn.recalculateOrderReturn();
		verifyTaxDocumentForOrderReturn(modifiedOrderReturn, store);
	}

	private OrderReturn createOrderShipmentReturn(final Order order,
			final PhysicalOrderShipment phShipment) {
		OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.populateOrderReturn(order, phShipment, OrderReturnType.RETURN);
		orderReturn.setOrderReturnAddress(phShipment.getShipmentAddress());
		
		for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
			orderReturnSku.setQuantity(orderReturnSku.getOrderSku().getQuantity());
			orderReturnSku.setReturnReason("Faulty");
			orderReturnSku.setReturnAmount(orderReturnSku.getAmountMoney().getAmount());
		}
		orderReturn.setShippingCost(phShipment.getShippingCost());
		orderReturn.recalculateOrderReturn();
		orderReturn.setReturnStatus(OrderReturnStatus.AWAITING_STOCK_RETURN);
		return orderReturn;
	}
}
