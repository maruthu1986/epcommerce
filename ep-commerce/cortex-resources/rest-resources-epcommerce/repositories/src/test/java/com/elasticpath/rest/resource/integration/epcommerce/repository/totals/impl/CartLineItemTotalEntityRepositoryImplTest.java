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
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.totals.OrderTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;

/**
 * Test for {@link CartLineItemTotalEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartLineItemTotalEntityRepositoryImplTest {

	private static final String SCOPE = "scope";
	private static final String ORDER_ID = "order_id";
	private static final Money TEN_CAD = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));

	@Mock
	private TotalsCalculator totalsCalculator;

	@Mock
	private ConversionService conversionService;

	@InjectMocks
	private OrderTotalEntityRepositoryImpl<TotalEntity, OrderTotalIdentifier> orderTotalEntityRepository;

	@Test
	public void shouldGetTotal() {
		TotalEntity totalEntity = createTotalEntity();
		when(totalsCalculator.calculateTotalForCartOrder(SCOPE, ORDER_ID)).thenReturn(Single.just(TEN_CAD));
		when(conversionService.convert(TEN_CAD, TotalEntity.class)).thenReturn(totalEntity);

		orderTotalEntityRepository.findOne(createOrderTotalIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(totalEntity);
	}

	@Test
	public void shouldNotGetTotal() {
		when(totalsCalculator.calculateTotalForCartOrder(SCOPE, ORDER_ID))
				.thenReturn(Single.error(ResourceOperationFailure.badRequestBody()));
		orderTotalEntityRepository.findOne(createOrderTotalIdentifier())
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


	private OrderTotalIdentifier createOrderTotalIdentifier() {
		final OrderIdentifier orderIdentifier = OrderIdentifier.builder()
				.withOrderId(StringIdentifier.of(ORDER_ID))
				.withScope(StringIdentifier.of(SCOPE))
				.build();

		return OrderTotalIdentifier.builder()
				.withOrder(orderIdentifier)
				.build();
	}

}