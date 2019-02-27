/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for {@link CreditCardDirectPostPaymentHandler}.
 */
public class CreditCardDirectPostPaymentHandlerTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private TimeService timeService;
	private CreditCardDirectPostPaymentHandler creditCardDirectPostPaymentHandler;

	/**
	 * Setup.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		timeService = context.mock(TimeService.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ORDER_PAYMENT, OrderPaymentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.TIME_SERVICE, timeService);

		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});

		creditCardDirectPostPaymentHandler = new CreditCardDirectPostPaymentHandler();
	}

	/**
	 * Clean up expectations factory.
	 */
	@After
	public void tearDown() throws Exception {
		expectationsFactory.close();
	}

	/**
	 * Ensure that generateAuthorizeOrderPayments creates an order-based order payment with the appropriate values.
	 * @throws Exception if an exception is raised
	 */
	@Test
	public void testGenerateAuthorizeOrderPayments() throws Exception {
		final OrderPayment templateOrderPayment = new OrderPaymentImpl();
		templateOrderPayment.setAuthorizationCode("authCode");
		final Order order = context.mock(Order.class);
		context.checking(new Expectations() {
			{
				allowing(order);
			}
		});
		Collection<OrderPayment> orderPayments = creditCardDirectPostPaymentHandler.generateAuthorizeOrderPayments(templateOrderPayment, order);
		assertEquals(1, orderPayments.size());
		OrderPayment orderPayment = orderPayments.iterator().next();
		assertEquals("authCode", orderPayment.getAuthorizationCode());
		assertEquals(order, orderPayment.getOrder());
		assertNull(orderPayment.getOrderShipment());
	}
}
