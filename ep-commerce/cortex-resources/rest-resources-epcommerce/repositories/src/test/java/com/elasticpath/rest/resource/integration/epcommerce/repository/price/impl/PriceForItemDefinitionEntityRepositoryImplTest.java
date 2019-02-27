/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;

import java.util.Currency;
import java.util.Objects;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.PriceForItemdefinitionIdentifier;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test for {@link PriceForItemDefinitionEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceForItemDefinitionEntityRepositoryImplTest {

	private final PriceForItemdefinitionIdentifier priceForItemdefinitionIdentifier =
			IdentifierTestFactory.buildPriceForItemdefinitionIdentifier(SCOPE, SKU_CODE);

	@Mock
	private Price price;

	@InjectMocks
	private PriceForItemDefinitionEntityRepositoryImpl<PriceRangeEntity, PriceForItemdefinitionIdentifier> repository;

	@Mock
	private PriceRepository priceRepository;

	@Mock
	private MoneyTransformer moneyTransformer;

	@Mock
	private CostEntity costEntity;

	@Test
	public void verifyFindOneReturnsPriceRangeEntity() {
		Money money = Money.valueOf(1, Currency.getInstance("USD"));
		when(priceRepository.getLowestPrice(SKU_CODE)).thenReturn(Single.just(price));
		when(price.getLowestPrice()).thenReturn(money);
		when(moneyTransformer.transformToEntity(money)).thenReturn(costEntity);

		repository.findOne(priceForItemdefinitionIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(Objects::nonNull);
	}

	@Test
	public void verifyFindOneReturnsNotFoundWhenLowestPriceNotFound() {
		when(priceRepository.getLowestPrice(SKU_CODE))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(priceForItemdefinitionIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}
}
