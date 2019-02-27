/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Maybe;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseNumberEntity;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.impl.OrderRepositoryImpl;

/**
 * Test for {@link PurchaseNumberEntityPurchaseIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseNumberEntityPurchaseIdentifierRepositoryImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String PURCHASE_NUMBER = "123456";
	private static final String USER_ID = "78910";

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private PurchaseNumberEntityPurchaseIdentifierRepositoryImpl<PurchaseNumberEntity, PurchaseIdentifier> fixture;

	@Test
	public void testReturnPurchaseNumberIdentifierSuccess() {
		fixture.setOrderRepository(orderRepository);
		fixture.setResourceOperationContext(resourceOperationContext);

		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(orderRepository.findByGuidAndCustomerGuid(SCOPE, PURCHASE_NUMBER, USER_ID)).thenReturn(Maybe.just(PURCHASE_NUMBER));

		fixture.submit(buildPurchaseNumberEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertNoErrors()
				.assertValue(submitResult -> submitResult.getIdentifier().getPurchaseId().getValue().equals(PURCHASE_NUMBER))
				.assertValue(submitResult -> submitResult.getIdentifier().getPurchases().getScope().getValue().equals(SCOPE));
	}

	@Test
	public void testReturnPurchaseNumberIdentifierNotFound() {
		fixture.setOrderRepository(orderRepository);
		fixture.setResourceOperationContext(resourceOperationContext);

		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(orderRepository.findByGuidAndCustomerGuid(SCOPE, PURCHASE_NUMBER, USER_ID)).thenReturn(Maybe.empty());

		fixture.submit(buildPurchaseNumberEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertNoValues()
				.assertErrorMessage(String.format(OrderRepositoryImpl.PURCHASE_NOT_FOUND, PURCHASE_NUMBER, SCOPE));
	}

	public PurchaseIdentifier getPurchaseIdentifier() {
		return PurchaseIdentifier.builder()
				.withPurchaseId(StringIdentifier.of(PURCHASE_NUMBER))
				.withPurchases(PurchasesIdentifier.builder()
						.withScope(StringIdentifier.of(SCOPE))
						.build())
				.build();
	}

	private PurchaseNumberEntity buildPurchaseNumberEntity() {
		return PurchaseNumberEntity.builder()
				.withPurchaseNumber(PURCHASE_NUMBER)
				.build();
	}
}
