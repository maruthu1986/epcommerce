/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Defines the contracts necessary for formatting money.
 *
 * <code>StandardMoneyFormatter</code> is an out of the box implementation of this interface
 * provided with the code base that uses the java.text api to provide locales specific
 * currency symbol and price formatting.
 *
 * Implementations  have 2 choices to provide desired formatting.
 * 1. Override the format method directly without worrying about providing a <code>java.text.NumberFormat</code> impl.
 * 2. Provide a <code>NumberFormat</code> implementation and override format method as appropriate.
 *
 * @see StandardMoneyFormatter
 */
public interface MoneyFormatter extends Serializable {

	/**
	 * Formats the amount in the given Money object without including the currency symbol.
	 * For instance, will format 5.50 CDN -> 5.50
	 *
	 * @param money the Money object to format
	 * @param locale the Locale
	 *
	 * @return A locale-specific string containing the amount of currency in the money object
	 */
	String formatAmount(Money money, Locale locale);

	/**
	 * Formats the amount + currency symbol in the given Money object.  For instance, will format 5.50 CDN -> $5.50
	 *
	 * @param money the Money object to format
	 * @param locale the Locale
	 *
	 * @return A locale-specific string containing the amount of currency in the money object
	 */
	String formatCurrency(Money money, Locale locale);

	/**
	 * Formats the given currency + amount according to the given locale.  Convenience method equivalent to
	 * format(Money.valueOf(amount, currency), locale).
	 *
	 * @param currency currency.
	 * @param amount amount of money.
	 * @param locale the locale
	 * @return amount string
	 */
	String formatCurrency(Currency currency, BigDecimal amount, Locale locale);

	/**
	 * Returns the symbol associated with the current currency.
	 * @param currency the currency to get the symbol for.
	 * @return the currency symbol
	 */
	String formatCurrencySymbol(Currency currency);

	/**
	 * Formats a percentage amount using the given locale.  Convenience method for NumberFormat.getPercentInstance().format()
	 * @param percentage the percentage amount
	 * @param locale the locale to format with
	 * @return the given percentage, formatted with the given locale
	 */
	String formatPercentage(double percentage, Locale locale);

	/**
	 * Formats the passed in <code>Money<code>.
	 *
	 * @param money money
	 * @param includeCurrencySymbol indicates whether to include the currency symbol.
	 * @param locale the locale to format with
	 * @return formatted money
	 * @deprecated Replaced by formatAmount() or formatCurrency()
	 */
	@Deprecated
	String format(Money money, boolean includeCurrencySymbol, Locale locale);

	/**
	 * Returns the symbol associated with the current currency.
	 * @param currency the currency to get the symbol for.
	 * @return the currency symbol
	 * @deprecated Use formatCurrencySymbol instead
	 */
	@Deprecated
	String getCurrencySymbol(Currency currency);

}
