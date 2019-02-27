/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.paymentmeans;

import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeansIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for the  {@link PaymentMeansEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentMeansEntityRepositoryImplTest {

	private static final String INVALID_PAYMENT_MEANS_ID = "invalidPaymentMeansId";
	private static final String PAYMENT_MEANS_ID = "1234567890"; //numbers only
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SCOPE = "scope";

	private static final String NO_BILLING_ADDRESS_FOR_PAYMENT_MEANS =
			String.format(PaymentMeansEntityRepositoryImpl.NO_BILLING_ADDRESS_FOR_PAYMENT_MEANS, PURCHASE_ID, SCOPE);
	private static final String NO_PAYMENT_MEANS_FOUND =
			String.format(PaymentMeansEntityRepositoryImpl.NO_PAYMENT_MEANS_FOUND, PURCHASE_ID, SCOPE);
	private static final String FAILED_TO_GET_PAYMENT_MEANS = PaymentMeansEntityRepositoryImpl.FAILED_TO_GET_PAYMENT_MEANS;

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private ConversionService conversionService;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;
	@InjectMocks
	private PaymentMeansEntityRepositoryImpl repository;
	private PurchasePaymentmeanIdentifier identifier;

	@Mock
	private Order order;
	@Mock
	private OrderAddress orderAddress;
	@Mock
	private OrderPayment orderPayment1;
	@Mock
	private OrderPayment orderPayment2;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
	}

	@Test
	public void testInvalidPaymentMeans() {
		setUpPurchasePaymentmeanIdentifier(INVALID_PAYMENT_MEANS_ID);

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(FAILED_TO_GET_PAYMENT_MEANS));
	}

	@Test
	public void testBillingAddressIsMissing() {
		setUpPurchasePaymentmeanIdentifier(PAYMENT_MEANS_ID);

		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
		when(order.getBillingAddress()).thenReturn(null);

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(NO_BILLING_ADDRESS_FOR_PAYMENT_MEANS));
	}

	@Test
	public void testPaymentMeansNotFoundInOrderPayments() {
		setUpPurchasePaymentmeanIdentifier(PAYMENT_MEANS_ID);

		Set<OrderPayment> orderPayments = new HashSet<>();
		orderPayments.add(orderPayment1);
		orderPayments.add(orderPayment2);
		when(orderPayment1.getUidPk()).thenReturn(1L);
		when(orderPayment2.getUidPk()).thenReturn(2L);

		when(order.getBillingAddress()).thenReturn(orderAddress);
		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
		when(order.getOrderPayments()).thenReturn(orderPayments);

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(NO_PAYMENT_MEANS_FOUND));
	}

	@Test
	public void testWithSuccess() {
		setUpPurchasePaymentmeanIdentifier(PAYMENT_MEANS_ID);
		Long paymentMeansId = Long.valueOf(PAYMENT_MEANS_ID);

		Set<OrderPayment> orderPayments = new HashSet<>();
		orderPayments.add(orderPayment1);
		orderPayments.add(orderPayment2);
		when(orderPayment2.getUidPk()).thenReturn(paymentMeansId);

		when(order.getBillingAddress()).thenReturn(orderAddress);
		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
		when(order.getOrderPayments()).thenReturn(orderPayments);

		PaymentMeansEntity result = PaymentMeansEntity.builder()
				.withPaymentMeansId(PAYMENT_MEANS_ID)
				.build();

		when(conversionService.convert(new Pair<>(orderPayment2, orderAddress), PaymentMeansEntity.class)).thenReturn(result);

		repository.findOne(identifier)
				.test()
				.assertValue(result);
	}

	private void setUpPurchasePaymentmeanIdentifier(final String paymentmeansId) {
		PurchasesIdentifier purchases = PurchasesIdentifier.builder()
				.withScope(StringIdentifier.of(SCOPE))
				.build();
		PurchaseIdentifier purchase = PurchaseIdentifier.builder()
				.withPurchaseId(StringIdentifier.of(PURCHASE_ID))
				.withPurchases(purchases)
				.build();
		PurchasePaymentmeansIdentifier paymentMeans = PurchasePaymentmeansIdentifier.builder()
				.withPurchase(purchase)
				.build();
		identifier = PurchasePaymentmeanIdentifier.builder()
				.withPaymentmeansId(StringIdentifier.of(paymentmeansId))
				.withPurchasePaymentmeans(paymentMeans)
				.build();
	}
}
