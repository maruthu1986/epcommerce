/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.conversion;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;

/**
 * Databinding converter to convert String to Number.
 */
public class EpStringToNumberConverter extends Converter {

	/**
	 * Constructor.
	 */
	public EpStringToNumberConverter() {
		super(String.class, Number.class);
	}

	/**
	 * Convert from given object to Number.
	 * @param fromObject the string to convert
	 * @return a number
	 */
	public Object convert(final Object fromObject) {
		if (!(fromObject instanceof String)) {
			throw new IllegalArgumentException("The argument to convert is not String"); //$NON-NLS-1$
		}
		String numberString = (String) fromObject;
		//If the field is not required then validation will pass and the conversion method will be called, but there may be nothing in the field.
		if (StringUtils.isBlank(numberString)) {
			return null;
		}
		
		return new BigDecimal(numberString);
	}

}
