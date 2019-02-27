/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.definition.taxes.OrderTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TaxesCalculator;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Test for {@link OrderTaxEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderTaxEntityRepositoryTest {

	@Mock private ConversionService conversionService;
	@Mock private TaxesCalculator taxesCalculator;

	@InjectMocks
	private OrderTaxEntityRepositoryImpl<TaxesEntity, OrderTaxIdentifier> repository;

	@Test
	public void findElementInOrderTaxRepository() {

		TaxCalculationResult taxCalculationResult = mock(TaxCalculationResult.class);
		TaxesEntity result = mock(TaxesEntity.class);

		when(taxesCalculator.calculateTax(ResourceTestConstants.SCOPE, ResourceTestConstants.ORDER_ID))
				.thenReturn(Single.just(taxCalculationResult));
		when(conversionService.convert(taxCalculationResult, TaxesEntity.class)).thenReturn(result);

		repository.findOne(getOrderTaxIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(result);
	}

	private OrderTaxIdentifier getOrderTaxIdentifier() {
		OrderIdentifier order = OrderIdentifier.builder()
				.withScope(StringIdentifier.of(ResourceTestConstants.SCOPE))
				.withOrderId(StringIdentifier.of(ResourceTestConstants.ORDER_ID))
				.build();

		return OrderTaxIdentifier.builder()
				.withOrder(order)
				.build();
	}
}
