/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.conversion;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * Converter for converting a BigDecimal into String.
 */
public class EpBigDecimalToStringConverter extends Converter {

	/**
	 * Creates a new converter from BigDecimal to String.
	 */
	public EpBigDecimalToStringConverter() {
		super(BigDecimal.class, String.class);
	}
	
	
	/**
	 * Converts from BigDecimal to String.
	 * 
	 * @param fromObject object to convert
	 * @return resulting object
	 */
	public Object convert(final Object fromObject) {
		if (!(fromObject instanceof BigDecimal)) {
			throw new IllegalArgumentException("The argument to convert is not BigDecimal"); //$NON-NLS-1$
		}
		
		return ((BigDecimal) fromObject).toPlainString();
	}

}
