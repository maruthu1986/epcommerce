/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.conversion;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * Converter for changing a integer to a string, without any commas. 
 */
public class EpIntToStringConverter extends Converter {
	
	/**
	 * Constructor. 
	 */
	public EpIntToStringConverter() {
		super(Integer.class, String.class);
	}
	
	/**
	 * Convert to string.
	 * @param input integer
	 * @return string representation of int
	 */
	public Object convert(final Object input) {
		if (!(input instanceof Integer)) {
			throw new IllegalArgumentException("The argument to convert is not Integer"); //$NON-NLS-1$
		}
		return Integer.toString((Integer) input);
	}
}