/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers.store;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentGatewayType;

/**
 * Unit test class for StoreEditorModel. Also see StoreEditorModelHelperTest.
 */
public class StoreEditorModelTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private Store store;

	private StoreEditorModel editorModel;
	private final Map<PaymentGatewayType, PaymentGateway> paymentGatewayMap = new HashMap<PaymentGatewayType, PaymentGateway>();

	@Mock
	private PaymentGateway mockPaymentGateway;

	/**
	 * Set up {@link StoreEditorModel} and mock store.
	 */
	@Before
	public void setUp() {
		editorModel = new StoreEditorModel(store);
	}
	

	/**
	 * Ensure is payment method selected returns false with no payment method selected.
	 */
	@Test
	public void ensureIsPaymentMethodSelectedReturnsFalseWithNoPaymentMethodSelected() {
		when(store.getPaymentGatewayMap()).thenReturn(Collections.<PaymentGatewayType, PaymentGateway>emptyMap());
		when(store.getCreditCardTypes()).thenReturn(Collections.<CreditCardType>emptySet());

		assertFalse("Payment method should not be selected.", editorModel.isPaymentMethodSelected()); //$NON-NLS-1$
	}
	
	/**
	 * Ensure is payment method selected returns false with only credit card gateway selected.
	 */
	@Test
	public void ensureIsPaymentMethodSelectedReturnsFalseWithOnlyCreditCardGatewaySelected() {
		paymentGatewayMap.put(PaymentGatewayType.CREDITCARD, mockPaymentGateway);
		when(store.getPaymentGatewayMap()).thenReturn(paymentGatewayMap);
		when(store.getCreditCardTypes()).thenReturn(Collections.<CreditCardType>emptySet());

		assertFalse("Payment method should not be selected.", editorModel.isPaymentMethodSelected()); //$NON-NLS-1$
	}
	
	/**
	 * Ensure is payment method selected returns false with only credit card types selected.
	 */
	@Test
	public void ensureIsPaymentMethodSelectedReturnsFalseWithOnlyCreditCardTypesSelected() {
		final CreditCardType mockCreditCardType = mock(CreditCardType.class);
		when(store.getPaymentGatewayMap()).thenReturn(Collections.<PaymentGatewayType, PaymentGateway>emptyMap());
		when(store.getCreditCardTypes()).thenReturn(Collections.singleton(mockCreditCardType));

		assertFalse("Payment method should not be selected.", editorModel.isPaymentMethodSelected()); //$NON-NLS-1$
	}
	
	
	/**
	 * Ensure is payment method selected returns true with pay pal payment gateway selected.
	 */
	@Test
	public void ensureIsPaymentMethodSelectedReturnsTrueWithPayPalPaymentGatewaySelected() {
		when(store.getPaymentGatewayMap()).thenReturn(Collections.singletonMap(PaymentGatewayType.PAYPAL_EXPRESS, mockPaymentGateway));
		when(store.getCreditCardTypes()).thenReturn(Collections.<CreditCardType>emptySet());

		assertTrue("At least one PaymentGateway is selected.", editorModel.isPaymentMethodSelected()); //$NON-NLS-1$
	}

	/**
	 * Ensure is payment method selected returns true with payment token payment gateway selected.
	 */
	@Test
	public void ensureIsPaymentMethodSelectedReturnsTrueWithPaypalHostedPaymentGatewaySelected() {
		when(store.getPaymentGatewayMap()).thenReturn(Collections.singletonMap(PaymentGatewayType.HOSTED_PAGE, mockPaymentGateway));
		when(store.getCreditCardTypes()).thenReturn(Collections.<CreditCardType>emptySet());

		assertTrue("At least one PaymentGateway is selected.", editorModel.isPaymentMethodSelected()); //$NON-NLS-1$
	}
}
