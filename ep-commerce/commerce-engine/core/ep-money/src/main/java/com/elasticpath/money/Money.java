/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Represents an amount of money in a particular currency.
 */
public final class Money implements Serializable, Comparable<Money> {

	private static final long serialVersionUID = 0;

	/**
	 * Scale to be used by {@link Money#divide(int)} and {@link Money#divide(BigDecimal)}.
	 *
	 * It's a larger number than any {@link Currency#getDefaultFractionDigits()} to prevent rounding in those digits.
	 * {@link #getAmount()} will scale the resultant value to the Currency's scale. */
	private static final int DIVISION_SCALE = 10;

	private final BigDecimal amount;
	private final Currency currency;

	/**
	 * Constructor.  This class should not be instantiated directly from service and domain classes. Instead use
	 * {@link Money#valueOf(java.math.BigDecimal, java.util.Currency)}.
	 *
	 * @param amount the amount of money, must not be null
	 * @param currency the currency, must not be null
	 * @throws IllegalArgumentException if amount or currency is null
	 */
	private Money(final BigDecimal amount, final Currency currency) throws IllegalArgumentException {
		if (null == amount) {
			throw new IllegalArgumentException("Amount must not be null");
		}

		if (null == currency) {
			throw new IllegalArgumentException("Currency must not be null");
		}

		this.amount = amount;
		this.currency = currency;
	}

	/**
	 * Create a money object.
	 *
	 * @param amount the amount of money to create
	 * @param currency the currency
	 *
	 * @return the money.
	 */
	public static Money valueOf(final BigDecimal amount, final Currency currency) {
		return new Money(amount, currency);
	}

	/**
	 * Create a money object.
	 *
	 * @param amount the amount of money to create
	 * @param currency the currency
	 *
	 * @return the money.
	 */
	public static Money valueOf(final String amount, final Currency currency) {
		return new Money(new BigDecimal(amount), currency);
	}

	/**
	 * Create a money object.
	 *
	 * @param amount the amount of money to create
	 * @param currency the currency
	 *
	 * @return the money.
	 */
	public static Money valueOf(final int amount, final Currency currency) {
		return new Money(new BigDecimal(amount), currency);
	}

	/**
	 * Create a money object in the currency given with a zero value.
	 *
	 * @param currency the currency
	 *
	 * @return the money.
	 */
	public static Money zero(final Currency currency) {
		return valueOf(BigDecimal.ZERO, currency);
	}

	/**
	 * Get the amount of money as a <code>BigDecimal</code>, in the scale
	 * dictated by this object's Currency (if one has been set).
	 *
	 * @return the <code>BigDecimal</code> amount, or null if it has not been set.
	 */
	public BigDecimal getAmount() {
		return amount.setScale(getCurrency().getDefaultFractionDigits(), RoundingMode.HALF_UP);
	}

	/**
	 * Returns the amount of money as a <code>BigDecimal</code>, without applying rounding. The value returned will be the same value used
	 * originally to construct this Money instance.
	 *
	 * @return the amount of money as a <code>BigDecimal</code> without applying rounding.
	 */
	public BigDecimal getRawAmount() {
		return amount;
	}

	/**
	 * Get the <code>Currency</code> of the money.
	 *
	 * @return the <code>Currency</code>
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Returns a new <code>Money</code> object representing the the amount of this object less the amount of the
	 * <code>otherMoney</code> object.
	 *
	 * @param otherMoney the <code>Money</code> object whose value is to be subtracted from this object
	 * @return the new <code>Money</code> object representing this value less the other object's value.
	 */
	public Money subtract(final Money otherMoney) {
		return add(otherMoney.negate());
	}

	/**
	 * Returns a new <code>Money</code> object representing the sum of this object and otherMoney's values.
	 *
	 * @param otherMoney the <code>Money</code> object whose value is to be added to this object
	 * @return the new <code>Money</code> object representing the sum.
	 */
	public Money add(final Money otherMoney) {
		checkCurrencyMatch(otherMoney);

		return valueOf(amount.add(otherMoney.getAmount()), currency);
	}

	/**
	 * Returns a new <code>Money</code> object with the negated value of this object.
	 *
	 * @return the negated <code>Money</code> object
	 */
	Money negate() {
		return valueOf(amount.negate(), currency);
	}

	/**
	 * Returns the amount that would be saved by buying at the sale price instead of the (this) price.  If the sale price is greater than
	 * the current price returns 0.
	 *
	 * @param salePrice the sale price
	 * @return the amount saved by buying at the sale price, or zero if the sale is not so good
	 */
	public Money getSaleSavings(final Money salePrice) {
		if (salePrice.lessThan(this)) {
			return subtract(salePrice);
		}

		return valueOf(BigDecimal.ZERO, getCurrency());
	}

