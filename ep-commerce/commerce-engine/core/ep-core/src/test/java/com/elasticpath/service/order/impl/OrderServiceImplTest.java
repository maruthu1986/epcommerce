/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.order.impl;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyVararg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.domain.catalog.InventoryAudit.EVENT_ORIGINATOR_WS;
import static com.elasticpath.domain.order.OrderPaymentStatus.PENDING;
import static com.elasticpath.domain.order.OrderShipmentStatus.INVENTORY_ASSIGNED;
import static com.elasticpath.domain.order.OrderShipmentStatus.RELEASED;
import static com.elasticpath.domain.order.OrderShipmentStatus.SHIPPED;
import static com.elasticpath.domain.order.OrderStatus.CANCELLED;
import static com.elasticpath.domain.order.OrderStatus.COMPLETED;
import static com.elasticpath.domain.order.OrderStatus.FAILED;
import static com.elasticpath.domain.order.OrderStatus.IN_PROGRESS;
import static com.elasticpath.domain.order.OrderStatus.PARTIALLY_SHIPPED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.event.impl.EventOriginatorImpl;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.AbstractOrderShipmentImpl;
import com.elasticpath.domain.order.impl.ElectronicOrderShipmentImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderReturnImpl;
import com.elasticpath.domain.order.impl.OrderReturnSkuImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.order.impl.ServiceOrderShipmentImpl;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.persistence.support.impl.OrderCriterionImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.CompleteShipmentFailedException;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.ReleaseShipmentFailedException;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.payment.impl.PaymentResultImpl;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;

