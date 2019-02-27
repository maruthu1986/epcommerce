/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.totals.impl;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.totals.CartTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;

/**
 * Test for {@link CartTotalEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartTotalEntityRepositoryImplTest {

	private static final String SCOPE = "scope";
	private static final String CART_ID = "cart_id";
	private static final Money TEN_CAD = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));

	@Mock
	private TotalsCalculator totalsCalculator;

	@Mock
	private ConversionService conversionService;

	@InjectMocks
	private CartTotalEntityRepositoryImpl<TotalEntity, CartTotalIdentifier> cartTotalEntityRepository;

	@Test
	public void shouldGetTotal() {
		TotalEntity totalEntity = createTotalEntity();
		when(totalsCalculator.calculateTotalForShoppingCart(SCOPE, CART_ID)).thenReturn(Single.just(TEN_CAD));
		when(conversionService.convert(TEN_CAD, TotalEntity.class)).thenReturn(totalEntity);

		cartTotalEntityRepository.findOne(createCartTotalIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(totalEntity);
	}

	@Test
	public void shouldNotGetTotal() {
		when(totalsCalculator.calculateTotalForShoppingCart(SCOPE, CART_ID))
				.thenReturn(Single.error(ResourceOperationFailure.badRequestBody()));
		cartTotalEntityRepository.findOne(createCartTotalIdentifier())
				.test()
				.assertError(ResourceOperationFailure.badRequestBody());
	}


	private TotalEntity createTotalEntity() {
		final CostEntity costEntity = CostEntity.builder()
				.withAmount(TEN_CAD.getAmount())
				.withCurrency(TEN_CAD.getCurrency().getDisplayName())
				.withDisplay(TEN_CAD.getAmount().toString())
				.build();

		return TotalEntity.builder()
				.withCost(Collections.singletonList(costEntity))
				.build();
	}


	private CartTotalIdentifier createCartTotalIdentifier() {
		final CartIdentifier cartIdentifier = CartIdentifier.builder()
				.withCartId(StringIdentifier.of(CART_ID))
				.withScope(StringIdentifier.of(SCOPE))
				.build();

		return CartTotalIdentifier.builder()
				.withCart(cartIdentifier)
				.build();
	}
}