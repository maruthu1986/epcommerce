/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.converter.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;

/**
 * <p>
 * Converts a String to a {@link java.util.Map} of parameterised type {@code <String, String>}.
 * </p>
 * <p>
 * Expected setting value syntax: {@code a=b,foo=bar,hello=world}.
 * </p>
 */
public class StringToMapConverter implements StringToTypeConverter<Map<String, String>> {

	private static final String PAIRS_SEPARATOR = ",";

	private static final String KEY_VALUE_SEPARATOR = "=";

	@Override
	public Map<String, String> convert(final String input) {
		final Map<String, String> result = new HashMap<>();

		if (StringUtils.isEmpty(input)) {
			return result;
		}

		try {
			final Map<String, String> stringStringMap = Splitter.on(PAIRS_SEPARATOR)
					.omitEmptyStrings()
					.withKeyValueSeparator(KEY_VALUE_SEPARATOR)
					.split(input);

			return Maps.transformValues(stringStringMap, Strings::emptyToNull);
		} catch (final IllegalArgumentException e) {
			throw new ConversionMalformedValueException("Unable to convert input string [" + input + "] to a map", e);
		}
	}

}