/**
 * Test <code>OrderServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects", "PMD.GodClass",
		"PMD.AvoidDecimalLiteralsInBigDecimalConstructor"})
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

	private static final String GUID_VALUE = "guidValue";

	public static final Currency CAD = Currency.getInstance("CAD");
	private static final String SHIPMENT_NUMBER = "1-1";

	private OrderServiceImpl orderServiceImpl;

	private static final String ORDER_NUMBER = "order_1";

	private static final long MINUTE_IN_MILLIS = 60000;

	private static final String EMAIL_VALUE = "support@elasticpath.ca";

	private OrderCriterion orderCriterion;
	private Store store;
	private StoreImpl store2;
	private Order order, order2;

	@Mock private TimeService mockTimeService;
	@Mock private PaymentService mockPaymentService;
	@Mock private StoreService mockStoreService;
	@Mock private EventMessageFactory eventMessageFactory;
	@Mock private EventMessagePublisher eventMessagePublisher;
	@Mock private OrderEventHelper orderEventHelper;
	@Mock private PersistenceEngine persistenceEngine;
	@Mock private FetchPlanHelper fetchPlanHelper;
	@Mock private BeanFactory beanFactory;

	private EventOriginatorHelper eventOriginatorHelper;

	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	private final ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		orderServiceImpl = new OrderServiceImpl();
		orderServiceImpl.setPersistenceEngine(persistenceEngine);
		orderServiceImpl.setFetchPlanHelper(fetchPlanHelper);

		elasticPath.setBeanFactory(beanFactory);
		when(beanFactory.getBeanImplClass(ContextIdNames.ORDER)).thenAnswer(answer -> OrderImpl.class);
		when(beanFactory.getBean(ContextIdNames.ORDER)).thenReturn(new OrderImpl());

		orderCriterion = new OrderCriterionImpl();
		when(beanFactory.getBean(ContextIdNames.ORDER_CRITERION)).thenReturn(orderCriterion);

		eventOriginatorHelper = new EventOriginatorHelperImpl();
		when(beanFactory.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER)).thenReturn(eventOriginatorHelper);
		when(beanFactory.getBean(ContextIdNames.ORDER_EVENT_HELPER)).thenReturn(orderEventHelper);
		when(beanFactory.getBean(ContextIdNames.EVENT_ORIGINATOR)).thenReturn(new EventOriginatorImpl());

		when(mockTimeService.getCurrentTime()).thenReturn(new Date());

		orderServiceImpl.setEventMessageFactory(eventMessageFactory);
		orderServiceImpl.setEventMessagePublisher(eventMessagePublisher);
		orderServiceImpl.setPaymentService(mockPaymentService);
		orderServiceImpl.setStoreService(mockStoreService);
		orderServiceImpl.setTimeService(mockTimeService);

		Warehouse warehouse = new WarehouseImpl();

		store = new StoreImpl();
		store.setCode("store");
		store.setWarehouses(singletonList(warehouse));

		store2 = new StoreImpl();
		store2.setCode("store2");

		when(mockStoreService.findStoreWithCode(store.getCode())).thenReturn(store);
		when(mockStoreService.findStoreWithCode(store2.getCode())).thenReturn(store2);

		order = new OrderImpl();
		order.setUidPk(1L);
		order.setStoreCode(store.getCode());
		order.setOrderNumber(ORDER_NUMBER);
		order.setCurrency(Currency.getInstance(Locale.US));

		order2 = new OrderImpl();
		order2.setUidPk(1L);
		order2.setStoreCode(store.getCode());

	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test(expected = EpServiceException.class)
	public void testSetPersistenceEngine() {
		orderServiceImpl.setPersistenceEngine(null);
		orderServiceImpl.add(new OrderImpl());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.add(Order)'.
	 */
	@Test
	public void testAdd() {
		Order added = orderServiceImpl.add(order);
		verify(persistenceEngine).save(order);
		assertThat(added.getStore()).as("Store should have been set").isEqualTo(store);
	}

	/**
	 * Test findOrder with null inputs.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindOrderWithNullsShouldThrowException() {
		orderServiceImpl.findOrder(null, null, true);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findOrder'.
	 */
	@Test
	public void testFindOrder() {
		when(persistenceEngine.retrieve(anyString(), anyVararg())).thenReturn(singletonList(order));

		final List<Order> resultList1 = orderServiceImpl.findOrder("orderNumber", ORDER_NUMBER, true);
		assertThat(resultList1).isEqualTo(singletonList(order));
		assertThat(order.getStore()).as("Store should be populated via the store code").isEqualTo(store);

		final List<Order> resultList2 = orderServiceImpl.findOrder("orderNumber", ORDER_NUMBER, false);
		assertThat(resultList2).isEqualTo(singletonList(order));
		assertThat(order.getStore()).as("Store should be populated via the store code").isEqualTo(store);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findOrderByCustomerCriteria'.
	 */
	@Test
	public void testFindOrderByCustomerEmail() {
		when(persistenceEngine.retrieve(anyString(), anyVararg())).thenReturn(singletonList(order));
		assertThat(orderServiceImpl.findOrderByCustomerEmail(EMAIL_VALUE, true)).isEqualTo(singletonList(order));
		assertStoreIsPopulated(store, order);

		when(persistenceEngine.retrieve(anyString(), anyVararg())).thenReturn(singletonList(order2));
		assertThat(orderServiceImpl.findOrderByCustomerEmail(EMAIL_VALUE, false)).isEqualTo(singletonList(order2));
		assertStoreIsPopulated(store, order2);
	}

	@Test
	public void testFindOrderByStatus() {
		when(persistenceEngine.retrieve(anyString(), anyVararg())).thenReturn(singletonList(order));
		assertThat(orderServiceImpl.findOrderByStatus(
			CANCELLED, PENDING, INVENTORY_ASSIGNED)).isEqualTo(singletonList(order));
		assertStoreIsPopulated(store, order);
	}

	@Test
	public void testFindOrderByGiftCertificateCode() {
		final boolean isExactMatch = true;
		when(persistenceEngine.retrieve(anyString(), anyVararg())).thenReturn(singletonList(order));

		assertThat(orderServiceImpl.findOrderByGiftCertificateCode("GC1", isExactMatch)).isEqualTo(singletonList(order));
		assertStoreIsPopulated(store, order);
	}

	@Test
	public void testAwaitingShipmentsCount() {
		final List<Long> orders = new ArrayList<>();
		orders.add(2L);

		Warehouse warehouse = new WarehouseImpl();

		when(persistenceEngine.retrieveByNamedQuery("COUNT_PHYSICAL_SHIPMENTS_BY_STATUS_AND_WAREHOUSE", RELEASED, warehouse.getUidPk()))
			.thenAnswer(answer -> orders);

		final Long result = orderServiceImpl.getAwaitingShipmentsCount(warehouse);
		assertThat(result).isEqualTo(2L);
		verify(persistenceEngine).retrieveByNamedQuery("COUNT_PHYSICAL_SHIPMENTS_BY_STATUS_AND_WAREHOUSE", RELEASED, warehouse.getUidPk());
	}

	@Test
	public void testFindOrderByCustomerGuid() {
		// expectations
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		when(beanFactory.getBean(ContextIdNames.ORDER_SEARCH_CRITERIA)).thenReturn(orderSearchCriteria);
		CustomerSearchCriteria customerSearchCriteria = new CustomerSearchCriteria();
		when(beanFactory.getBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA)).thenReturn(customerSearchCriteria);
		when(persistenceEngine.retrieve(anyString(), any(Object[].class), anyInt(), anyInt())).thenReturn(singletonList(order));

		assertThat(orderServiceImpl.findOrderByCustomerGuid(GUID_VALUE, true))
			.as("The orders should come from the retrieve call")
			.isEqualTo(singletonList(order));
		assertStoreIsPopulated(store, order);
		assertThat(customerSearchCriteria.getGuid())
			.as("The customer search criteria should include the guid")
			.isEqualTo(GUID_VALUE);
		assertThat(orderSearchCriteria.getCustomerSearchCriteria())
			.as("The order criteria should include the customer criteria")
			.isEqualTo(customerSearchCriteria);
		assertThat(orderSearchCriteria.getExcludedOrderStatus())
			.as("The order criteria should exclude failed orders")
			.isEqualTo(FAILED);
		assertThat(customerSearchCriteria.isFuzzySearchDisabled())
			.as("The customer search criteria should disable fuzzy match")
			.isTrue();

		assertThat(orderServiceImpl.findOrderByCustomerGuid(GUID_VALUE, false))
			.as("The orders should come from the retrieve call")
			.isEqualTo(singletonList(order));
		assertThat(customerSearchCriteria.isFuzzySearchDisabled())
			.as("The customer search criteria should enable fuzzy match")
			.isFalse();
	}

	@Test
	public void testFindOrderByCustomerGuidAndStoreCode() {
		when(persistenceEngine.retrieveByNamedQuery("ORDER_SELECT_BY_CUSTOMER_GUID_AND_STORECODE", GUID_VALUE, store.getCode()))
			.thenReturn(singletonList(order));

		final boolean retrieveFullInfo = true;
		assertThat(orderServiceImpl.findOrdersByCustomerGuidAndStoreCode(GUID_VALUE, store.getCode(), retrieveFullInfo))
			.as("The orders should come from the retrieve call")
			.isEqualTo(singletonList(order));
		assertStoreIsPopulated(store, order);
		verify(persistenceEngine).retrieveByNamedQuery("ORDER_SELECT_BY_CUSTOMER_GUID_AND_STORECODE", GUID_VALUE, store.getCode());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findByCreatedDate'.
	 */
	@Test
	public void testFindByCreatedDate() {
		final Date date = new Date();
		final List<Order> orders = singletonList(order);

		when(persistenceEngine.retrieveByNamedQuery("ORDER_SELECT_BY_CREATED_DATE", date)).thenAnswer(answer -> orders);
		assertThat(orderServiceImpl.findByCreatedDate(date)).isEqualTo(orders);
		assertStoreIsPopulated(store, order);
		verify(persistenceEngine).retrieveByNamedQuery("ORDER_SELECT_BY_CREATED_DATE", date);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.list'.
	 */
	@Test
	public void testList() {
		when(persistenceEngine.retrieveByNamedQuery("ORDER_SELECT_ALL")).thenReturn(asList(order, order2));
		assertThat(orderServiceImpl.list()).isEqualTo(asList(order, order2));
		assertStoreIsPopulated(store, order2);
		verify(persistenceEngine).retrieveByNamedQuery("ORDER_SELECT_ALL");
	}

	@Test
	public void testFindOrdersBySearchCriteria() {
		when(beanFactory.getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER)).thenReturn(new FetchGroupLoadTunerImpl());
		when(persistenceEngine.retrieve(anyString(), anyInt(), anyInt())).thenReturn(asList(order, order2));

		assertThat(orderServiceImpl.findOrdersBySearchCriteria(new OrderSearchCriteria(), 1, 1)).isEqualTo(asList(order, order2));
		assertStoreIsPopulated(store, order2);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.get'.
	 */
	@Test
	public void testGet() {
		when(persistenceEngine.get(OrderImpl.class, order.getUidPk())).thenAnswer(answer -> order);

		final Order loadedOrder = orderServiceImpl.get(order.getUidPk());
		assertThat(loadedOrder).isSameAs(order);
		assertStoreIsPopulated(store, order);

		verify(persistenceEngine).get(OrderImpl.class, order.getUidPk());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.getOrderDetail'.
	 */
	@Test
	public void testGetOrderDetail() {
		when(persistenceEngine.get(OrderImpl.class, order.getUidPk())).thenAnswer(answer -> order);

		final Order loadedOrder = orderServiceImpl.getOrderDetail(order.getUidPk());
		assertThat(loadedOrder).isSameAs(order);
		assertStoreIsPopulated(store, order);

		verify(persistenceEngine).get(OrderImpl.class, order.getUidPk());
		verify(fetchPlanHelper).addField(OrderSkuImpl.class, "productSku");
		verify(fetchPlanHelper).addField(AbstractOrderShipmentImpl.class, "shipmentOrderSkusInternal");
	}

	private OrderSku createOrderSku(final String guid, final long uid, final int qty) {
		OrderSku orderSku = new OrderSkuImpl();
		orderSku.setGuid(guid);
		orderSku.setUidPk(uid);
		orderSku.setPrice(qty, null);
		return orderSku;
	}

	private OrderReturnSku createOrderReturnSku(final String guid, final OrderSku orderSku, final int qtyReturned) {
		OrderReturnSku orderReturnSku = new OrderReturnSkuImpl();
		orderReturnSku.setGuid(guid);
		orderReturnSku.setOrderSku(orderSku);
		orderReturnSku.setQuantity(qtyReturned);
		return orderReturnSku;
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.getOrderSkuReturnQtyMap(orderUid)'.
	 */
	@Test
	public void testGetOrderSkuReturnQtyMap() {
		//Create an orderSku for 2 items
		final long skuUid1 = 111L;
		final int skuQty1 = 2;
		OrderSku orderSku1 = createOrderSku("OrderSku1", skuUid1, skuQty1);
		//Create a return for 1 of the first 2 items
		final int returnSku1Qty1 = 1;
		OrderReturnSku orderReturnSku1 = createOrderReturnSku("OrderReturnSku1", orderSku1, returnSku1Qty1);
		OrderReturn orderReturn1 = new OrderReturnImpl();
		orderReturn1.setRmaCode("rmaCode1");
		Set<OrderReturnSku> orderReturnSkus1 = new HashSet<>();
		orderReturnSkus1.add(orderReturnSku1);
		orderReturn1.setOrderReturnSkus(orderReturnSkus1);

		//Create a return for the second of the first OrderSku's 2 items
		final int returnSku1Qty2 = 1;
		OrderReturnSku orderReturnSku2 = createOrderReturnSku("OrderReturnSku2", orderSku1, returnSku1Qty2);

		//Create a second orderSku for 3 items
		final long skuUid2 = 222L;
		final int skuQty2 = 3;
		OrderSku orderSku2 = createOrderSku("orderSku2", skuUid2, skuQty2);
		//Create a return for 1 of the second orderSku's 3 items
		final int returnSku2Qty1 = 1;
		OrderReturnSku orderReturnSku3 = createOrderReturnSku("orderReturnSku3", orderSku2, returnSku2Qty1);

		//At this point the first OrderSku's 2 items should both be returned, and one of the second OrderSku's 3 items should be returned.
		//Add the OrderReturnSkus to an OrderReturn
		Set<OrderReturnSku> orderReturnSkus2 = new HashSet<>();
		orderReturnSkus2.add(orderReturnSku2);
		orderReturnSkus2.add(orderReturnSku3);
		OrderReturn orderReturn2 = new OrderReturnImpl();
		orderReturn2.setRmaCode("rmaCode2");
		orderReturn2.setOrderReturnSkus(orderReturnSkus2);

		final Order mockOrder = mock(Order.class);
		final long uid = 1234L;

		final Set<OrderReturn> orderReturns = new HashSet<>();
		orderReturns.add(orderReturn1);
		orderReturns.add(orderReturn2);
		when(mockOrder.getReturns()).thenReturn(orderReturns);

		OrderServiceImpl service = new OrderServiceImpl() {
			@Override
			public Order get(final long orderUid) {
				return mockOrder;
			}

		};

		Map<Long, Integer> orderSkuReturnedMap = service.getOrderSkuReturnQtyMap(uid);
		assertThat(orderSkuReturnedMap.get(skuUid1).intValue())
			.as("Two items should be returned from the first OrderSku.")
			.isEqualTo(returnSku1Qty1 + returnSku1Qty2);
		assertThat(orderSkuReturnedMap.get(skuUid2).intValue())
			.as("One item should be returned from the second OrderSku.")
			.isEqualTo(returnSku2Qty1);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findByUids(orderUids)'.
	 */
	@Test
	public void testFindByUids() {
		final List<Long> orderUids = asList(1L, 2L);

		// expectations
		final List<Order> orders = asList(order, order2);
		when(persistenceEngine.retrieveByNamedQueryWithList("ORDER_BY_UIDS", "list", orderUids)).thenAnswer(answer -> orders);
		assertThat(this.orderServiceImpl.findByUids(orderUids)).isEqualTo(orders);
		assertStoreIsPopulated(store, order);

		// Should return an empty list if no product uid is given.
		List<Order> result = this.orderServiceImpl.findByUids(new ArrayList<>());
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(0);
		verify(persistenceEngine).retrieveByNamedQueryWithList("ORDER_BY_UIDS", "list", orderUids);
	}

	private PhysicalOrderShipmentImpl getMockPhysicalOrderShipment() {

		final TaxCalculationResult result = getTaxCalculationResult();

		return new PhysicalOrderShipmentImpl() {
			private static final long serialVersionUID = 7907342381488460247L;

			@Override
			public TaxCalculationResult calculateTaxes() {
				return result;
			}

			@Override
			public String getShipmentNumber() {
				return SHIPMENT_NUMBER;
			}

			@Override
			public long getUidPk() {
				return 2;
			}
		};

	}

	private ServiceOrderShipmentImpl getMockServiceOrderShipment() {

		final TaxCalculationResult result = getTaxCalculationResult();

		return new ServiceOrderShipmentImpl() {
			private static final long serialVersionUID = 7907342381488460247L;

			@Override
			public TaxCalculationResult calculateTaxes() {
				return result;
			}

			@Override
			public String getShipmentNumber() {
				return SHIPMENT_NUMBER;
			}

			@Override
			public long getUidPk() {
				return 2;
			}
		};

	}


	private ElectronicOrderShipmentImpl getMockElectronicOrderShipment() {

		final TaxCalculationResult result = getTaxCalculationResult();

		return new ElectronicOrderShipmentImpl() {
			private static final long serialVersionUID = 7907342381488460247L;

			@Override
			public TaxCalculationResult calculateTaxes() {
				return result;
			}

			@Override
			public String getShipmentNumber() {
				return SHIPMENT_NUMBER;
			}

			@Override
			public long getUidPk() {
				return 2;
			}
		};

	}

	private TaxCalculationResult getTaxCalculationResult() {
		final TaxCalculationResult result = new TaxCalculationResultImpl() {

			private static final long serialVersionUID = 4841309847005158665L;

			@Override
			public Money getBeforeTaxShippingCost() {
				return Money.valueOf(BigDecimal.ONE, Currency.getInstance(Locale.US));
			}

			@Override
			public Money getBeforeTaxSubTotal() {
				return Money.valueOf(BigDecimal.TEN, Currency.getInstance(Locale.US));
			}

		};
		result.setDefaultCurrency(Currency.getInstance(Locale.US));
		return result;
	}

	/**
	 * Test that when a capture fails while attempting to complete a shipment we deal with it nicely.
	 */
	@Test
	public void testCompleteShipmentCaptureFails() {
		final String orderNumber = "1";
		final String trackingCode = "code";
		final boolean captureFund = true;
		final boolean sendConfEmail = false;
		final List<OrderShipment> shipmentList = new ArrayList<>();
		PhysicalOrderShipmentImpl physicalOrderShipmentImpl = this.getMockPhysicalOrderShipment();

		shipmentList.add(physicalOrderShipmentImpl);

		Order order = new OrderImpl() {
			private static final long serialVersionUID = -2159834083423268104L;

			@Override
			public String getOrderNumber() {
				return orderNumber;
			}

		};
		order.setCurrency(CAD);
		order.setUidPk(1);
		order.setStoreCode(store.getCode());
		order.addShipment(physicalOrderShipmentImpl);

		when(beanFactory.getBean(ContextIdNames.ORDER_SERVICE)).thenReturn(orderServiceImpl);

		when(persistenceEngine.retrieveByNamedQuery(eq("PHYSICAL_SHIPMENT_BY_SHIPMENT_NUMBER"), anyString()))
			.thenAnswer(answer -> shipmentList);

		final PaymentResult paymentResult = new PaymentResultImpl();
		paymentResult.setResultCode(PaymentResult.CODE_FAILED);
		final OrderPayment orderPayment = new OrderPaymentImpl();
		paymentResult.addProcessedPayment(orderPayment);

		when(mockPaymentService.processShipmentPayment(any(OrderShipment.class)))
			.thenReturn(paymentResult)
			.thenThrow(new PaymentServiceException("expected"));

		Throwable thrown = catchThrowable(() -> orderServiceImpl.completeShipment(SHIPMENT_NUMBER, trackingCode, captureFund, null,
			sendConfEmail, eventOriginatorHelper.getSystemOriginator()));

		assertThat(thrown).isInstanceOf(CompleteShipmentFailedException.class);
		assertThat(thrown).isNotNull();
		verify(mockPaymentService).rollBackPayments(singletonList(orderPayment));
	}

	/**
	 * test the quartz job method to update shipment status.
	 */
	@Test
	public void testUpdateOrderShipmentStatus() {
		when(beanFactory.getBean(ContextIdNames.ORDER_SERVICE)).thenReturn(orderServiceImpl);

		final int pickDelayOrder1 = 4;
		final int pickDelayOrder2 = 8;
		Date now = new Date(currentTimeMillis());

		final Order order = new OrderImpl();
		order.setUidPk(1);
		Warehouse warehouse1 = new WarehouseImpl();
		warehouse1.setPickDelay(pickDelayOrder1);
		store.setWarehouses(singletonList(warehouse1));
		order.setStoreCode(store.getCode());
		order.setCurrency(CAD);

		final Order secondOrder = new OrderImpl();
		secondOrder.setUidPk(2);
		Warehouse warehouse2 = new WarehouseImpl();
		warehouse2.setPickDelay(pickDelayOrder2);

		store2.setWarehouses(singletonList(warehouse2));
		secondOrder.setStoreCode(store2.getCode());
		order.setCurrency(CAD);
		secondOrder.setCurrency(CAD);

		final OrderShipment order2Shipment = this.getMockPhysicalOrderShipment();
		order2Shipment.setCreatedDate(now);
		order2Shipment.setStatus(INVENTORY_ASSIGNED);
		// set created date an hour earlier than the actual pick delay gets active
		Date order2CreationDate = new Date(currentTimeMillis() - (pickDelayOrder2 - 1) * MINUTE_IN_MILLIS);
		order2Shipment.setCreatedDate(order2CreationDate);
		secondOrder.addShipment(order2Shipment);

		final OrderShipment newShipment = this.getMockPhysicalOrderShipment();
		newShipment.setCreatedDate(now);
		newShipment.setStatus(INVENTORY_ASSIGNED);

		// set created date an hour later than the actual pick delay gets active
		Date order1CreationDate = new Date(currentTimeMillis() - (pickDelayOrder1 + 1) * MINUTE_IN_MILLIS);
		final OrderShipment oldShipment = this.getMockPhysicalOrderShipment();
		oldShipment.setStatus(INVENTORY_ASSIGNED);
		oldShipment.setCreatedDate(order1CreationDate);

		order.addShipment(oldShipment);
		order.addShipment(newShipment);
		final List<Order> orderList = new LinkedList<>();
		orderList.add(order);
		orderList.add(secondOrder);

		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("listOrderStatus", asList(IN_PROGRESS, PARTIALLY_SHIPPED));
		parameters.put("shipmentStatus", INVENTORY_ASSIGNED);

		// expectations
		when(persistenceEngine.retrieveByNamedQuery("ORDERS_BY_ORDER_STATUS_AND_SHIPMENT_STATUS", parameters))
			.thenAnswer(answer -> orderList);
		when(persistenceEngine.get(OrderImpl.class, 1L)).thenAnswer(answer -> order);
		when(persistenceEngine.merge(any(Persistable.class))).thenReturn(order);
		when(mockPaymentService.adjustShipmentPayment(any(OrderShipment.class))).thenReturn(new PaymentResultImpl());

		this.orderServiceImpl.updateOrderShipmentStatus();

		assertThat(oldShipment.getShipmentStatus()).isEqualTo(RELEASED);
		assertThat(newShipment.getShipmentStatus()).isEqualTo(INVENTORY_ASSIGNED);
		assertThat(order2Shipment.getShipmentStatus()).isEqualTo(INVENTORY_ASSIGNED);

	}

	/** Test that you cannot cancel an order that is not cancellable. */
	@Test(expected = EpServiceException.class)
	public void testProcessOrderCancellationNotCancellable() {
		Order order = new OrderImpl() {
			private static final long serialVersionUID = 4330059855392807314L;

			@Override
			public boolean isCancellable() {
				return false;
			}
		};
		orderServiceImpl.processOrderCancellation(order);
	}

	/** Test that you cannot cancel an order that is not cancellable. */
	@Test
	public void testProcessOrderCancellationIsCancellableNoOrderSkus() {

		// Mock out getEventOriginator to make the test easier
		orderServiceImpl = new OrderServiceImpl() {
			@Override
			String getEventOriginator(final Order order) {
				return EVENT_ORIGINATOR_WS;
			}

			@Override
			protected void sanityCheck() {
				// Always be sane
			}

			@Override
			public Order update(final Order order) throws EpServiceException {
				return order;  // ignore persistence - not testing that
			}
		};
		orderServiceImpl.setEventMessageFactory(eventMessageFactory);
		orderServiceImpl.setEventMessagePublisher(eventMessagePublisher);

		// This order is cancellable
		Order order = new OrderImpl() {
			private static final long serialVersionUID = -392157231837612308L;

			@Override
			public boolean isCancellable() {
				return true;
			}
		};

		Order processedOrder = orderServiceImpl.processOrderCancellation(order);
		assertThat(processedOrder.getStatus()).isEqualTo(CANCELLED);
	}

	@Test
	public void verifyOrderCancelSendsEventMessage() {
		// Given
		OrderServiceImpl orderService = new OrderServiceImpl();
		Order order = mock(Order.class);
		given(order.isCancellable()).willReturn(true);

		EventMessageFactory eventMessageFactory = mock(EventMessageFactory.class);
		EventMessage eventMessage = mock(EventMessage.class);
		given(eventMessageFactory.createEventMessage(eq(OrderEventType.ORDER_CANCELLED), any(), any())).willReturn(eventMessage);
		orderService.setEventMessageFactory(eventMessageFactory);

		EventMessagePublisher eventMessagePublisher = mock(EventMessagePublisher.class);
		orderService.setEventMessagePublisher(eventMessagePublisher);

		PersistenceEngine persistenceEngine = mock(PersistenceEngine.class);
		given(persistenceEngine.merge(order)).willReturn(order);
		orderService.setPersistenceEngine(persistenceEngine);

		orderService.setStoreService(mock(StoreService.class));
		orderService.setTimeService(mock(TimeService.class));

		// When
		orderService.processOrderCancellation(order);

		// Then
		verify(eventMessagePublisher).publish(eventMessage);
	}

	@Test
	public void verifyHoldOrderSendsEventMessage() {
		ignorePersistence();
		EventMessage eventMessage = mock(EventMessage.class);
		when(eventMessageFactory.createEventMessage(OrderEventType.ORDER_HELD, ORDER_NUMBER, null)).thenReturn(eventMessage);

		orderServiceImpl.holdOrder(order);
		verify(eventMessagePublisher).publish(eventMessage);
	}

	/**
	 * Test that when a capture fails we deal with it nicely.
	 */
	@Test
	public void testProcessOrderShipment() {
		when(beanFactory.getBean(ContextIdNames.ORDER_SERVICE)).thenReturn(orderServiceImpl);

		final String orderNumber = "1";
		final String shipmentNumber = "2";
		final List<OrderShipment> shipmentList = new ArrayList<>();

		final PhysicalOrderShipmentImpl physicalOrderShipmentImpl = this.getMockPhysicalOrderShipment();
		physicalOrderShipmentImpl.setShipmentNumber(shipmentNumber);

		shipmentList.add(physicalOrderShipmentImpl);

		final Order order = new OrderImpl() {
			private static final long serialVersionUID = 5193999827719720358L;

			@Override
			public String getOrderNumber() {
				return orderNumber;
			}

		};
		order.setUidPk(1);
		order.setStoreCode(store.getCode());
		order.setCurrency(CAD);
		order.releaseOrder();
		order.addShipment(physicalOrderShipmentImpl);

		when(persistenceEngine.retrieveByNamedQuery(eq("PHYSICAL_SHIPMENT_BY_SHIPMENT_NUMBER"), anyString()))
			.thenAnswer(answer -> shipmentList);
		when(persistenceEngine.merge(any(Persistable.class))).thenReturn(order);
		final Order result = orderServiceImpl.processOrderShipment(shipmentNumber, null, null, eventOriginatorHelper.getSystemOriginator());
		verify(mockPaymentService).finalizeShipment(physicalOrderShipmentImpl);
		assertThat(result.getStatus()).as("Order should now be in the COMPLETED state.").isEqualTo(COMPLETED);
		assertThat(result.getAllShipments().iterator().next()
			.getShipmentStatus()).as("Shipment should not be in the SHIPPED state.").isEqualTo(SHIPPED);
	}

	/**
	 * Test method for {@link OrderServiceImpl#get(long, FetchGroupLoadTuner).
	 */
	@Test
	public void testGetWithFGLoadTuner() {
		final FetchGroupLoadTuner mockFGLoadTuner = mock(FetchGroupLoadTuner.class);

		when(persistenceEngine.get(OrderImpl.class, order.getUidPk())).thenAnswer(answer -> order);

		assertThat(orderServiceImpl.get(order.getUidPk(), mockFGLoadTuner)).isSameAs(order);
		assertStoreIsPopulated(store, order);

		final long nonExistUid = 3456L;
		when(persistenceEngine.get(OrderImpl.class, nonExistUid)).thenReturn(null);

		assertThat(orderServiceImpl.get(nonExistUid, mockFGLoadTuner)).isNull();
		assertThat(orderServiceImpl.get(0, mockFGLoadTuner).getUidPk()).isEqualTo(0);
		verify(persistenceEngine).get(OrderImpl.class, order.getUidPk());
	}

	/**
	 * Tests adding order return and appropriate index update notification sending.
	 */
	@Test
	public void testAddOrderReturn() {
		final OrderReturn orderReturn = new OrderReturnImpl();

		when(persistenceEngine.merge(order)).thenReturn(order);

		order.setStoreCode(store.getCode());
		final Order returned = orderServiceImpl.addOrderReturn(order, orderReturn);
		assertThat(returned.getReturns()).as("Return should have been added").isEqualTo(singleton(orderReturn));
		assertThat(returned.getStore()).as("Store should be populated on return").isEqualTo(store);
		verify(persistenceEngine).merge(order);
	}

	@Test
	public void testProcessRefundOrderPayment() {
		final double amount = 10.0;

		final PaymentGateway gateway = mock(PaymentGateway.class);
		store.setPaymentGateways(singleton(gateway));

		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		order.addShipment(shipment);
		shipment.setStatus(SHIPPED);

		final OrderPayment payment = new OrderPaymentImpl();
		payment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		payment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		payment.setAmount(new BigDecimal(amount));
		payment.setCurrencyCode("CAD");
		order.addOrderPayment(payment);

		final BigDecimal refundAmount = new BigDecimal(amount);
		EventOriginator originator = new EventOriginatorImpl();

		when(persistenceEngine.get(OrderImpl.class, order.getUidPk())).thenAnswer(answer -> order);
		when(persistenceEngine.merge(order)).thenReturn(order);
		when(mockPaymentService.refundShipmentPayment(any(OrderShipment.class), any(OrderPayment.class), any(BigDecimal.class)))
			.thenReturn(null);
		orderServiceImpl.processRefundOrderPayment(order.getUidPk(), shipment.getShipmentNumber(), payment, refundAmount, originator);
		verify(orderEventHelper).logOrderPaymentCaptured(order, payment);
	}

	@Test(expected = ReleaseShipmentFailedException.class)
	public void verifyFailureToReleaseOrderShipmentSendsEventMessage() {
		final String exceptionMessage = "Boom!";

		final OrderServiceImpl releaseShipmentFailingOrderServiceImpl = new OrderServiceImpl() {
			@Override
			protected OrderShipment processReleaseShipmentInternal(final OrderShipment orderShipment) throws ReleaseShipmentFailedException {
				throw new ReleaseShipmentFailedException(exceptionMessage);
			}
		};
		releaseShipmentFailingOrderServiceImpl.setEventMessageFactory(eventMessageFactory);
		releaseShipmentFailingOrderServiceImpl.setEventMessagePublisher(eventMessagePublisher);

		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		order.addShipment(shipment);

		final Map<String, Object> expectedEventMessageData = new HashMap<>();
		expectedEventMessageData.put("orderGuid", order.getGuid());
		expectedEventMessageData.put("shipmentType", shipment.getOrderShipmentType().toString());
		expectedEventMessageData.put("errorMessage", exceptionMessage);

		final EventMessage eventMessage = mock(EventMessage.class);
		when(eventMessageFactory.createEventMessage(OrderEventType.ORDER_SHIPMENT_RELEASE_FAILED,
			shipment.getShipmentNumber(), expectedEventMessageData)
		).thenReturn(eventMessage);

		releaseShipmentFailingOrderServiceImpl.processReleaseShipment(shipment);
		verify(eventMessageFactory).createEventMessage(OrderEventType.ORDER_SHIPMENT_RELEASE_FAILED,
			shipment.getShipmentNumber(), expectedEventMessageData);
		verify(eventMessagePublisher).publish(eventMessage);
	}

	@Test
	public void verifyCompleteShipmentSendsEventMessage() {
		final OrderService mockOrderService = mock(OrderService.class);

		when(beanFactory.getBean(ContextIdNames.ORDER_SERVICE)).thenReturn(mockOrderService);

		final String shipmentNumber = "shipment-1";
		final String trackingCode = "TRACK-001";
		final Date shipmentDate = new Date();

		final EventOriginator eventOriginator = eventOriginatorHelper.getSystemOriginator();

		final boolean sendConfEmail = true;

		final Order order = mock(Order.class);

		when(mockOrderService.processOrderShipment(shipmentNumber, trackingCode, shipmentDate, eventOriginator))
			.thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_NUMBER);

		final EventMessage eventMessage = mock(EventMessage.class);
		when(eventMessageFactory.createEventMessage(OrderEventType.ORDER_SHIPMENT_SHIPPED, shipmentNumber,
			ImmutableMap.of("orderGuid", ORDER_NUMBER))
		).thenReturn(eventMessage);


		orderServiceImpl.completeShipment(shipmentNumber, trackingCode, false, shipmentDate, sendConfEmail, eventOriginator);
		verify(eventMessagePublisher).publish(eventMessage);
		verify(eventMessageFactory).createEventMessage(OrderEventType.ORDER_SHIPMENT_SHIPPED, shipmentNumber,
			ImmutableMap.of("orderGuid", ORDER_NUMBER));
	}

	@Test
	public void shouldProcessOrderOnCheckoutAndMarkNewCustomerAsFirstTimeBuyer() {

		when(persistenceEngine.merge(order)).thenReturn(order);

		final Customer customer = new CustomerImpl();
		final EventOriginator originator = new EventOriginatorImpl();
		originator.setCustomer(customer);

		order.setCustomer(customer);
		order.setModifiedBy(originator);

		assertThat(customer.isFirstTimeBuyer()).as("New customer must be first time buyer").isTrue();

		final Order persistedOrder = orderServiceImpl.processOrderOnCheckout(order, false);

		assertThat(persistedOrder.getCustomer().isFirstTimeBuyer()).as("Customer with at least one order is not a first time buyer").isFalse();
	}

	@Test
	public void verifyReleaseOrderReturnsPersistedOrder() {
		final Order persistedOrder = mock(Order.class);
		when(persistenceEngine.merge(order)).thenReturn(persistedOrder);

		final Order actual = orderServiceImpl.releaseOrder(this.order);

		assertThat(actual).isSameAs(persistedOrder);
		verify(persistenceEngine).merge(order);
	}

	@Test
	public void verifyReleaseOrderSendsEventMessage() {
		ignorePersistence();

		EventMessage eventMessage = mock(EventMessage.class);
		when(eventMessageFactory.createEventMessage(OrderEventType.ORDER_RELEASED, ORDER_NUMBER, null)).thenReturn(eventMessage);

		orderServiceImpl.releaseOrder(order);
		verify(eventMessagePublisher).publish(eventMessage);
	}

	@Test
	public void verifyReleaseOrderLogsOrderHoldReleasedEventWhenInHoldState() {
		ignorePersistence();

		order.holdOrder();

		orderServiceImpl.releaseOrder(order);
		verify(orderEventHelper).logOrderHoldReleased(order);
	}

	@Test
	public void verifyReleaseOrderLogsOrderReleasedEvent() {
		ignorePersistence();

		orderServiceImpl.releaseOrder(order);
		verify(orderEventHelper).logOrderReleasedForFulfilment(order);
	}

	@Test
	public void verifyReleaseOrderThrowsExceptionWhenOrderNotInReleasableState() {
		order = createNonReleasableOrder();
		assertThatExceptionOfType(EpServiceException.class).isThrownBy(
				() -> orderServiceImpl.releaseOrder(order)).withMessageStartingWith("Cannot release order with status ");
	}

	@Test
	public void verifyShipmentsAreReleasedWhenOrderIsReleased() {
		final OrderService mockOrderService = mock(OrderService.class);
		when(beanFactory.getBean(ContextIdNames.ORDER_SERVICE)).thenReturn(mockOrderService);

		ignorePersistence();

		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		shipment.setCreatedDate(new Date(0L));
		shipment.setStatus(INVENTORY_ASSIGNED);

		order.addShipment(shipment);

		orderServiceImpl.releaseOrder(order);
		verify(mockOrderService).processReleaseShipment(shipment);
	}

	@Test
	public void verifyReleaseOrderCapturesElectronicShipmentPayments() {
		ignorePersistence();

		OrderShipment electronicShipment = getMockElectronicOrderShipment();
		order.addShipment(electronicShipment);

		OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setOrderShipment(electronicShipment);
		order.addOrderPayment(orderPayment);

		// Given we are trying to release an order previously held.
		order.holdOrder();

		orderServiceImpl.releaseOrder(this.order);
		verify(mockPaymentService).processShipmentPayment(electronicShipment);
	}

	@Test
	public void testCaptureNonPhysicalShipments() {
		ignorePersistence();

		OrderShipment electronicShipment = getMockElectronicOrderShipment();
		OrderShipment serviceShipment = getMockServiceOrderShipment();
		OrderShipment physicalShipment = getMockPhysicalOrderShipment();

		order.addShipment(electronicShipment);
		order.addShipment(serviceShipment);
		order.addShipment(physicalShipment);

		OrderPaymentImpl electronicShipmentPayment = new OrderPaymentImpl();
		electronicShipmentPayment.setOrderShipment(electronicShipment);
		order.addOrderPayment(electronicShipmentPayment);

		OrderPaymentImpl serviceShipmentPayment = new OrderPaymentImpl();
		serviceShipmentPayment.setOrderShipment(serviceShipment);
		order.addOrderPayment(serviceShipmentPayment);

		// Given we are trying to release an order previously held.
		order.holdOrder();

		orderServiceImpl.captureImmediatelyShippableShipments(order);
		verify(mockPaymentService).processShipmentPayment(electronicShipment);
		verify(mockPaymentService).processShipmentPayment(serviceShipment);
		verify(mockPaymentService, never()).processShipmentPayment(physicalShipment);
	}


	@Test
	public void verifyReleaseDoesntCapturePaymentsForElectronicItemsWhenOrderIsNotOnHold() {
		ignorePersistence();

		OrderShipment electronicShipment = getMockElectronicOrderShipment();
		order.addShipment(electronicShipment);

		OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setOrderShipment(electronicShipment);
		order.addOrderPayment(orderPayment);

		// Given the order was not on hold.

		// When
		orderServiceImpl.releaseOrder(this.order);

		// Then there should be no call to paymentService.processShipment
		verify(mockPaymentService, never()).processShipmentPayment(electronicShipment);
	}

	private void assertStoreIsPopulated(final Store expectedStore, final Order order) {
		//  Pointless PMD-enforced craziness
		assertThat(order.getStore()).as("Store should be populated").isEqualTo(expectedStore);
	}

	private void ignorePersistence() {
		when(persistenceEngine.merge(order)).thenReturn(order);
	}

	private Order createNonReleasableOrder() {
		return new OrderImpl() {
			private static final long serialVersionUID = -8676337089930838989L;

			@Override
			public boolean isReleasable() {
				return false;
			}
		};
	}

}
