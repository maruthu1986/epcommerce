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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.eclipse.rap.rwt.testfixture.TestContext;

import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory.OrderPaymentGiftCertificatePresenter;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Tests for the OrderPaymentGiftCertificatePresenter class.
 */
public class OrderPaymentGiftCertificatePresenterTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();

	@Mock
	private OrderPayment mockOrderPayment;

	/**
	 * Setup the tests.
	 * @throws Exception on error.
	 */
	@Before
	public void setUp() throws Exception {
		when(mockOrderPayment.getPaymentMethod()).thenReturn(PaymentType.GIFT_CERTIFICATE);
	}

	/**
	 * Test that if the user is not authorized to see credit card numbers OR is not
	 * authorized to see the OrderPayment's Store, the gift certificate's
	 * masked number will be presented.
	 */
	@Test
	public void testGetDisplayPaymentDetailsNotAuthorized() {
		final String maskedGcCode = "MaskedGcCode"; //$NON-NLS-1$
		final GiftCertificate mockGiftCertificate = mock(GiftCertificate.class);
		when(mockGiftCertificate.displayMaskedGiftCertificateCode()).thenReturn(maskedGcCode);
		when(mockOrderPayment.getGiftCertificate()).thenReturn(mockGiftCertificate);

		OrderPaymentGiftCertificatePresenter presenter =
			new OrderPaymentPresenterFactory().new OrderPaymentGiftCertificatePresenter(mockOrderPayment) {
			@Override
			public boolean isAuthorized() {
				return false;
			}
		};

		assertEquals(FulfillmentMessages.get().PaymentType_GiftCertificate + ' ' + maskedGcCode, presenter.getDisplayPaymentDetails());
	}

	/**
	 * Test that if the user IS authorized to see credit card numbers AND is
	 * authorized to see the OrderPayment's Store, the gift certificate's
	 * masked number will be presented.
	 */
	@Test
	public void testGetDisplayPaymentDetailsAuthorized() {
		final String gcCode = "MyGcCode"; //$NON-NLS-1$
		final GiftCertificate mockGiftCertificate = mock(GiftCertificate.class);
		when(mockGiftCertificate.displayGiftCertificateCode()).thenReturn(gcCode);
		when(mockOrderPayment.getGiftCertificate()).thenReturn(mockGiftCertificate);

		OrderPaymentGiftCertificatePresenter presenter =
			new OrderPaymentPresenterFactory().new OrderPaymentGiftCertificatePresenter(mockOrderPayment) {
			@Override
			public boolean isAuthorized() {
				return true;
			}
		};

		assertEquals(FulfillmentMessages.get().PaymentType_GiftCertificate + ' ' + gcCode, presenter.getDisplayPaymentDetails());
	}
}
