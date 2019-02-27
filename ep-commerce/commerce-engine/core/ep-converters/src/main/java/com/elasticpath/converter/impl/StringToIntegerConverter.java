/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;

/**
 * Converts a string to an integer.
 */
public class StringToIntegerConverter implements StringToTypeConverter<Integer> {

	@Override
	public Integer convert(final String stringValue) {
		try {
			return Integer.valueOf(stringValue);
		} catch (NumberFormatException e) {
			throw new ConversionMalformedValueException("Cannot convert [" + stringValue + "] to Integer", e);
		}
	}

}
