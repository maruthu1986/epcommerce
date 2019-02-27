/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.conversion;

import java.util.Currency;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;

import com.elasticpath.money.Money;

/**
 * Converter for converting a String into Money.
 */
public class EpStringToMoneyConverter extends Converter {

	private final Currency currency;

	/**
	 *
	 * @param currency order currency
	 */
	public EpStringToMoneyConverter(final Currency currency) {
		super(String.class, Money.class);
		this.currency = currency;
	}

	
	/**
	 * Converts from String to BigDecimal.
	 * 
	 * @param fromObject object to convert
	 * @return resulting object
	 */
	@Override
	public Object convert(final Object fromObject) {
		if (!(fromObject instanceof String)) {
			throw new IllegalArgumentException("The argument to convert is not String"); //$NON-NLS-1$
		}
		String moneyString = (String) fromObject;
		//If the field is not required then validation will pass and the conversion method will be called, but there may be nothing in the field.
		if (StringUtils.isBlank(moneyString)) {
			return null;
		}
		
		return Money.valueOf(moneyString, currency);
	}

}
