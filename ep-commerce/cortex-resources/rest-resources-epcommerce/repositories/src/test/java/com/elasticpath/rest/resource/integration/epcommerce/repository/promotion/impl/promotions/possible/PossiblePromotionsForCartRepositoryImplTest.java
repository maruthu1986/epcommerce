/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.possible;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionTestFactory.buildPossiblePromotionsForCartIdentifier;

import com.google.common.collect.ImmutableList;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link PossiblePromotionsForCartRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PossiblePromotionsForCartRepositoryImplTest {

	private final PossiblePromotionsForCartIdentifier identifier = buildPossiblePromotionsForCartIdentifier();

	@InjectMocks
	private PossiblePromotionsForCartRepositoryImpl<PossiblePromotionsForCartIdentifier, PromotionIdentifier> repository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyGetElementsReturnsNotFoundWhenPromotionsNotFound() {
		when(promotionRepository.getPossiblePromotionsForCart(ResourceTestConstants.SCOPE, ResourceTestConstants.CART_ID))
				.thenReturn(Observable.error(ResourceOperationFailure.notFound(ResourceTestConstants.NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(ResourceTestConstants.NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnsPromotionIdentifiers() {
		when(promotionRepository.getPossiblePromotionsForCart(ResourceTestConstants.SCOPE, ResourceTestConstants.CART_ID))
				.thenReturn(Observable.fromIterable(ImmutableList.of("0", "1")));

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("0"))
				.assertValueAt(1, promotionIdentifier -> promotionIdentifier.getPromotionId().getValue().equals("1"));
	}

}
