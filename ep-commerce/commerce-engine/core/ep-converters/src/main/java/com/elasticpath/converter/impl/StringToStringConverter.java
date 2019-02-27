/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import com.elasticpath.converter.StringToTypeConverter;

/**
 * Converts to a string value.
 */
public class StringToStringConverter implements StringToTypeConverter<String> {

	@Override
	public String convert(final String stringValue) {
		return stringValue;
	}

}