/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform.impl;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;

/**
 * Test class for {@link MoneyTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MoneyTransformerImplTest {

	private static final double AMOUNT_DOUBLE = 10.0;
	private static final String DISPLAY_VALUE = "$10.0";
	private static final BigDecimal MONEY_AMOUNT = new BigDecimal(AMOUNT_DOUBLE);
	private static final Currency CURRENCY = Currency.getInstance(Locale.CANADA);

	@Mock
	private MoneyFormatter moneyFormatter;
	@Mock
	private ResourceOperationContext roContext;

	@InjectMocks
	private MoneyTransformerImpl transformer;

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Test transform to entity.
	 */
	@Test
	public void testTransformToEntityWithLocale() {
		Money money = Money.valueOf(MONEY_AMOUNT, CURRENCY);

		when(moneyFormatter.formatCurrency(money, Locale.ENGLISH))
			.thenReturn(DISPLAY_VALUE);

		CostEntity costEntity = transformer.transformToEntity(money, Locale.ENGLISH);

		assertEquals("Amount value does not match expected value.", costEntity.getAmount(), money.getAmount());
		assertEquals("Currency does not match expected value.", costEntity.getCurrency(), CURRENCY.getCurrencyCode());
		assertEquals("Display value does not match expected value.", costEntity.getDisplay(), DISPLAY_VALUE);
	}

	@Test
	public void testTransformToEntityWithoutLocale() {
		Money money = Money.valueOf(MONEY_AMOUNT, CURRENCY);
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale("scope", "user", Locale.JAPAN);

		when(roContext.getSubject())
			.thenReturn(subject);
		when(moneyFormatter.formatCurrency(money, Locale.JAPAN))
			.thenReturn(DISPLAY_VALUE);

		CostEntity costEntity = transformer.transformToEntity(money);

		assertEquals("Amount value does not match expected value.", costEntity.getAmount(), money.getAmount());
		assertEquals("Currency does not match expected value.", costEntity.getCurrency(), CURRENCY.getCurrencyCode());
		assertEquals("Display value does not match expected value.", costEntity.getDisplay(), DISPLAY_VALUE);
	}
}
