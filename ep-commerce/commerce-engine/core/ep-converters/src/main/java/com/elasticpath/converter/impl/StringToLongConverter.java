/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;

/**
 * Converts a string to a long.
 */
public class StringToLongConverter implements StringToTypeConverter<Long> {

	@Override
	public Long convert(final String stringValue) {
		try {
			return Long.valueOf(stringValue);
		} catch (final NumberFormatException e) {
			throw new ConversionMalformedValueException("Cannot convert [" + stringValue + "] to Long", e);
		}
	}

}
