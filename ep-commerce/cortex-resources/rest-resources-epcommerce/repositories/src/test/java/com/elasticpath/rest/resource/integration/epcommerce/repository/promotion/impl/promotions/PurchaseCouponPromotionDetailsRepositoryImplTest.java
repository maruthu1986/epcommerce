/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory.buildPurchaseCouponPromotionIdentifier;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link PurchaseCouponPromotionDetailsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseCouponPromotionDetailsRepositoryImplTest {

	private final PurchaseCouponPromotionIdentifier identifier = buildPurchaseCouponPromotionIdentifier();

	@Mock
	private PromotionEntity promotionEntity;

	@InjectMocks
	private PurchaseCouponPromotionDetailsRepositoryImpl<PromotionEntity, PurchaseCouponPromotionIdentifier> repository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyFindOneReturnNotFoundWhenPromotionEntityNotFound() {
		when(promotionRepository.getPromotionEntity(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
				ResourceTestConstants.PROMOTION_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.findOne(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnPromotionEntity() {
		when(promotionRepository.getPromotionEntity(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
				ResourceTestConstants.PROMOTION_ID)).thenReturn(Single.just(promotionEntity));

		repository.findOne(identifier)
				.test()
				.assertNoErrors()
				.assertValue(promotionEntity);
	}
}
