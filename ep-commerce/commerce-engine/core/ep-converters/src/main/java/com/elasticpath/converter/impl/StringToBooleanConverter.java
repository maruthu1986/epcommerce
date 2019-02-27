/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import com.elasticpath.converter.StringToTypeConverter;

/**
 * Converts a string to a boolean.
 */
public class StringToBooleanConverter implements StringToTypeConverter<Boolean> {

	@Override
	public Boolean convert(final String stringValue) {
		return Boolean.valueOf(stringValue);
	}

}
