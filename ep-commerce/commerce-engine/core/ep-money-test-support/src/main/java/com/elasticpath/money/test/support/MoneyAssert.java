/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.money.test.support;

import java.math.BigDecimal;
import java.util.Currency;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.internal.Objects;

import com.elasticpath.money.Money;

/**
 * <p>Assertion methods for {@code Money} instances.</p>
 * <p>To create a new instance of this class, invoke <code>{@link #assertThat(Money)}</code>.</p>
 */
public final class MoneyAssert extends AbstractAssert<MoneyAssert, Money> {

	/**
	 * Private constructor.
	 *
	 * @param actual the Money instance upon which assertions will be made
	 * @see #assertThat(Money)
	 */
	private MoneyAssert(final Money actual) {
		super(actual, MoneyAssert.class);
	}

	/**
	 * Creates a new instance of {@link MoneyAssert}.
	 *
	 * @param actual the Money instance upon which assertions will be made
	 * @return the created MoneyAssert instance
	 */
	public static MoneyAssert assertThat(final Money actual) {
		return new MoneyAssert(actual);
	}

	/**
	 * Verifies that the actual instance's Currency matches the given parameter.
	 *
	 * @param currency the currency to compare with the actual Money's currency
	 * @return this assertion object
	 */
	public MoneyAssert hasCurrency(final Currency currency) {
		Objects.instance().assertEqual(info, actual.getCurrency(), currency);

		return this;
	}

	/**
	 * Verifies that the actual instance's amount matches the given parameter.
	 *
	 * @param amount the amount to compare with the actual Money's amount
	 * @return this assertion object
	 */
	public MoneyAssert hasAmount(final BigDecimal amount) {
		Objects.instance().assertEqual(info, actual.getAmount(), amount);

		return this;
	}

	/**
	 * Verifies that the actual instance's amount matches the given parameter, ignoring precision.
	 *
	 * @param amount the amount to compare with the actual Money's amount
	 * @return this assertion object
	 */
	public MoneyAssert hasRawAmount(final BigDecimal amount) {
		Objects.instance().assertEqual(info, actual.getRawAmount(), amount);

		return this;
	}

}
