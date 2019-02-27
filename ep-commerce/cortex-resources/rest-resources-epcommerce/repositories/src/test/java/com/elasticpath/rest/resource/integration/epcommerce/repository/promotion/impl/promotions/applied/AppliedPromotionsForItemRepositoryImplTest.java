/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory.buildAppliedPromotionsForItemIdentifier;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForItemIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link AppliedPromotionsForItemRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedPromotionsForItemRepositoryImplTest {

	private final AppliedPromotionsForItemIdentifier appliedPromotionsForItemIdentifier = buildAppliedPromotionsForItemIdentifier();

	private static final int NUM_OF_ID = 2;

	@InjectMocks
	private AppliedPromotionsForItemRepositoryImpl<AppliedPromotionsForItemIdentifier, PromotionIdentifier> repository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyGetElementsReturnPromotionIdentifiers() {
		List<String> appliedPromotions = new ArrayList<>();
		for (int i = 0; i < NUM_OF_ID; i++) {
			appliedPromotions.add(String.valueOf(i));
		}

		when(promotionRepository.getAppliedPromotionsForItem(SCOPE, SKU_CODE))
				.thenReturn(Observable.fromIterable(appliedPromotions));

		repository.getElements(appliedPromotionsForItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("0"))
				.assertValueAt(1, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("1"));
	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenProductNotFound() {
		when(promotionRepository.getAppliedPromotionsForItem(SCOPE, SKU_CODE))
				.thenReturn(Observable.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.getElements(appliedPromotionsForItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenPromotionsAreEmpty() {
		when(promotionRepository.getAppliedPromotionsForItem(SCOPE, SKU_CODE))
				.thenReturn(Observable.empty());

		repository.getElements(appliedPromotionsForItemIdentifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}
}
