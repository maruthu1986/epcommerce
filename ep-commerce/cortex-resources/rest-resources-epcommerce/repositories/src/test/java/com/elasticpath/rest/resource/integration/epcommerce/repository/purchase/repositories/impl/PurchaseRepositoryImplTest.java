/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.domain.catalog.AvailabilityException;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformerFactory;
import com.elasticpath.service.shoppingcart.CheckoutService;

/**
 * Test for the  {@link PurchaseRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseRepositoryImplTest {

	@Mock
	private BeanFactory coreBeanFactory;
	@Mock
	private PaymentMethodTransformerFactory paymentMethodTransformerFactory;
	@Mock
	private CheckoutService checkoutService;
	@Mock
	private ExceptionTransformer exceptionTransformer;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@InjectMocks
	private PurchaseRepositoryImpl purchaseRepository;

	@Before
	public void setUp() {
		purchaseRepository.setCoreBeanFactory(coreBeanFactory);
		purchaseRepository.setPaymentMethodTransformerFactory(paymentMethodTransformerFactory);
		purchaseRepository.setCheckoutService(checkoutService);
		purchaseRepository.setReactiveAdapter(reactiveAdapter);
	}

	@Test
	public void testCheckout() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);
		CheckoutResults checkoutResults = mock(CheckoutResults.class);

		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true)).thenReturn(checkoutResults);

		purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment)
				.test()
				.assertNoErrors()
				.assertValue(checkoutResults);
	}

	@Test
	public void testCheckoutException() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true))
				.thenThrow(new EpServiceException("an exception"));

		purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment)
				.test()
				.assertError(ResourceOperationFailure.stateFailure("EpServiceException: an exception"));
	}

	@Test
	public void testCheckoutInvalidBusinessStateException() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		String productNotAvailableError = "error message";
		StructuredErrorMessage structuredErrorMessage = mock(StructuredErrorMessage.class);
		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true))
				.thenThrow(
						new AvailabilityException(
								productNotAvailableError,
								asList(structuredErrorMessage)
						)

				);
		ResourceOperationFailure failure = ResourceOperationFailure.stateFailure(productNotAvailableError);
		when(exceptionTransformer.getResourceOperationFailure(any(InvalidBusinessStateException.class)))
				.thenReturn(failure);

		purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment)
				.test()
				.assertError(failure);
	}

	@Test
	public void testCheckoutEpValidationException() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		String validationError = "validation error";
		StructuredErrorMessage structuredErrorMessage = mock(StructuredErrorMessage.class);
		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true))
				.thenThrow(
						new EpValidationException(
								validationError,
								asList(structuredErrorMessage)
						)

				);
		ResourceOperationFailure failure = ResourceOperationFailure.stateFailure(validationError);
		when(exceptionTransformer.getResourceOperationFailure(any(EpValidationException.class)))
				.thenReturn(failure);

		purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment)
				.test()
				.assertError(failure);
	}

	@Test
	public void testGetOrderPaymentFromPaymentMethod() {
		PaymentMethod paymentMethod = mock(PaymentMethod.class);
		PaymentMethodTransformer paymentMethodTransformer = mock(PaymentMethodTransformer.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		when(paymentMethodTransformerFactory.getTransformerInstance(paymentMethod)).thenReturn(paymentMethodTransformer);
		when(paymentMethodTransformer.transformToOrderPayment(paymentMethod)).thenReturn(orderPayment);

		purchaseRepository.getOrderPaymentFromPaymentMethod(paymentMethod)
				.test()
				.assertNoErrors()
				.assertValue(orderPayment);
	}

	@Test
	public void testGetOrderPaymentWithInvalidPaymentMethod() {
		PaymentMethod paymentMethod = mock(PaymentMethod.class);

		when(paymentMethodTransformerFactory.getTransformerInstance(paymentMethod)).thenThrow(new IllegalArgumentException());

		purchaseRepository.getOrderPaymentFromPaymentMethod(paymentMethod)
				.test()
				.assertError(ResourceOperationFailure.notFound("No PaymentMethodTransformer for payment method: " + paymentMethod));
	}

}