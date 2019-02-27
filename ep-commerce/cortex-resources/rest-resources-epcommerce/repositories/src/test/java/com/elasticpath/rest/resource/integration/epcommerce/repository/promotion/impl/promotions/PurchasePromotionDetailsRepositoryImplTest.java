/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory.buildPurchasePromotionIdentifier;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PurchasePromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link PurchasePromotionDetailsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchasePromotionDetailsRepositoryImplTest {

	private final PurchasePromotionIdentifier identifier = buildPurchasePromotionIdentifier();

	@Mock
	private PromotionEntity promotionEntity;

	@InjectMocks
	private PurchasePromotionDetailsRepositoryImpl<PromotionEntity, PurchasePromotionIdentifier> repository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyFindOneReturnsNotFoundWhenPromotionNotFound() {
		when(promotionRepository.getPromotionEntity(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
				ResourceTestConstants.PROMOTION_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.findOne(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsPromotionEntity() {
		when(promotionRepository.getPromotionEntity(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
				ResourceTestConstants.PROMOTION_ID)).thenReturn(Single.just(promotionEntity));

		repository.findOne(identifier)
				.test()
				.assertNoErrors()
				.assertValue(promotionEntity);
	}
}
