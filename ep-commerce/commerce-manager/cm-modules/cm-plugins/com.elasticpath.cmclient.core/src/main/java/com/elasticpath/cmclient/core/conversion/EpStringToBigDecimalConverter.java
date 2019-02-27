/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.conversion;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;

/**
 * Converter for converting a String into BigDecimal.
 */
public class EpStringToBigDecimalConverter extends Converter {

	/**
	 *
	 * @param fromType
	 * @param toType
	 */
	public EpStringToBigDecimalConverter() {
		super(String.class, BigDecimal.class);
	}
	
	
	/**
	 * Converts from String to BigDecimal.
	 * 
	 * @param fromObject object to convert
	 * @return resulting object
	 */
	public Object convert(final Object fromObject) {
		if (!(fromObject instanceof String)) {
			throw new IllegalArgumentException("The argument to convert is not String"); //$NON-NLS-1$
		}
		String bigDecimalString = (String) fromObject;
		//If the field is not required then validation will pass and the conversion method will be called, but there may be nothing in the field.
		if (StringUtils.isBlank(bigDecimalString)) {
			return null;
		}
		
		return new BigDecimal(bigDecimalString);
	}

}
