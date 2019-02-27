/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.impl.OrderRepositoryImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.order.OrderService;

/**
 * Test cases for {@link ShipmentRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentRepositoryImplTest {

	private static final String STORE_CODE = "TEST";

	private static final String ORDER_GUID = "test-order";

	private static final String SHIPMENT_GUID = "test-shipment";

	private static final String LINE_ITEM_GUID = "test-shipment-line-item";

	@Mock
	private OrderService orderService;

	@Mock
	private Order order;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private PhysicalOrderShipment orderShipment;

	@Mock
	private ProductSkuRepository productSkuRepository;

	private ShipmentRepositoryImpl shipmentRepositoryImpl;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@Before
	public void setUp() {
		shipmentRepositoryImpl = new ShipmentRepositoryImpl(orderService, orderRepository, reactiveAdapter, productSkuRepository);
	}

	@Test
	public void testFindWhenOrderShipmentNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(null);

		callFind()
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testFindForSuccess() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);

		callFind()
				.test()
				.assertNoErrors()
				.assertValue((PhysicalOrderShipment) orderShipment);
	}

	@Test
	public void testFindOtherCustomersShipment() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn("someone-elses-order-guid");

		callFind()
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testFindAllWhendOrderNotFound() {
		when(orderRepository.findByGuidAsSingle(STORE_CODE, ORDER_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(OrderRepositoryImpl.PURCHASE_NOT_FOUND)));

		callFindAll()
				.test()
				.assertError(createErrorCheckPredicate(OrderRepositoryImpl.PURCHASE_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}


	@Test
	public void testFindAllWhenNoShipmentFound() {
		when(orderRepository.findByGuidAsSingle(STORE_CODE, ORDER_GUID)).thenReturn(Single.just(order));
		when(order.getPhysicalShipments()).thenReturn(null);

		callFindAll()
				.test()
				.assertNoErrors()
				.assertNoValues();
	}


	@Test
	public void testFindAllWhenOneOrMoreShipmentsFound() {
		when(orderRepository.findByGuidAsSingle(STORE_CODE, ORDER_GUID)).thenReturn(Single.just(order));
		PhysicalOrderShipment physicalOrderShipment = mock(PhysicalOrderShipment.class);
		List<PhysicalOrderShipment> shipments = ImmutableList.of(physicalOrderShipment);
		when(order.getPhysicalShipments()).thenReturn(shipments);

		callFindAll()
				.test()
				.assertNoErrors()
				.assertValueSequence(shipments);
	}

	@Test
	public void testGetOrderSkusForShipment() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku orderSku = mock(OrderSku.class);
		expectedSkus.add(orderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);

		shipmentRepositoryImpl.getOrderSkusForShipment(STORE_CODE, ORDER_GUID, SHIPMENT_GUID)
				.test()
				.assertNoErrors()
				.assertValueSet(expectedSkus);
	}

	@Test
	public void testGetOrderSku() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);

		shipmentRepositoryImpl.getOrderSkuWithParentId(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, null)
				.test()
				.assertNoErrors()
				.assertValue(expectedOrderSku);
	}

	@Test
	public void testGetOrderSkuNoSkusFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);

		shipmentRepositoryImpl.getOrderSkuWithParentId(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, null)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.LINE_ITEM_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testGetOrderSkuNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(expectedOrderSku.getGuid()).thenReturn("not the sku you're looking for");
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);

		shipmentRepositoryImpl.getOrderSkuWithParentId(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, null)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.LINE_ITEM_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testGetOrderSkuBundleConstituentNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);

		shipmentRepositoryImpl.getOrderSkuWithParentId(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, "another-line-item")
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.LINE_ITEM_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testGetOrderSkuDependentNotFound() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);
		OrderSku parentOrderSku = mock(OrderSku.class);
		when(expectedOrderSku.getParent()).thenReturn(parentOrderSku);

		shipmentRepositoryImpl.getOrderSkuWithParentId(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, "another-line-item")
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.LINE_ITEM_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testGetOrderSkuDependent() {
		when(orderService.findOrderShipment(SHIPMENT_GUID, ShipmentType.PHYSICAL)).thenReturn(orderShipment);
		when(orderShipment.getOrder()).thenReturn(order);
		when(order.getGuid()).thenReturn(ORDER_GUID);
		Set<OrderSku> expectedSkus = new HashSet<>();
		OrderSku expectedOrderSku = mock(OrderSku.class);
		expectedSkus.add(expectedOrderSku);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(expectedSkus);
		when(expectedOrderSku.getGuid()).thenReturn(LINE_ITEM_GUID);
		OrderSku parentOrderSku = mock(OrderSku.class);
		when(expectedOrderSku.getParent()).thenReturn(parentOrderSku);
		String parentOrderSkuGuid = "another-line-item";
		when(parentOrderSku.getGuid()).thenReturn(parentOrderSkuGuid);

		shipmentRepositoryImpl.getOrderSkuWithParentId(STORE_CODE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID, parentOrderSkuGuid)
				.test()
				.assertNoErrors()
				.assertValue(expectedOrderSku);
	}

	private Single<PhysicalOrderShipment> callFind() {
		return shipmentRepositoryImpl.find(ORDER_GUID, SHIPMENT_GUID);
	}

	private Observable<PhysicalOrderShipment> callFindAll() {
		return shipmentRepositoryImpl.findAll(STORE_CODE, ORDER_GUID);
	}
}
