/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 *
 */
package com.elasticpath.cmclient.fulfillment.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory.OrderPaymentPaypalExpressPresenter;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Test class for OrderPaymentPaypalExpressPresenter.
 */
public class OrderPaymentPaypalExpressPresenterTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	/**
	 * Test that the payment details will just be the orderPayment's email address.
	 */
	@Test
	public void testGetDisplayPaymentDetails() {
		final String emailAddress = "demo@demo.elasticpath.com"; //$NON-NLS-1$
		final OrderPayment mockOrderPayment = mock(OrderPayment.class);
		when(mockOrderPayment.getPaymentMethod()).thenReturn(PaymentType.PAYPAL_EXPRESS);
		when(mockOrderPayment.getEmail()).thenReturn(emailAddress);

		OrderPaymentPaypalExpressPresenter presenter =
			new OrderPaymentPresenterFactory().new OrderPaymentPaypalExpressPresenter(mockOrderPayment);

		assertEquals(emailAddress, presenter.getDisplayPaymentDetails());
	}
}
