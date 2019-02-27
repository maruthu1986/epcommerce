/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory
		.buildAppliedPromotionsForPurchaseIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForPurchaseIdentifier;
import com.elasticpath.rest.definition.promotions.PurchasePromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link AppliedPromotionsForPurchaseRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedPromotionsForPurchaseRepositoryImplTest {

	private static final int NUM_OF_ID = 2;

	private final AppliedPromotionsForPurchaseIdentifier identifier = buildAppliedPromotionsForPurchaseIdentifier();

	@Mock
	private Order order;

	@InjectMocks
	private AppliedPromotionsForPurchaseRepositoryImpl<AppliedPromotionsForPurchaseIdentifier, PurchasePromotionIdentifier> repository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyGetElementsReturnNotFoundWhenOrderIsNotFound() {
		when(orderRepository.findByGuidAsSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenPromotionsNotFound() {
		when(orderRepository.findByGuidAsSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID)).thenReturn(Single.just(order));
		when(promotionRepository.getAppliedPromotionsForPurchase(order)).thenReturn(Collections.emptyList());

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnPurchasePromotionIdentifiers() {
		List<String> appliedPromotions = new ArrayList<>();
		for (int i = 0; i < NUM_OF_ID; i++) {
			appliedPromotions.add(String.valueOf(i));
		}

		when(orderRepository.findByGuidAsSingle(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID)).thenReturn(Single.just(order));
		when(promotionRepository.getAppliedPromotionsForPurchase(order)).thenReturn(appliedPromotions);

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, purchasePromotionIdentifier -> purchasePromotionIdentifier.getPromotionId().getValue().equals("0"))
				.assertValueAt(1, purchasePromotionIdentifier -> purchasePromotionIdentifier.getPromotionId().getValue().equals("1"));
	}
}
