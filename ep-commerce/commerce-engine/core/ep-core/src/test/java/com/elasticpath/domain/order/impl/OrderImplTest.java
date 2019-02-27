/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.impl.EventOriginatorImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderEvent;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.service.tax.impl.TaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxOperationServiceImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>OrderImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports" })
public class OrderImplTest extends AbstractEPServiceTestCase {

	private static final String HST = "HST";

	private static final String PST = "PST";

	private static final String GST = "GST";

	private static final String TEST_ORDER_NUMBER1 = "TestOrderNumber1";

	private static final Currency CURRENCY = Currency.getInstance("USD");

	private static final long TEST_UIDPK = 10000L;

	private static final String TAX_CODE = "tax_code";
	private static final String FOO = "foo";
	private static final String BAR = "bar";

	private StoreService storeService;

	private static final double ALLOWABLE_ERROR = 0.0000001;
	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);

		storeService = context.mock(StoreService.class);
		stubGetBean(ContextIdNames.STORE_SERVICE, storeService);

		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(getMockedStore().getCode()); will(returnValue(getMockedStore()));
		} });

		setUpTaxCalculationService();
	}

	private void setUpTaxCalculationService() {
		TaxCalculationService taxCalculationService = new TaxCalculationServiceImpl() {

			private final BigDecimal taxValue = new BigDecimal("0.10");

			private final Currency defaultCurrency = Currency.getInstance(Locale.US);

			private static final int CALCULATION_FINAL_SCALE = 2;

			@Override
			public TaxCalculationResult calculateTaxes(final String storeCode, final TaxAddress destinationAddress, final TaxAddress originAddress,
					final Money shippingCost, final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap,
					final Money preTaxDiscount, final TaxOperationContext taxOperationContext) {
				final TaxCalculationResult taxResult = new TaxCalculationResultImpl();
				taxResult.setDefaultCurrency(Currency.getInstance(Locale.US));

				for (final Map.Entry<? extends ShoppingItem, ShoppingItemPricingSnapshot> entry : shoppingItemPricingSnapshotMap.entrySet()) {
					final ShoppingItem shoppingItem = entry.getKey();
					final ShoppingItemPricingSnapshot pricingSnapshot = entry.getValue();
					final BigDecimal shoppingItemTotal = pricingSnapshot.getPriceCalc().withCartDiscounts().getAmount();
					final Money curTax = Money.valueOf(
							shoppingItemTotal.multiply(taxValue).setScale(CALCULATION_FINAL_SCALE, BigDecimal.ROUND_HALF_UP),
							defaultCurrency);
					taxResult.addItemTax(shoppingItem.getGuid(), curTax);
				}

				Money newShippingCost = shippingCost;
				if (newShippingCost.getAmount() == null) {
					newShippingCost = Money.valueOf(BigDecimal.ZERO.setScale(CALCULATION_FINAL_SCALE), defaultCurrency);
				}
				final Money shippingTax = Money.valueOf(
						newShippingCost.getAmount().multiply(taxValue).setScale(CALCULATION_FINAL_SCALE, BigDecimal.ROUND_HALF_UP),
						defaultCurrency);
				taxResult.addShippingTax(shippingTax);
				taxResult.setBeforeTaxShippingCost(newShippingCost);
				taxResult.setTaxInclusive(false);

				return taxResult;
			}
		};

		TaxOperationServiceImpl taxOperationService = new TaxOperationServiceImpl();
		taxOperationService.setTaxCalculationService(taxCalculationService);
		taxOperationService.setBeanFactory(getBeanFactory());
		taxOperationService.setAddressAdapter(new TaxAddressAdapter());

		stubGetBean(ContextIdNames.TAX_OPERATION_SERVICE, taxOperationService);
	}

	/**
	 * Test that the order subtotal is the sum of the order's shipments subtotals.
	 */
	@Test
	public void testSubtotalSum() {
		final BigDecimal shipmentSubtotal = BigDecimal.TEN;
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		final OrderShipment mockOrderShipment2 = context.mock(OrderShipment.class, "second order shipment");
		context.checking(new Expectations() {
			{
				allowing(mockOrderShipment).getSubtotal();
				will(returnValue(shipmentSubtotal));

				allowing(mockOrderShipment).getCreatedDate();
				will(returnValue(new Date()));

				allowing(mockOrderShipment2).getSubtotal();
				will(returnValue(shipmentSubtotal));

				allowing(mockOrderShipment2).getCreatedDate();
				will(returnValue(new Date()));
			}
		});
		final List<OrderShipment> shipments = new ArrayList<>();
		shipments.add(mockOrderShipment);
		shipments.add(mockOrderShipment2);
		OrderImpl order = new OrderImpl() {
			private static final long serialVersionUID = -5548083387876584259L;

			@Override
			public List<OrderShipment> getShipments() {
				return shipments;
			}
		};
		assertEquals("Order subtotal should be the sum of its shipment subtotals, and the scale should be 2.",
				new BigDecimal("20").setScale(2), order.getSubtotal());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		Order order = createTestOrder();
		Date testDate = new Date();
		order.setCreatedDate(testDate);
		assertEquals(testDate, order.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		Order order = createTestOrder();
		Date testDate = new Date();
		order.setLastModifiedDate(testDate);
		assertEquals(testDate, order.getLastModifiedDate());

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getLastModifiedBy()'.
	 */
	@Test
	public void testGetSetLastModifiedBy() {
		Order order = createTestOrder();
		EventOriginator originator = new EventOriginatorImpl();
		order.setModifiedBy(originator);
		assertEquals(originator, order.getModifiedBy());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getIpAddress()'.
	 */
	@Test
	public void testGetSetIpAddress() {
		Order order = createTestOrder();
		String testString = "TestString";
		order.setIpAddress(testString);
		assertEquals(testString, order.getIpAddress());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getCustomer()'.
	 */
	@Test
	public void testGetSetCustomer() {
		Order order = createTestOrder();
		Customer customer = new CustomerImpl();
		order.setCustomer(customer);
		assertEquals(customer, order.getCustomer());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderBillingAddress()'.
	 */
	@Test
	public void testGetSetOrderBillingAddress() {
		Order order = createTestOrder();
		OrderAddress orderAddress = new OrderAddressImpl();
		order.setBillingAddress(orderAddress);
		assertEquals(orderAddress, order.getBillingAddress());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderShipments()'.
	 */
	@Test
	public void testGetSetOrderShipments() {
		OrderImpl order = createTestOrder();
		OrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		List<OrderShipment> shipmentSet = new ArrayList<>();
		shipmentSet.add(orderShipment);
		order.setShipments(shipmentSet);
		assertEquals(shipmentSet, order.getAllShipments());

		int numShipments = order.getAllShipments().size();
		createPhysicalOrderShipmentForOrder(order);
		assertEquals(numShipments + 1, order.getAllShipments().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderShipments()'.
	 */
	@Test
	public void testGetSetOrderPayments() {
		Order order = createTestOrder();
		OrderPayment orderPayment = new OrderPaymentImpl();
		Set<OrderPayment> paymentSet = new HashSet<>();
		paymentSet.add(orderPayment);
		order.setOrderPayments(paymentSet);
		assertEquals(paymentSet, order.getOrderPayments());

		int numPayments = order.getOrderPayments().size();
		OrderPayment anotherOrderPayment = new OrderPaymentImpl();
		order.addOrderPayment(anotherOrderPayment);
		assertEquals(numPayments + 1, order.getOrderPayments().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderEvents()'.
	 */
	@Test
	public void testGetSetOrderEvents() {
		OrderImpl order = createTestOrder();
		Set<OrderEvent> orderNotesSet = new HashSet<>();
		order.setOrderEvents(orderNotesSet);
		assertEquals(orderNotesSet, order.getOrderEvents());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getLocale()'.
	 */
	@Test
	public void testGetSetLocale() {
		Order order = createTestOrder();
		Locale testLocale = Locale.CANADA;
		order.setLocale(testLocale);
		assertEquals(testLocale, order.getLocale());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getCurrency()'.
	 */
	@Test
	public void testGetSetCurrency() {
		Order order = createTestOrder();
		order.setCurrency(CURRENCY);
		assertEquals(CURRENCY, order.getCurrency());
	}

	/**
	 * Test that the before tax subtotal is the sum of the before tax subtotal
	 * of all the OrderShipments, and that the scale of the returned Money object
	 * is 2.
	 */
	@Test
	public void testBeforeTaxSubtotalSum() {
		final BigDecimal beforeTaxSubtotal = BigDecimal.TEN;
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		final OrderShipment mockOrderShipment2 = context.mock(OrderShipment.class, "second order shipment");
		context.checking(new Expectations() {
			{
				allowing(mockOrderShipment).getSubtotalBeforeTax();
				will(returnValue(beforeTaxSubtotal));

				allowing(mockOrderShipment).getCreatedDate();
				will(returnValue(new Date()));

				allowing(mockOrderShipment2).getSubtotalBeforeTax();
				will(returnValue(beforeTaxSubtotal));

				allowing(mockOrderShipment2).getCreatedDate();
				will(returnValue(new Date()));
			}
		});
		final List<OrderShipment> shipments = new ArrayList<>();
		shipments.add(mockOrderShipment);
		shipments.add(mockOrderShipment2);
		OrderImpl order = new OrderImpl() {
			private static final long serialVersionUID = 3957500176048089715L;

			@Override
			public List<OrderShipment> getShipments() {
				return shipments;
			}
			@Override
			public Currency getCurrency() {
				return CURRENCY;
			}
		};
		assertEquals("Order beforeTaxSubtotal should be the sum of its shipment beforeTaxSubtotals, and the scale should be 2.",
				new BigDecimal("20").setScale(2), order.getBeforeTaxSubtotalMoney().getRawAmount());
		assertEquals(CURRENCY, order.getBeforeTaxSubtotalMoney().getCurrency());
	}

	/**
	 * Test that the subtotal discount is the sum of the subtotal discounts
	 * of all the OrderShipments, and that the scale of the returned Money object
	 * is 2.
	 */
	@Test
	public void testSubtotalDiscountSum() {
		final BigDecimal subtotalDiscount = BigDecimal.TEN;
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		final OrderShipment mockOrderShipment2 = context.mock(OrderShipment.class, "second order shipment");
		context.checking(new Expectations() {
			{
				allowing(mockOrderShipment).getSubtotalDiscount();
				will(returnValue(subtotalDiscount));

				allowing(mockOrderShipment).getCreatedDate();
				will(returnValue(new Date()));

				allowing(mockOrderShipment2).getSubtotalDiscount();
				will(returnValue(subtotalDiscount));

				allowing(mockOrderShipment2).getCreatedDate();
				will(returnValue(new Date()));
			}
		});
		final List<OrderShipment> shipments = new ArrayList<>();
		shipments.add(mockOrderShipment);
		shipments.add(mockOrderShipment2);
		OrderImpl order = new OrderImpl() {
			private static final long serialVersionUID = -4789276059268183318L;

			@Override
			public List<OrderShipment> getShipments() {
				return shipments;
			}
			@Override
			public Currency getCurrency() {
				return CURRENCY;
			}
		};
		assertEquals("Order subtotal discount should be the sum of its shipment subtotal discounts, and the scale should be 2.",
				new BigDecimal("20").setScale(2), order.getSubtotalDiscountMoney().getRawAmount());
		assertEquals(CURRENCY, order.getSubtotalDiscountMoney().getCurrency());
	}

	/** Test for getOrderShippingCostMoney(). */
	@Test
	public void testGetOrderShippingCostMoney() {
		Order order = createTestOrder();
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		orderPayment.setCurrencyCode(CURRENCY.getCurrencyCode());
		Set<OrderPayment> paymentSet = new HashSet<>();
		paymentSet.add(orderPayment);
		order.setOrderPayments(paymentSet);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);

		assertEquals(BigDecimal.ZERO.setScale(2), order.getTotalShippingCostMoney().getAmount());

		PhysicalOrderShipment orderShipment1 = createPhysicalOrderShipmentForOrder(order);
		orderShipment1.setShippingCost(new BigDecimal("50.00"));

		PhysicalOrderShipment orderShipment2 = createPhysicalOrderShipmentForOrder(order);
		orderShipment2.setShippingCost(new BigDecimal("25.50"));

		assertEquals(new BigDecimal("75.50"), order.getTotalShippingCostMoney().getAmount());

	}

	/** Test for getBeforeTaxShippingCostMoney(). */
	@Test
	public void testGetBeforeTaxShippingCostMoney() {
		Order order = createTestOrder();
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		orderPayment.setCurrencyCode(CURRENCY.getCurrencyCode());
		Set<OrderPayment> paymentSet = new HashSet<>();
		paymentSet.add(orderPayment);
		order.setOrderPayments(paymentSet);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);

		assertEquals(BigDecimal.ZERO.setScale(2), order.getTotalShippingCostMoney().getAmount());

		PhysicalOrderShipment orderShipment1 =  createPhysicalOrderShipmentForOrder(order);
		orderShipment1.setShippingCost(new BigDecimal("45.00"));


		PhysicalOrderShipment orderShipment2 = createPhysicalOrderShipmentForOrder(order);
		orderShipment2.setShippingCost(new BigDecimal("22.50"));

		assertEquals(new BigDecimal("67.50"), order.getBeforeTaxTotalShippingCostMoney().getAmount());

	}

	/** test for OrderImpl method. */
	@Test
	public void testGetShippingAddress() {
		Order order = createTestOrder();
		assertNull(order.getShippingAddress());

		OrderAddress testAddress = new OrderAddressImpl();
		PhysicalOrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		orderShipment.setShipmentAddress(testAddress);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.addShipment(orderShipment);

		assertSame(testAddress, order.getShippingAddress());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.getReturns()'.
	 */
	@Test
	public void testGetReturns() {
		OrderImpl order = createTestOrder();
		Set<OrderReturn> returns = new HashSet<>();
		order.setReturns(returns);
		assertEquals(returns, order.getReturns());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.addReturn(OrderReturn)'.
	 */
	@Test
	public void testOrderReturn() {
		Order order = createTestOrder();
		OrderReturn orderReturn = new OrderReturnImpl();
		order.addReturn(orderReturn);
		assertEquals(1, order.getReturns().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.adOrderNote(OrderNote)'.
	 */
	@Test
	public void testOrderNote() {
		Order order = createTestOrder();
		OrderEvent orderEvent = new OrderEventImpl();
		order.addOrderEvent(orderEvent);
		assertEquals(1, order.getOrderEvents().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.getOrderNumber'.
	 */
	@Test
	public void testGetSetOrderNumber() {
		Order order = createTestOrder();
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		assertEquals(TEST_ORDER_NUMBER1, order.getOrderNumber());
		assertEquals(TEST_ORDER_NUMBER1, order.getGuid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.setGuid'.
	 */
	@SuppressWarnings({ "PMD.EmptyCatchBlock" })
	@Test
	public void testSetGuidShouldThrowException() {
		Order order = createTestOrder();
		try {
			order.setGuid(TEST_ORDER_NUMBER1);
			fail();
		} catch (Exception e) {
			// exception was expected
		}
	}

	/**
	 * Verify that orders are created with a status of CREATED.
	 */
	@Test
	public void testOrderStartsWithStatusOfCreated() {
		Order order = createTestOrder();
		assertEquals(OrderStatus.CREATED, order.getStatus());
	}

	/**
	 * Test order status changes due to its shipment status changes.
	 */
	@Test
	public void testOrderStatusChange() {
		OrderImpl order = createTestOrder();
		final OrderShipment shipment = createPhysicalOrderShipmentForOrder(order);
		shipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
		final OrderShipment shipment2 = createPhysicalOrderShipmentForOrder(order);
		shipment2.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);

		order.setStatus(OrderStatus.IN_PROGRESS);

		shipment.setStatus(OrderShipmentStatus.SHIPPED);
		assertEquals(OrderStatus.PARTIALLY_SHIPPED, order.getStatus());

		shipment2.setStatus(OrderShipmentStatus.SHIPPED);
		assertEquals(OrderStatus.COMPLETED, order.getStatus());
	}

	/**
	 * Test that an order with digital goods cannot be cancelled.
	 */
	@Test
	public void testOrderNotCancellableWithDigitalGoods() {
		Order order = createTestOrder();
		final OrderShipment shipment = new ElectronicOrderShipmentImpl();
		shipment.setStatus(OrderShipmentStatus.RELEASED);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.addShipment(shipment);
		shipment.setStatus(OrderShipmentStatus.SHIPPED);
		assertFalse(order.isCancellable());
	}

	/**
	 * Test that a completed order cannot be cancelled.
	 */
	@Test
	public void testOrderNotCancellableWhenComplete() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.COMPLETED);
		assertFalse(order.isCancellable());
	}

	/**
	 * Test that a partially completed order cannot be cancelled.
	 */
	@Test
	public void testOrderNotCancellableWhenPartiallyShipped() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.PARTIALLY_SHIPPED);
		assertFalse(order.isCancellable());
	}

	/**
	 * Test that an order awaiting exchange can be cancelled.
	 */
	@Test
	public void testOrderCancellableWhenAwaitingExchange() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.AWAITING_EXCHANGE);
		assertThat(order.isCancellable()).isTrue();
	}

	/**
	 * Test that an order that has been cancelled cannot be cancelled again.
	 */
	@Test
	public void testOrderNotCancellableWhenCancelled() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CANCELLED);
		assertFalse(order.isCancellable());
	}

	/**
	 * Test that an order currently in progress can be cancelled.
	 */
	@Test
	public void testOrderCancellableWhenInProgress() {
		OrderImpl order = createOrderInProgress();
		assertTrue(order.isCancellable());
	}

	/**
	 * Test that a created order can be cancelled.
	 */
	@Test
	public void testOrderCancellableWhenCreated() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CREATED);
		assertTrue(order.isCancellable());
	}

	/**
	 * Test that a created order can be cancelled.
	 */
	@Test
	public void testOrderHoldableWhenCreated() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CREATED);
		assertTrue(order.isHoldable());
	}

	/**
	 * Test that a completed order cannot be put on hold.
	 */
	@Test
	public void testOrderNotHoldableWhenComplete() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.COMPLETED);
		assertFalse(order.isHoldable());
	}

	/**
	 * Test that a partially completed order can be put on hold.
	 */
	@Test
	public void testOrderNotHoldableWhenPartiallyShipped() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.PARTIALLY_SHIPPED);
		assertFalse(order.isHoldable());
	}

	/**
	 * Test that an order awaiting exchange can be put on hold.
	 */
	@Test
	public void testOrderHoldableWhenAwaitingExchange() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.AWAITING_EXCHANGE);
		assertFalse(order.isHoldable());
	}

	/**
	 * Test that an order that has been cancelled cannot be put on hold.
	 */
	@Test
	public void testOrderNotHoldableWhenCancelled() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CANCELLED);
		assertFalse(order.isHoldable());
	}

	/**
	 * Test that an order currently in progress may not be put on hold.
	 */
	@Test
	public void testOrderNotHoldableWhenInProgress() {
		OrderImpl order = createOrderInProgress();
		assertFalse(order.isHoldable());
	}

	private OrderImpl createOrderInProgress() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.IN_PROGRESS);
		return order;
	}

	/**
	 * Test that order status transitions to IN_PROGRESS when released for fulfilment.
	 */
	@Test
	public void testOrderStatusInProgressWhenReleasedForFulfilment() {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CREATED);

		order.releaseOrder();

		assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
	}

	/**
	 * Test that a shipment can be added to an order and gets a valid shipment number and order status
	 * is correct.
	 */
	@Test
	public void testPersistedOrderAddPhysicalShipment() {
		Order order = createTestOrder();
		assertTrue("Order should be persistent", order.isPersisted());
		OrderShipment orderShipment = createPhysicalOrderShipmentForOrder(order);
		orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		try {
			order.addShipment(orderShipment);
		} catch (OrderNotPersistedException ex) {
			fail("Persisted order should not have a problem adding a shipment");
		}
		assertNotNull("Shipment number should not be null", orderShipment.getShipmentNumber());
		assertTrue("Shipment number should not be empty", orderShipment.getShipmentNumber().length() > 0);
		assertTrue("Shipment number should contain order number", orderShipment.getShipmentNumber().contains(order.getOrderNumber()));
		assertEquals("Order status should be CREATED", OrderStatus.CREATED, order.getStatus());
	}

	/**
	 * Test that a shipment can be added to an order and gets a valid shipment number and order status
	 * is correct.
	 */
	@Test
	public void testPersistedOrderAddElectronicShipment() {
		OrderImpl order = createOrderInProgress();
		assertTrue("Order should be persistent", order.isPersisted());
		OrderShipment orderShipment = new ElectronicOrderShipmentImpl();
		orderShipment.setStatus(OrderShipmentStatus.RELEASED);
		try {
			order.addShipment(orderShipment);
		} catch (OrderNotPersistedException ex) {
			fail("Persisted order should not have a problem adding a shipment");
		}
		assertNotNull("Shipment number should not be null", orderShipment.getShipmentNumber());
		assertTrue("Shipment number should not be empty", orderShipment.getShipmentNumber().length() > 0);
		assertTrue("Shipment number should contain order number", orderShipment.getShipmentNumber().contains(order.getOrderNumber()));
		assertEquals("Order status should be IN_PROGRESS", OrderStatus.IN_PROGRESS, order.getStatus());
	}

	/**
	 * Test that a shipment can be added to an order and gets a valid shipment number and order status
	 * is correct.
	 */
	@Test
	public void testPersistedOrderAddServiceShipment() {
		OrderImpl order = createOrderInProgress();
		assertTrue("Order should be persistent", order.isPersisted());
		OrderShipment orderShipment = new ServiceOrderShipmentImpl();
		orderShipment.setStatus(OrderShipmentStatus.SHIPPED);
		try {
			order.addShipment(orderShipment);
		} catch (OrderNotPersistedException ex) {
			fail("Persisted order should not have a problem adding a shipment");
		}
		assertNotNull("Shipment number should not be null", orderShipment.getShipmentNumber());
		assertTrue("Shipment number should not be empty", orderShipment.getShipmentNumber().length() > 0);
		assertTrue("Shipment number should contain order number", orderShipment.getShipmentNumber().contains(order.getOrderNumber()));
		assertEquals("Order status should be COMPLETED", OrderStatus.COMPLETED, order.getStatus());
	}

	/**
	 * Test that an attempt to add a shipment to a transient order throws an exception.
	 */
	@Test
	public void testTransientOrderAddShipment() {
		Order order = createTestOrder();
		order.setUidPk(0);
		assertFalse("Order should not be persistent", order.isPersisted());
		OrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		try {
			order.addShipment(orderShipment);
			fail("Attempting to add a shipment to a transient order should result in an exception");
		} catch (OrderNotPersistedException expected) {
			assertNotNull("Exception was expected", expected);
		}
	}

	@Test
	public void testOrderDataLazyInstantiation() {
		Order order = createTestOrder();

		assertNull("Getter of non-existent order data key should return null", order.getFieldValue(FOO));
		assertEquals("Map getter should return empty map", Collections.emptyMap(), order.getFieldValues());
	}

	@Test
	public void testOrderData() {
		Order order = createTestOrder();
		order.setFieldValue(FOO, BAR);

		assertEquals("Getter/Setters should work as expected", BAR, order.getFieldValue(FOO));
		assertEquals("Map getter should also work", Collections.singletonMap(FOO, BAR), order.getFieldValues());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getEachItemTaxTotalsMoney()'
	 * for a single shipment.
	 */
	@Test
	public void testGetEachItemTaxTotalsMoneySingleShipment() {
		final BigDecimal pstValue = BigDecimal.valueOf(40.00);
		final BigDecimal gstValue = BigDecimal.valueOf(30.00);
		final BigDecimal hstValue = BigDecimal.valueOf(45.00);

		OrderImpl order = createTestOrder();

		Map<String, BigDecimal> taxes = new HashMap<>();
		taxes.put(GST, gstValue);
		taxes.put(PST, pstValue);
		taxes.put(HST, hstValue);

		AbstractOrderShipmentImpl orderShipment = createTestShipment(taxes);
		List<OrderShipment> shipmentSet = new ArrayList<>();
		shipmentSet.add(orderShipment);
		order.setShipments(shipmentSet);
		assertEquals(shipmentSet, order.getAllShipments());

		int numShipments = order.getAllShipments().size();
		assertEquals(numShipments, order.getAllShipments().size());

		Map<String, Money> eachTaxMap = order.getEachItemTaxTotalsMoney();
		for (final Map.Entry<String, Money> taxEntry : eachTaxMap.entrySet()) {
			BigDecimal actualValue = BigDecimal.ZERO;
			if (GST.equals(taxEntry.getKey())) {
				actualValue = gstValue;
			} else if (PST.equals(taxEntry.getKey())) {
				actualValue = pstValue;
			} else if (HST.equals(taxEntry.getKey())) {
				actualValue = hstValue;
			} else {
				fail("Unrecognized tax is given.");
			}

			assertEquals(actualValue.doubleValue(),
				taxEntry.getValue().getAmount().doubleValue(),
					ALLOWABLE_ERROR);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getEachItemTaxTotalsMoney()'
	 * for multiple shipments.
	 */
	@Test
	public void testGetEachItemTaxTotalsMoneyMultiShipment() {
		final BigDecimal gstValue = BigDecimal.valueOf(70.00);
		final BigDecimal pstValue = BigDecimal.valueOf(60.00);
		final BigDecimal hstValue = BigDecimal.valueOf(45.00);

		final BigDecimal gst1 = BigDecimal.valueOf(10.00);
		final BigDecimal gst2 = BigDecimal.valueOf(60.00);
		final BigDecimal pst1 = BigDecimal.valueOf(20.00);
		final BigDecimal hst1 = BigDecimal.valueOf(5.00);
		final BigDecimal hst2 = BigDecimal.valueOf(0.00);
		final BigDecimal hst3 = BigDecimal.valueOf(40.00);

		OrderImpl order = createTestOrder();
		List<OrderShipment> shipmentSet = new ArrayList<>();

		Map<String, BigDecimal> taxes1 = new HashMap<>();
		taxes1.put(GST, gst1);
		taxes1.put(PST, pst1);
		taxes1.put(HST, hst1);

		AbstractOrderShipmentImpl orderShipment1 = createTestShipment(taxes1);
		shipmentSet.add(orderShipment1);

		Map<String, BigDecimal> taxes2 = new HashMap<>();

		taxes2.put(GST, gst2);
		taxes2.put(PST, pst1);
		taxes2.put(HST, hst2);
		AbstractOrderShipmentImpl orderShipment2 = createTestShipment(taxes2);
		shipmentSet.add(orderShipment2);

		Map<String, BigDecimal> taxes3 = new HashMap<>();
		taxes3.put(PST, pst1);
		taxes3.put(HST, hst3);
		AbstractOrderShipmentImpl orderShipment3 = createTestShipment(taxes3);
		shipmentSet.add(orderShipment3);


		order.setShipments(shipmentSet);
		assertEquals(shipmentSet, order.getAllShipments());

		int numShipments = order.getAllShipments().size();
		assertEquals(numShipments, order.getAllShipments().size());

		Map<String, Money> eachTaxMap = order.getEachItemTaxTotalsMoney();
		for (final Map.Entry<String, Money> taxEntry : eachTaxMap.entrySet()) {
			BigDecimal actualValue = BigDecimal.ZERO;
			if (GST.equals(taxEntry.getKey())) {
				actualValue = gstValue;
			} else if (PST.equals(taxEntry.getKey())) {
				actualValue = pstValue;
			} else if (HST.equals(taxEntry.getKey())) {
				actualValue = hstValue;
			} else {
				fail("Unrecognized tax is given.");
			}

			assertEquals("For " + taxEntry.getKey(), actualValue.doubleValue(),
				taxEntry.getValue().getAmount().doubleValue(),
					ALLOWABLE_ERROR);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getEachItemTaxTotalsMoney()'
	 * for no shipments.
	 */
	@Test
	public void testGetEachItemTaxTotalsMoneyNoShipment() {
		OrderImpl order = createTestOrder();
		Map<String, Money> eachTaxMap = order.getEachItemTaxTotalsMoney();
		assertEquals(0, eachTaxMap.size());
	}

	private OrderSku createOrderSku(final String guid) {
		final OrderSkuImpl orderSku = new OrderSkuImpl();
		orderSku.setGuid(guid);
		return orderSku;
	}

	private PhysicalOrderShipment createPhysicalOrderShipmentForOrder(final Order order) {
		final PhysicalOrderShipment orderShipment = new PhysicalOrderShipmentImpl() {
			private static final long serialVersionUID = -4932088990192672022L;

			@Override
			protected void addSkuListeners(final OrderSku orderSku) {
				// do nothing; this short-circuits activities we don't care about, for example tax calculation
			}
		};
		order.addShipment(orderShipment);
		return orderShipment;
	}

	private ElectronicOrderShipment createElectronicOrderShipmentForOrder(final Order order) {
		final ElectronicOrderShipment orderShipment = new ElectronicOrderShipmentImpl() {
			private static final long serialVersionUID = -7776654305823847968L;

			@Override
			protected void addSkuListeners(final OrderSku orderSku) {
				// do nothing; this short-circuits activities we don't care about, for example tax calculation
			}
		};
		order.addShipment(orderShipment);
		return orderShipment;
	}

	@Test
	public void testGetOrderSkuByGuidReturnsNullWhenNoSuchOrderSku() throws Exception {
		final Order order = createTestOrder();
		final OrderShipment orderShipment = createPhysicalOrderShipmentForOrder(order);

		final OrderSku orderSku1 = createOrderSku("GUID-0001");
		final OrderSku orderSku2 = createOrderSku("GUID-0002");

		orderShipment.addShipmentOrderSku(orderSku1);
		orderShipment.addShipmentOrderSku(orderSku2);

		assertNull("Expected null when attempting to retrieve an OrderSku not contained within the order",
				order.getOrderSkuByGuid("NO-SUCH-GUID"));
	}

	@Test
	public void testGetOrderSkuByGuidReturnsMatchingOrderSku() throws Exception {
		final Order order = createTestOrder();
		final OrderShipment orderShipment = createPhysicalOrderShipmentForOrder(order);

		final OrderSku orderSku1 = createOrderSku("GUID-0001");

		final String matchingOrderSkuGuid = "GUID-0002";
		final OrderSku orderSku2 = createOrderSku(matchingOrderSkuGuid);

		orderShipment.addShipmentOrderSku(orderSku1);
		orderShipment.addShipmentOrderSku(orderSku2);

		assertEquals("Unexpected OrderSku", orderSku2, order.getOrderSkuByGuid(matchingOrderSkuGuid));
	}

	@Test
	public void testGetOrderSkuByGuidInspectsAllShipmentsWhenMatchingOrderSku() throws Exception {
		final Order order = createTestOrder();

		final OrderShipment orderShipment1 = createPhysicalOrderShipmentForOrder(order);

		final OrderSku orderSku1 = createOrderSku("GUID-0001");
		final OrderSku orderSku2 = createOrderSku("GUID-0002");

		orderShipment1.addShipmentOrderSku(orderSku1);
		orderShipment1.addShipmentOrderSku(orderSku2);

		final OrderShipment orderShipment2 = createElectronicOrderShipmentForOrder(order);

		final OrderSku orderSku3 = createOrderSku("GUID-0003");

		final String matchingOrderSkuGuid = "GUID-0004";
		final OrderSku orderSku4 = createOrderSku(matchingOrderSkuGuid);

		orderShipment2.addShipmentOrderSku(orderSku3);
		orderShipment2.addShipmentOrderSku(orderSku4);

		assertEquals("Unexpected OrderSku", orderSku4, order.getOrderSkuByGuid(matchingOrderSkuGuid));
	}

	@Test
	public void verifyOrderNotReleasableWhenFulfilmentAlreadyInProgress() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.IN_PROGRESS);

		assertFalse("Expected order not to be releasable with a status of IN_PROGRESS", order.isReleasable());
	}

	@Test
	public void verifyOrderNotReleasableWhenAlreadyCompleted() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.COMPLETED);

		assertFalse("Expected order not to be releasable with a status of COMPLETED", order.isReleasable());
	}

	@Test
	public void verifyOrderNotReleasableWhenAlreadyCancelled() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CANCELLED);

		assertFalse("Expected order not to be releasable with a status of CANCELLED", order.isReleasable());
	}

	@Test
	public void verifyOrderNotReleasableWhenAlreadyPartiallyShipped() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.PARTIALLY_SHIPPED);

		assertFalse("Expected order not to be releasable with a status of PARTIALLY_SHIPPED", order.isReleasable());
	}

	@Test
	public void verifyOrderNotReleasableWhenAlreadyFailed() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.FAILED);

		assertFalse("Expected order not to be releasable with a status of FAILED", order.isReleasable());
	}

	@Test
	public void verifyOrderReleasableWhenCreated() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CREATED);

		assertTrue("Expected order to be releasable with a status of CREATED", order.isReleasable());
	}

	@Test
	public void verifyOrderReleasableWhenOnHold() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.ONHOLD);

		assertTrue("Expected order to be releasable with a status of ONHOLD", order.isReleasable());
	}

	@Test
	public void verifyOrderReleasableWhenAwaitingExchange() throws Exception {
		final OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.AWAITING_EXCHANGE);

		assertTrue("Expected order to be releasable with a status of AWAITING_EXCHANGE", order.isReleasable());
	}

	/**
	 * Implementation of Order with auto-recalculation enabled by default for testing purposes.
	 */
	class TestOrderImpl extends OrderImpl {

		private static final long serialVersionUID = -9036493321034387004L;

		/**
		 * Override constructor to enable auto-recalculation.
		 * Uses exclusive tax calculation mode.
		 */
		TestOrderImpl() {
			super();
			enableRecalculation();
		}
	}

	/**
	 * Create an order for testing.
	 *
	 * @return the test order
	 */
	private OrderImpl createTestOrder() {
		OrderImpl order = new TestOrderImpl();
		order.initialize();
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.setCurrency(CURRENCY);
		order.setStoreCode(getMockedStore().getCode());
		order.setCustomer(new CustomerImpl());
		return order;
	}

	/**
	 * Creates a shipment for testing.
	 *
	 * @param taxes a tax mapping of tax name to value
	 * @return a shipment object
	 */
	private AbstractOrderShipmentImpl createTestShipment(final Map<String, BigDecimal> taxes) {
		Set<OrderTaxValue> taxValuesForShipment = new HashSet<>();

		for (final Map.Entry<String, BigDecimal> taxEntry : taxes.entrySet()) {
			OrderTaxValue orderTaxValue = new OrderTaxValueImpl();
			orderTaxValue.setTaxCategoryDisplayName(taxEntry.getKey());
			orderTaxValue.setTaxValue(taxEntry.getValue());
			taxValuesForShipment.add(orderTaxValue);
		}

		AbstractOrderShipmentImpl orderShipment = new PhysicalOrderShipmentImpl();
		orderShipment.setShipmentTaxes(taxValuesForShipment);

		return orderShipment;
	}

	/**
	 * Returns the default mocked store.
	 *
	 * @return the default mocked store.
	 */
	@Override
	protected Store getMockedStore() {
		Set <TaxCode> taxCodes = new HashSet<>();
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(TAX_CODE));

		Store store = super.getMockedStore();
		store.setTaxCodes(taxCodes);
		return store;
	}

	private static TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(taxCodeName);
		taxCode.setGuid(System.currentTimeMillis() + taxCodeName);
		return taxCode;
	}
}