	/**
	 * Returns the percentage amount that would be saved by buying at the sale price instead of the (this) price, rounded to the nearest
	 * whole percentage (e.g. 50.5% would return 0.50).  If the sale price is greater than the current price, then this method returns 0.
	 *
	 * @param salePrice the sale price
	 * @return the percentage saved by buying at the sale price, or zero if the sale is not so good
	 */
	public double getSalePercentage(final Money salePrice) {
		final double oneHundredPercent = 100.0;

		if (salePrice.lessThan(this)) {
			double percentageSaving = 1.0 - salePrice.getAmount().doubleValue() / getAmount().doubleValue();
			return Math.round(percentageSaving * oneHundredPercent) / oneHundredPercent;
		}

		return 0;
	}

	/**
	 * Returns true if this money object is greater than the specified other money object.
	 *
	 * @param otherMoney the other money object
	 * @return true if this object is greater
	 */
	public boolean greaterThan(final Money otherMoney) {
		return compareTo(otherMoney) > 0;
	}

	/**
	 * Returns true if this money object is less than the specified other money object.
	 *
	 * @param otherMoney the other money object
	 * @return true if this object is less
	 */
	public boolean lessThan(final Money otherMoney) {
		return compareTo(otherMoney) < 0;
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Money)) {
			return false;
		}

		final Money other = (Money) obj;
		return Objects.equals(getAmount(), other.getAmount())
			&& Objects.equals(getCurrency(), other.getCurrency());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAmount(), getCurrency());
	}

	/**
	 * Checks the currency of the parameter <code>Money</code> object matches this <code>Money</code> object.
	 *
	 * @param otherMoney the other Money object to be checked
	 * @throws IllegalArgumentException if the currencies don't match
	 */
	private void checkCurrencyMatch(final Money otherMoney) {
		if (currency.getCurrencyCode().equalsIgnoreCase(otherMoney.getCurrency().getCurrencyCode())) {
			return;
		}

		throw new IllegalArgumentException("Money currency mismatch: " + currency.getCurrencyCode()
				+ " != " + otherMoney.getCurrency().getCurrencyCode());
	}

	/**
	 * Return a new Money object whose value is this money object's value times the specified multiplier.
	 *
	 * @param multiplier the amount to multiply by
	 * @return a Money object representing the result
	 */
	public Money multiply(final BigDecimal multiplier) {
		return valueOf(amount.multiply(multiplier), currency);
	}

	/**
	 * Return a new Money object whose value is this money object's value times the specified multiplier.
	 *
	 * @param multiplier the amount to multiply by
	 * @return a Money object representing the result
	 */
	public Money multiply(final int multiplier) {
		return multiply(BigDecimal.valueOf(multiplier));
	}

	/**
	 * Return a new Money object whose value is this money object's value divided by the specified divisor.
	 * When rounding after dividing is required the number is rounded up if >= 0.5, otherwise it's rounded down.
	 *
	 * @param divisor the amount to divide by
	 * @return a Money object representing the result
	 */
	public Money divide(final int divisor) {
		return divide(BigDecimal.valueOf(divisor));
	}

	/**
	 * Return a new Money object whose value is this money object's value divided by the specified divisor.
	 * When rounding after dividing is required the number is rounded up if >= 0.5, otherwise it's rounded down.
	 *
	 * @param divisor the amount to divide by
	 * @return a Money object representing the result
	 */
	public Money divide(final BigDecimal divisor) {
		return valueOf(amount.divide(divisor, DIVISION_SCALE, BigDecimal.ROUND_HALF_UP), currency);
	}

	/**
	 * Compares this money with the specified object for order.
	 *
	 * @param money the given object
	 * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the
	 *         specified object.
	 * @throws IllegalArgumentException if the currencies do not match
	 */
	@Override
	public int compareTo(final Money money) throws IllegalArgumentException {
		checkCurrencyMatch(money);

		// Just Compare amount
		return getAmount().compareTo(money.getAmount());
	}

	/**
	 * Returns string representation of Money objects to facilitate debugging.
	 *
	 * @return a string representation of the Money object
	 */
	@Override
	public String toString() {
		StringBuilder moneyString = new StringBuilder();
		moneyString.append(getAmount());
		moneyString.append(' ').append(getCurrency());
		return moneyString.toString();
	}
}
