/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.fulfillment.domain.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory.OrderPaymentGiftCertificatePresenter;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory.OrderPaymentPaypalExpressPresenter;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory.OrderPaymentReturnExchangePresenter;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Test class for OrderPaymentPresenterFactory.
 */
public class OrderPaymentPresenterFactoryTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	private final OrderPaymentPresenterFactory factory = new OrderPaymentPresenterFactory();

	@Mock
	private OrderPayment mockOrderPayment;

	/**
	 * Test that getOrderPaymentPresenter() returns the correct OrderPaymentPresenter when
	 * OrderPayment's PaymentType is gift cert.
	 */
	@Test
	public void testGetOrderPaymentPresenterGiftCert() {
		givenPaymentMethodOfType(PaymentType.GIFT_CERTIFICATE);
		assertTrue(factory.getOrderPaymentPresenter(mockOrderPayment) instanceof OrderPaymentGiftCertificatePresenter);
	}

	/**
	 * Test that getOrderPaymentPresenter() returns the correct OrderPaymentPresenter when
	 * OrderPayment's PaymentType is paypal express.
	 */
	@Test
	public void testGetOrderPaymentPresenterPaypalExpress() {
		givenPaymentMethodOfType(PaymentType.PAYPAL_EXPRESS);
		assertTrue(factory.getOrderPaymentPresenter(mockOrderPayment) instanceof OrderPaymentPaypalExpressPresenter);
	}

	/**
	 * Test that getOrderPaymentPresenter() returns the correct OrderPaymentPresenter when
	 * OrderPayment's PaymentType is return and exchange.
	 */
	@Test
	public void testGetOrderPaymentPresenterReturnAndExchange() {
		givenPaymentMethodOfType(PaymentType.RETURN_AND_EXCHANGE);
		assertTrue(factory.getOrderPaymentReturnExchangePresenter(mockOrderPayment) instanceof OrderPaymentReturnExchangePresenter);
	}

	private void givenPaymentMethodOfType(final PaymentType paymentType) {
		when(mockOrderPayment.getPaymentMethod()).thenReturn(paymentType);
	}
}
