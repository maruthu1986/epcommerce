/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.possible;

import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForCartIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link PossiblePromotionsForCartIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PossiblePromotionsForCartIdentifierRepositoryImplTest {

	private final CartIdentifier cartIdentifier = IdentifierTestFactory.buildCartIdentifier(ResourceTestConstants.SCOPE, ResourceTestConstants
			.CART_ID);

	@InjectMocks
	private PossiblePromotionsForCartIdentifierRepositoryImpl<CartIdentifier, PossiblePromotionsForCartIdentifier> repository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyGetElementsReturnPossiblePromotionWhenExists() {
		when(promotionRepository.cartHasPossiblePromotions(ResourceTestConstants.SCOPE, ResourceTestConstants.CART_ID)).thenReturn(Single.just(true));

		repository.getElements(cartIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(possiblePromotionsForCartIdentifier ->
						possiblePromotionsForCartIdentifier.getCart().getCartId().getValue().equals(ResourceTestConstants.CART_ID));
	}

	@Test
	public void verifyGetElementsReturnPossiblePromotionWhenNotExists() {
		when(promotionRepository.cartHasPossiblePromotions(ResourceTestConstants.SCOPE, ResourceTestConstants.CART_ID))
				.thenReturn(Single.just(false));

		repository.getElements(cartIdentifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}
}
