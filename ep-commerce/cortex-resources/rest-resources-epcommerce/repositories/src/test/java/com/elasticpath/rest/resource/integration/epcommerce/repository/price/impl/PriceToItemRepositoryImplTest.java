/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

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
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;

/**
 * Test for {@link PriceToItemRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceToItemRepositoryImplTest {

	private final ItemIdentifier itemIdentifier = IdentifierTestFactory.buildItemIdentifier(SCOPE, SKU_CODE);

	@InjectMocks
	private PriceToItemRepositoryImpl<ItemIdentifier, PriceForItemIdentifier> repository;

	@Mock
	private PriceRepository priceRepository;

	@Test
	public void verifyGetElementsReturnsEmptyWhenPriceDoesNotExist() {
		when(priceRepository.priceExists(SCOPE, SKU_CODE)).thenReturn(Single.just(false));

		repository.getElements(itemIdentifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnsPriceForItemIdentifierWhenPriceExists() {
		when(priceRepository.priceExists(SCOPE, SKU_CODE)).thenReturn(Single.just(true));

		repository.getElements(itemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(priceForItemIdentifier -> priceForItemIdentifier.getItem().getItemId().getValue().get(ItemRepository.SKU_CODE_KEY)
						.equals(SKU_CODE));
	}
}
