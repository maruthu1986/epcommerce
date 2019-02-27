/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.conversion;

import org.eclipse.core.databinding.conversion.Converter;

import com.elasticpath.money.Money;

/**
 * Converter for converting a Money into String.
 */
public class EpMoneyToStringConverter extends Converter {

	/**
	 * Creates a new converter from BigDecimal to String.
	 */
	public EpMoneyToStringConverter() {
		super(Money.class, String.class);
	}
	
	/**
	 * Converts from BigDecimal to String.
	 * 
	 * @param fromObject object to convert
	 * @return resulting object
	 */
	public Object convert(final Object fromObject) {
		if (!(fromObject instanceof Money)) {
			throw new IllegalArgumentException("The argument to convert is not Money"); //$NON-NLS-1$
		}
		
		return ((Money) fromObject).getAmount().toPlainString();
	}

}
