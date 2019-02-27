/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.converters;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Locale.CANADA;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.attribute.LocaleSubjectAttribute;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.identity.type.ImmutableSubject;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test for {@link ShipmentToTaxEntityConverter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentToTaxEntityConverterTest {

	private static final Currency CURRENCY = Currency.getInstance(CANADA);
	private static final BigDecimal AMOUNT_1 = BigDecimal.valueOf(10);
	private static final BigDecimal AMOUNT_2 = BigDecimal.valueOf(23);
	private static final BigDecimal AMOUNT = AMOUNT_1.add(AMOUNT_2);
	private static final String DISPLAY_NAME_1 = "display name 1";
	private static final String DISPLAY_NAME_2 = "display name 2";
	private static final String CURRENCY_FORMAT_1 = "10 CAD";
	private static final String CURRENCY_FORMAT_2 = "23 CAD";

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private MoneyTransformer moneyTransformer;
	@Mock
	private MoneyFormatter moneyFormatter;
	@InjectMocks
	private ShipmentToTaxEntityConverter converter;

	@Before
	public void setUp() {
		setUpLocale();
	}

	@Test
	public void test() {
		PhysicalOrderShipment physicalOrderShipment = mock(PhysicalOrderShipment.class);
		Order order = mock(Order.class);
		when(physicalOrderShipment.getOrder()).thenReturn(order);
		when(order.getCurrency()).thenReturn(CURRENCY);

		Money taxMoney = Money.valueOf(AMOUNT, CURRENCY);
		when(physicalOrderShipment.getTotalTaxMoney()).thenReturn(taxMoney);

		CostEntity total = mock(CostEntity.class);
		when(moneyTransformer.transformToEntity(taxMoney, CANADA)).thenReturn(total);

		OrderTaxValue taxValue1 = mock(OrderTaxValue.class);
		OrderTaxValue taxValue2 = mock(OrderTaxValue.class);
		Set<OrderTaxValue> shipmentTaxes = ImmutableSet.of(taxValue1, taxValue2);
		setUpCategoryDisplayNameWithTaxValue(taxValue1, DISPLAY_NAME_1, AMOUNT_1);
		setUpCategoryDisplayNameWithTaxValue(taxValue2, DISPLAY_NAME_2, AMOUNT_2);
		when(physicalOrderShipment.getShipmentTaxes()).thenReturn(shipmentTaxes);

		when(moneyFormatter.formatCurrency(CURRENCY, AMOUNT_1, CANADA)).thenReturn(CURRENCY_FORMAT_1);
		when(moneyFormatter.formatCurrency(CURRENCY, AMOUNT_2, CANADA)).thenReturn(CURRENCY_FORMAT_2);

		ImmutableList<NamedCostEntity> cost = ImmutableList.of(
				NamedCostEntity.builder()
						.withAmount(AMOUNT_1)
						.withCurrency(CURRENCY.getCurrencyCode())
						.withDisplay(CURRENCY_FORMAT_1)
						.withTitle(DISPLAY_NAME_1)
						.build(),
				NamedCostEntity.builder()
						.withAmount(AMOUNT_2)
						.withCurrency(CURRENCY.getCurrencyCode())
						.withDisplay(CURRENCY_FORMAT_2)
						.withTitle(DISPLAY_NAME_2)
						.build()
		);

		TaxesEntity expectedEntity = TaxesEntity.builder()
				.withTotal(total)
				.withCost(cost)
				.build();

		assertEquals(converter.convert(physicalOrderShipment), expectedEntity);
	}

	private void setUpCategoryDisplayNameWithTaxValue(final OrderTaxValue taxValue1, final String displayName1, final BigDecimal amount) {
		when(taxValue1.getTaxValue()).thenReturn(amount);
		when(taxValue1.getTaxCategoryDisplayName()).thenReturn(displayName1);
	}

	private void setUpLocale() {
		SubjectAttribute attribute = new LocaleSubjectAttribute(CANADA.toLanguageTag(), CANADA);
		Subject subject = new ImmutableSubject(emptyList(), singleton(attribute));
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

}
