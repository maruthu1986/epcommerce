/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildPriceForItemIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test for {@link ItemPriceEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemPriceEntityRepositoryImplTest {

	private final PriceForItemIdentifier priceForItemIdentifier =
			buildPriceForItemIdentifier(SCOPE, SKU_CODE);

	@Mock
	private Price price;

	@InjectMocks
	private ItemPriceEntityRepositoryImpl<ItemPriceEntity, PriceForItemIdentifier> repository;

	@Mock
	private MoneyTransformer moneyTransformer;

	@Mock
	private PriceRepository priceRepository;

	@Test
	public void verifyFindOneReturnsNotFoundWhenGetPriceReturnsNotFound() {
		when(priceRepository.getPrice(SCOPE, SKU_CODE))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(priceForItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsItemPriceEntity() {
		CostEntity listCostEntity = CostEntity.builder().build();
		CostEntity purchaseCostEntity = CostEntity.builder().build();
		when(priceRepository.getPrice(SCOPE, SKU_CODE)).thenReturn(Single.just(price));
		when(moneyTransformer.transformToEntity(price.getListPrice())).thenReturn(listCostEntity);
		when(moneyTransformer.transformToEntity(price.getLowestPrice())).thenReturn(purchaseCostEntity);

		repository.findOne(priceForItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(itemPriceEntity -> itemPriceEntity.getListPrice().contains(listCostEntity)
						&& itemPriceEntity.getPurchasePrice().contains(purchaseCostEntity));
	}
}
