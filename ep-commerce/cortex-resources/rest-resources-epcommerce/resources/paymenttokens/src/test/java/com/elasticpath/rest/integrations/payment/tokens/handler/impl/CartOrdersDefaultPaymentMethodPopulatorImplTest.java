/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

@RunWith(MockitoJUnitRunner.class)
public class CartOrdersDefaultPaymentMethodPopulatorImplTest {
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	private static final String STORE_CODE = "STORE_CODE";

	private final Collection<String> cartOrderGuids = new ArrayList<>();

	@Mock
	private CartOrderRepository cartOrderRepository;

	@InjectMocks
	private CartOrdersDefaultPaymentMethodPopulatorImpl cartOrdersDefaultPaymentMethodPopulatorImpl;

	@Mock
	private PaymentMethod mockPaymentMethod;

	@Mock
	private Customer mockCustomer;

	@Mock
	private CartOrder mockCartOrder;
	
	@Before
	public void setUp() {
		when(mockCustomer.getGuid()).thenReturn(CUSTOMER_GUID);
	}

	@Test
	public void testUpdateAllCartOrdersPaymentMethodsWhenPaymentMethodIsSetAsDefault() {
		setUpSuccessfulCartOrderRetrieval();
		when(mockCartOrder.getPaymentMethod()).thenReturn(null);

		cartOrdersDefaultPaymentMethodPopulatorImpl.updateAllCartOrdersPaymentMethods(mockCustomer, mockPaymentMethod, STORE_CODE);

		verifyPaymentMethodIsSavedOnCartOrder();

	}

	@Test
	public void testDoNotUpdateCartOrdersPaymentMethodsWhenPaymentMethodIsExisting() {
		setUpSuccessfulCartOrderRetrieval();
		when(mockCartOrder.getPaymentMethod()).thenReturn(mockPaymentMethod);

		cartOrdersDefaultPaymentMethodPopulatorImpl.updateAllCartOrdersPaymentMethods(mockCustomer, mockPaymentMethod, STORE_CODE);

		verifyPaymentMethodIsNotSetOnCartOrder();
	}

	@Test
	public void testNoAddressesSetWhenStoreNotValid() {
		when(cartOrderRepository.findCartOrderGuidsByCustomer(STORE_CODE, CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createNotFound());

		cartOrdersDefaultPaymentMethodPopulatorImpl.updateAllCartOrdersPaymentMethods(mockCustomer, mockPaymentMethod, STORE_CODE);

		verifyPaymentMethodIsNotSetOnCartOrder();
	}

	@Test
	public void testNoAddressesSetWhenNoCartOrdersFoundForCustomer() {
		allowingCartOrderGuidsByCustomer(ExecutionResultFactory.createNotFound());

		cartOrdersDefaultPaymentMethodPopulatorImpl.updateAllCartOrdersPaymentMethods(mockCustomer, mockPaymentMethod, STORE_CODE);

		verifyPaymentMethodIsNotSetOnCartOrder();
	}

	@Test
	public void testNoAddressesSetWhenNoCartOrderFoundForGuid() {
		cartOrderGuids.add(CART_ORDER_GUID);
		allowingCartOrderGuidsByCustomer(ExecutionResultFactory.createReadOK(cartOrderGuids));
		allowingCartOrderForGuid(ExecutionResultFactory.createNotFound());

		cartOrdersDefaultPaymentMethodPopulatorImpl.updateAllCartOrdersPaymentMethods(mockCustomer, mockPaymentMethod, STORE_CODE);

		verifyPaymentMethodIsNotSetOnCartOrder();
	}

	private void verifyPaymentMethodIsNotSetOnCartOrder() {
		verify(mockCartOrder, times(0)).usePaymentMethod(mockPaymentMethod);
		verify(cartOrderRepository, times(0)).saveCartOrder(mockCartOrder);
	}

	private void setUpSuccessfulCartOrderRetrieval() {
		cartOrderGuids.add(CART_ORDER_GUID);
		allowingCartOrderGuidsByCustomer(ExecutionResultFactory.createReadOK(cartOrderGuids));
		allowingCartOrderForGuid(ExecutionResultFactory.createReadOK(mockCartOrder));
	}

	private void allowingCartOrderForGuid(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(result);
	}

	private void allowingCartOrderGuidsByCustomer(final ExecutionResult<Collection<String>> result) {
		when(cartOrderRepository.findCartOrderGuidsByCustomer(STORE_CODE, CUSTOMER_GUID)).thenReturn(result);
	}

	private void verifyPaymentMethodIsSavedOnCartOrder() {
		verify(mockCartOrder, times(1)).usePaymentMethod(mockPaymentMethod);
		verify(cartOrderRepository, times(1)).saveCartOrder(mockCartOrder);
	}
}
