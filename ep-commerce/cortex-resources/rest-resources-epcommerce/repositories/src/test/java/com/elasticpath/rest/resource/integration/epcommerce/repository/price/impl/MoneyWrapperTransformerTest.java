/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static java.util.Locale.CANADA;
import static java.util.Locale.ENGLISH;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test for {@link MoneyWrapperTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MoneyWrapperTransformerTest {

	private static final Locale LOCALE = ENGLISH;
	private static final Currency CURRENCY = Currency.getInstance(CANADA);

	private final BigDecimal aDouble = BigDecimal.valueOf(42d);
	private final Money purchasePriceMoney = Money.valueOf(aDouble, CURRENCY);

	private final BigDecimal amount = BigDecimal.valueOf(128d);
	private final Money listPriceMoney = Money.valueOf(amount, CURRENCY);

	@Mock
	private MoneyTransformer mockMoneyTransformer;

	@InjectMocks
	private MoneyWrapperTransformer moneyWrapperTransformer;

	@Before
	public void setUp() {

		moneyWrapperTransformer = new MoneyWrapperTransformer(mockMoneyTransformer);

		when(mockMoneyTransformer.transformToEntity(purchasePriceMoney, LOCALE))
				.thenReturn(ResourceTypeFactory.createResourceEntity(CostEntity.class));

		when(mockMoneyTransformer.transformToEntity(listPriceMoney, LOCALE))
				.thenReturn(ResourceTypeFactory.createResourceEntity(CostEntity.class));
	}

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {

		moneyWrapperTransformer.transformToDomain(null);
	}

	/**
	 * Tests where purchase price equals to list price.
	 */
	@Test
	public void testPurchasePriceIsEqualToListPrice() {

		CartLineItemPriceEntity result = moneyWrapperTransformer.transformToEntity(
				new MoneyWrapper()
						.setPurchasePrice(purchasePriceMoney),
				LOCALE
		);

		assertSingleCostEntity(ResourceTypeFactory.createResourceEntity(CostEntity.class), result.getPurchasePrice());
		assertNull("there should not be any list price", result.getListPrice());
	}

	/**
	 * Tests where purchase price less than list price.
	 */
	@Test
	public void testPurchasePriceIsLessThanListPrice() {

		CartLineItemPriceEntity result = moneyWrapperTransformer.transformToEntity(
				new MoneyWrapper()
						.setPurchasePrice(purchasePriceMoney)
						.setListPrice(listPriceMoney),
				LOCALE
		);

		assertSingleCostEntity(ResourceTypeFactory.createResourceEntity(CostEntity.class), result.getPurchasePrice());
		assertSingleCostEntity(ResourceTypeFactory.createResourceEntity(CostEntity.class), result.getListPrice());
	}

	private static void assertSingleCostEntity(final CostEntity expectedCostEntity, final List<CostEntity> resultCostEntityEntities) {
		assertThat("costEntity should be equal", resultCostEntityEntities, contains(expectedCostEntity));
	}
}
