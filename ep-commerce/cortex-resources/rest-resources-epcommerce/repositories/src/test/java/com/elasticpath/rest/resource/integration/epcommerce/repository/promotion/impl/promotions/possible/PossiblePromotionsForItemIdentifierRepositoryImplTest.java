/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.possible;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.promotions.PossiblePromotionsForItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for {@link PossiblePromotionsForItemIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PossiblePromotionsForItemIdentifierRepositoryImplTest {

	private final ItemIdentifier identifier = IdentifierTestFactory.buildItemIdentifier(SCOPE, SKU_CODE);

	@InjectMocks
	private PossiblePromotionsForItemIdentifierRepositoryImpl<ItemIdentifier, PossiblePromotionsForItemIdentifier> repository;

	@Mock
	private PromotionRepository promotionRepository;

	@Test
	public void verifyGetElementsReturnsEmptyWhenNoPromotionsAreFound() {
		when(promotionRepository.itemHasPossiblePromotions(SCOPE, SKU_CODE))
				.thenReturn(Single.just(false));

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnsPromotionIdentifiers() {
		when(promotionRepository.itemHasPossiblePromotions(SCOPE, SKU_CODE))
				.thenReturn(Single.just(true));

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertValue(possiblePromotionsForItemIdentifier ->
						possiblePromotionsForItemIdentifier.getItem().getItemId().getValue().get(ItemRepository.SKU_CODE_KEY)
								.equals(SKU_CODE));
	}
}
