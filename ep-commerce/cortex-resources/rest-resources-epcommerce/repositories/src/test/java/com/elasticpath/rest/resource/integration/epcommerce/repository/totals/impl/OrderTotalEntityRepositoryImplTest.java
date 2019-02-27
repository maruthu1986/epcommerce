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
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.totals.CartLineItemTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;

/**
 * Test for {@link OrderTotalEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderTotalEntityRepositoryImplTest {

	private static final String SCOPE = "scope";
	private static final String CART_ID = "cart_id";
	private static final String LINE_ITEM_1_ID = "line_item_1_id";
	private static final Money TEN_CAD = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));

	@Mock
	private TotalsCalculator totalsCalculator;

	@Mock
	private ConversionService conversionService;

	@InjectMocks
	private CartLineItemTotalEntityRepositoryImpl<TotalEntity, CartLineItemTotalIdentifier> cartLineItemTotalEntityRepository;

	@Test
	public void shouldGetTotal() {
		TotalEntity totalEntity = createTotalEntity();
		when(totalsCalculator.calculateTotalForLineItem(SCOPE, CART_ID, LINE_ITEM_1_ID)).thenReturn(Single.just(TEN_CAD));
		when(conversionService.convert(TEN_CAD, TotalEntity.class)).thenReturn(totalEntity);

		cartLineItemTotalEntityRepository.findOne(createCartLineItemTotalIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(totalEntity);
	}

	@Test
	public void shouldNotGetTotal() {
		when(totalsCalculator.calculateTotalForLineItem(SCOPE, CART_ID, LINE_ITEM_1_ID))
				.thenReturn(Single.error(ResourceOperationFailure.badRequestBody()));
		cartLineItemTotalEntityRepository.findOne(createCartLineItemTotalIdentifier())
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


	private CartLineItemTotalIdentifier createCartLineItemTotalIdentifier() {
		final CartIdentifier cartIdentifier = CartIdentifier.builder()
				.withCartId(StringIdentifier.of(CART_ID))
				.withScope(StringIdentifier.of(SCOPE))
				.build();

		final LineItemsIdentifier lineItemsIdentifier = LineItemsIdentifier.builder()
				.withCart(cartIdentifier)
				.build();


		final LineItemIdentifier lineItemIdentifier = LineItemIdentifier.builder()
				.withLineItemId(StringIdentifier.of(LINE_ITEM_1_ID))
				.withLineItems(lineItemsIdentifier)
				.build();

		return CartLineItemTotalIdentifier.builder()
				.withLineItem(lineItemIdentifier)
				.build();
	}
}