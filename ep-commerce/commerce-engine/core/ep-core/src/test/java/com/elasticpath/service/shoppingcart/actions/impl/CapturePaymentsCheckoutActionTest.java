/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * Unit test for {@link CapturePaymentsCheckoutAction}.
 */

@RunWith(MockitoJUnitRunner.class)
public class CapturePaymentsCheckoutActionTest {

	private static final String AUTHORIZATION_CODE = "AN AUTHORIZATION CODE";

	@Mock
	private OrderService orderService;

	@InjectMocks
	private CapturePaymentsCheckoutAction checkoutAction;


	@Test
	public void verifyCaptureNotTriggeredWhenOnHold() {
		final Order order = mock(Order.class);
		final CheckoutActionContext checkoutActionContext = mock(CheckoutActionContext.class);
		given(checkoutActionContext.getOrder()).willReturn(order);

		when(order.getStatus()).thenReturn(OrderStatus.ONHOLD);

		checkoutAction.execute(checkoutActionContext);

		verify(orderService, never()).captureImmediatelyShippableShipments(any());
	}

	@Test
	public void verifyCaptureTriggeredWhenCreated() {
		final Order order = mock(Order.class);
		final Order updatedOrder = mock(Order.class);
		final CheckoutActionContext checkoutActionContext = mock(CheckoutActionContext.class);
		given(checkoutActionContext.getOrder()).willReturn(order);
		given(checkoutActionContext.getOrderPaymentList()).willReturn(Collections.emptyList());

		when(order.getStatus()).thenReturn(OrderStatus.CREATED);
		when(orderService.update(order)).thenReturn(updatedOrder);

		checkoutAction.execute(checkoutActionContext);

		verify(orderService).captureImmediatelyShippableShipments(order);
	}

	@Test
	public void verifyTransientOrderPaymentPropertiesArePreserved() {
		final Order order = mock(Order.class);
		final Order updatedOrder = mock(Order.class);
		final OrderPayment orderPayment = new OrderPaymentImpl();
		final Collection<OrderPayment> orderPayments = Arrays.asList(orderPayment);

		orderPayment.setAuthorizationCode(AUTHORIZATION_CODE);

		final CheckoutActionContext actionContext = mock(CheckoutActionContext.class);
		when(actionContext.getOrder()).thenReturn(order);
		when(actionContext.getOrderPaymentList()).thenReturn(orderPayments);

		when(order.getStatus()).thenReturn(OrderStatus.CREATED);
		when(orderService.update(eq(order))).thenReturn(updatedOrder);

		checkoutAction.execute(actionContext);

		verify(actionContext, atLeast(1)).preserveTransientOrderPayment(
				eq(orderPayments));
	}
}
