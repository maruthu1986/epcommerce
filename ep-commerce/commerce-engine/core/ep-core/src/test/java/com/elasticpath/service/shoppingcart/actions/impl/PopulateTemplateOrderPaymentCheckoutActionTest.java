/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import static org.junit.Assert.assertEquals;

import java.util.Currency;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.store.StoreService;

public class PopulateTemplateOrderPaymentCheckoutActionTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	protected StoreService storeService;

	private StoreImpl store;
	private PopulateTemplateOrderPaymentCheckoutAction checkoutAction;
	private CheckoutActionContextImpl checkoutActionContext;
	private OrderPaymentImpl orderPayment;
	private ShoppingCartImpl cart;
	private OrderImpl order;
	private PhysicalOrderShipmentImpl shipment;
	private CustomerImpl customer;

	@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
	@Before
	public void setUp() throws Exception {
		store = new StoreImpl();
		store.setCode("store");

		final CustomerSession customerSession = context.mock(CustomerSession.class);

		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
			allowing(customerSession).getIpAddress(); will(returnValue("1.2.3.4"));
		} });

		customer = new CustomerImpl();
		customer.setCustomerProfile(new CustomerProfileImpl());
		cart = new ShoppingCartImpl();

		order = new OrderImpl();
		order.setOrderNumber("123");
		order.setStoreCode(store.getCode());
		order.setCustomer(customer);
		order.setCurrency(Currency.getInstance("CAD"));
		shipment = new PhysicalOrderShipmentImpl();
		shipment.setOrder(order);
		orderPayment = new OrderPaymentImpl();
		orderPayment.setOrderShipment(shipment);


		checkoutActionContext = new CheckoutActionContextImpl(cart, null, customerSession, orderPayment, false, false, null);
		checkoutActionContext.setOrder(order);

		checkoutAction = new PopulateTemplateOrderPaymentCheckoutAction();
	}

	@Test
	public void testExecute() throws Exception {
		checkoutAction.execute(checkoutActionContext);

		assertEquals("Order Payment fields should be initialized based on the existing order",
				order.getOrderNumber(), orderPayment.getReferenceId());
	}
}
