/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.converter.impl;

import java.math.BigDecimal;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;

/**
 * Converts a String to a BigDecimal.
 */
public class StringToBigDecimalConverter implements StringToTypeConverter<BigDecimal> {

	@Override
	public BigDecimal convert(final String stringValue) {
		try {
			return new BigDecimal(stringValue);
		} catch (final NumberFormatException e) {
			throw new ConversionMalformedValueException("Cannot convert [" + stringValue + "] to BigDecimal", e);
		}
	}

}
