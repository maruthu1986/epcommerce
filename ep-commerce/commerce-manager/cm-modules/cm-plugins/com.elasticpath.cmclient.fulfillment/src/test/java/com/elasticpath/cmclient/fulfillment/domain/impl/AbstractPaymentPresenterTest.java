/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.eclipse.rap.rwt.testfixture.TestContext;

import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.OrderPaymentPresenter;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;

/**
* Test class for the AbstractPaymentPresenter.
 */
public class AbstractPaymentPresenterTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();

	@Mock
	private OrderPayment mockOrderPayment;

	private OrderPaymentPresenter presenter;

	/**
	 * Setup the tests.
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		presenter = new OrderPaymentPresenterFactory().new AbstractOrderPaymentPresenter(mockOrderPayment) {
			@Override
			public String getDisplayPaymentDetails() {
				return null; //not testing
			}
		};
	}

	/**
	 * Test that if the OrderPayment's OrderShipment is null, the ShipmentId is
	 * FulfillmentMessages.get().PaymentHistorySection_NotApplicable.
	 */
	@Test
	public void testDisplayShipmentIdNullOrderShipment() {
		when(mockOrderPayment.getOrderShipment()).thenReturn(null);
		when(mockOrderPayment.isPaymentForSubscriptions()).thenReturn(false);
		assertEquals(FulfillmentMessages.get().PaymentHistorySection_NotApplicable, presenter.getDisplayShipmentId());
	}

	/**
	 * Test that if the OrderPayment's Shipment is ELECTRONIC, the ShipmentId is
	 * FulfillmentMessages.get().OrderPaymentHistorySection_ElectronicShipmentId.
	 */
	@Test
	public void testDisplayShipmentIdElectronic() {
		final OrderShipment mockOrderShipment = mock(OrderShipment.class);
		when(mockOrderShipment.getOrderShipmentType()).thenReturn(ShipmentType.ELECTRONIC);
		when(mockOrderPayment.getOrderShipment()).thenReturn(mockOrderShipment);
		when(mockOrderPayment.isPaymentForSubscriptions()).thenReturn(false);

		assertEquals(FulfillmentMessages.get().OrderPaymentHistorySection_ElectronicShipmentId, presenter.getDisplayShipmentId());
	}

	/**
	 * Test that if the OrderPayment's Shipment is PHYSICAL, the ShipmentId is
	 * just the shipment number.
	 */
	@Test
	public void testDisplayShipmentIdPhysical() {
		final String shipmentNumber = "12345"; //$NON-NLS-1$
		final OrderShipment mockOrderShipment = mock(OrderShipment.class);
		when(mockOrderShipment.getOrderShipmentType()).thenReturn(ShipmentType.PHYSICAL);
		when(mockOrderShipment.getShipmentNumber()).thenReturn(shipmentNumber);
		when(mockOrderPayment.getOrderShipment()).thenReturn(mockOrderShipment);
		when(mockOrderPayment.isPaymentForSubscriptions()).thenReturn(false);
		assertEquals(shipmentNumber, presenter.getDisplayShipmentId());
	}
}
