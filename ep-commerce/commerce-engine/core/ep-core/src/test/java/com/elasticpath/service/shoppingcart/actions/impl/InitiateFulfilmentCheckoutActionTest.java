/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * Test class for {@link InitiateFulfilmentCheckoutAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class InitiateFulfilmentCheckoutActionTest {

	@Mock
	private OrderService orderService;

	@InjectMocks
	private InitiateFulfilmentCheckoutAction checkoutAction;

	@Test
	public void verifyFulfilmentNotTriggeredWhenAwaitingExchange() {
		final Order order = mock(Order.class);
		final CheckoutActionContext actionContext = createCheckoutActionContext(order);

		when(order.getStatus()).thenReturn(OrderStatus.AWAITING_EXCHANGE);

		checkoutAction.execute(actionContext);

		verifyZeroInteractions(orderService);
	}

	@Test
	public void verifyFulfilmentTriggeredWhenNotAwaitingExchangeAndNotOnHold() {
		final Order order = mock(Order.class);
		final Order updatedOrder = mock(Order.class);
		final CheckoutActionContext actionContext = createCheckoutActionContext(order);

		when(order.getStatus()).thenReturn(OrderStatus.CREATED);
		when(orderService.releaseOrder(order)).thenReturn(updatedOrder);

		checkoutAction.execute(actionContext);

		assertSame("Expected the persisted order to be set to the context", updatedOrder, actionContext.getOrder());
		Mockito.verify(orderService, times(1)).releaseOrder(order);
	}




	@Test
	public void verifyFulfilmentNotTriggeredWhenOnHold() {
		final Order order = mock(Order.class);
		final CheckoutActionContext actionContext = createCheckoutActionContext(order);

		when(order.getStatus()).thenReturn(OrderStatus.ONHOLD);

		checkoutAction.execute(actionContext);

		assertSame("Expected the order not to be modified.", order, actionContext.getOrder());

		verifyZeroInteractions(orderService);
	}

	private CheckoutActionContext createCheckoutActionContext(final Order order) {
		final CheckoutActionContext checkoutActionContext = new CheckoutActionContextImpl(
				null,
				null,
				null,
				null,
				false,
				false,
				null
		);
		checkoutActionContext.setOrder(order);

		return checkoutActionContext;
	}

}