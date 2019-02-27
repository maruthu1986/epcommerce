/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter;

import java.util.function.Function;

/**
 * Converts a string to a java type.
 *
 * @param <T> the type the string will be converted to.
 */
public interface StringToTypeConverter<T> extends Function<String, T> {

	/**
	 * Converts the given string to a java type.
	 *
	 * @param stringValue the string to convert
	 * @return the converted type
	 */
	T convert(String stringValue);

	@Override
	default T apply(String stringValue) {
		return convert(stringValue);
	}

}
