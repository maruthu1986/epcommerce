/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.order.Order;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;

/**
 * Test class for {@link CreateNewOrderEventCheckoutAction}.
 */
public class CreateNewOrderEventCheckoutActionTest {

	private static final EventType EVENT_TYPE = OrderEventType.ORDER_CREATED;
	private static final String ORDER_NUMBER = "ORDER123";

	private CheckoutActionContextImpl checkoutContext;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final EventMessageFactory eventMessageFactory = context.mock(EventMessageFactory.class);
	private final EventMessagePublisher messagePublisher = context.mock(EventMessagePublisher.class);
	private final Order order = context.mock(Order.class);

	private CreateNewOrderEventCheckoutAction checkoutAction;

	@Before
	public void setUp() {
		checkoutAction = new CreateNewOrderEventCheckoutAction();
		checkoutAction.setEventMessageFactory(eventMessageFactory);
		checkoutAction.setEventMessagePublisher(messagePublisher);

		checkoutContext = new CheckoutActionContextImpl(null, null, null, null, false, false, null);
		checkoutContext.setOrder(order);

		context.checking(new Expectations() {
			{
				allowing(order).getOrderNumber();
				will(returnValue(ORDER_NUMBER));
			}
		});
	}

	@Test(expected = EpSystemException.class)
	public void verifyExecuteThrowsEpSystemExceptionOnPublishingError() throws Exception {
		context.checking(new Expectations() {
			{
				final EventMessage eventMessage = context.mock(EventMessage.class);
				allowing(eventMessageFactory).createEventMessage(with(any(EventType.class)), with(any(String.class)));
				will(returnValue(eventMessage));

				oneOf(messagePublisher).publish(with(any(EventMessage.class)));
				will(throwException(new Exception("Boom!")));
			}
		});

		checkoutAction.execute(checkoutContext);
	}

	@Test
	public void verifyExecutePublishesEventMessage() throws Exception {
		context.checking(new Expectations() {
			{
				final EventMessage eventMessage = context.mock(EventMessage.class);

				oneOf(eventMessageFactory).createEventMessage(EVENT_TYPE, ORDER_NUMBER);
				will(returnValue(eventMessage));

				oneOf(messagePublisher).publish(eventMessage);
			}
		});

		checkoutAction.execute(checkoutContext);
	}

}