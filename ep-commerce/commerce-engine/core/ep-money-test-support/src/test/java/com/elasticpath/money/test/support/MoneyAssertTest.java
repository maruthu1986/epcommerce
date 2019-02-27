/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.money.test.support;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.elasticpath.money.test.support.MoneyAssert.assertThat;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.ComparisonFailure;
import org.junit.Test;

import com.elasticpath.money.Money;

/**
 * Test class for {@link MoneyAssert}.
 */
public class MoneyAssertTest {

	private static final Currency CAD = Currency.getInstance(Locale.CANADA);
	private static final Currency USD = Currency.getInstance(Locale.US);

	private static final Money TEN_DOLLARS_CANADIAN = Money.valueOf(BigDecimal.TEN, CAD);

	@Test
	public void verifyAssertCurrencyPassesForSameCurrency() {
		assertThat(TEN_DOLLARS_CANADIAN)
				.hasCurrency(CAD);
	}

	@Test
	public void verifyAssertCurrencyFailsForDifferentCurrency() {
		assertThatThrownBy(() ->
				assertThat(TEN_DOLLARS_CANADIAN)
						.hasCurrency(USD))
				.isInstanceOf(ComparisonFailure.class);
	}

	@Test
	public void verifyAssertAmountPassesForSameAmount() {
		assertThat(TEN_DOLLARS_CANADIAN)
				.hasAmount(new BigDecimal("10.00"));
	}

	@Test
	public void verifyAssertAmountFailsForDifferentAmount() {
		assertThatThrownBy(() ->
				assertThat(TEN_DOLLARS_CANADIAN)
						.hasAmount(new BigDecimal("9.00")))
				.isInstanceOf(ComparisonFailure.class);
	}

	@Test
	public void verifyAssertAmountFailsForSameAmountButDifferentPrecision() {
		assertThatThrownBy(() ->
				assertThat(TEN_DOLLARS_CANADIAN)
						.hasAmount(BigDecimal.TEN))
				.isInstanceOf(ComparisonFailure.class);
	}

	@Test
	public void verifyAssertRawAmountPassesForSameUnRoundedAmount() {
		assertThat(TEN_DOLLARS_CANADIAN)
				.hasRawAmount(BigDecimal.TEN);
	}

	@Test
	public void verifyAssertRawAmountFailsForSameAmountWithDifferentPrecision() {
		assertThatThrownBy(() ->
				assertThat(TEN_DOLLARS_CANADIAN)
						.hasRawAmount(new BigDecimal("10.00")))
				.isInstanceOf(ComparisonFailure.class);
	}

	@Test
	public void verifyAssertRawAmountFailsForDifferentAmount() {
		assertThatThrownBy(() ->
				assertThat(TEN_DOLLARS_CANADIAN)
						.hasRawAmount(new BigDecimal("9.00")))
				.isInstanceOf(ComparisonFailure.class);
	}

}