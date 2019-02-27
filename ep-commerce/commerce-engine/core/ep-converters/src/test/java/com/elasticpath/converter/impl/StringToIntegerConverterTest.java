/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import static java.lang.Integer.valueOf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.elasticpath.converter.ConversionMalformedValueException;

/**
 * Test class for {@link StringToIntegerConverter}.
 */
public class StringToIntegerConverterTest {

	private final StringToIntegerConverter converter = new StringToIntegerConverter();

	@Test
	public void testConvert() {
		final String value = "12";

		assertThat(converter.convert(value))
				.as("Should convert to integer with expected value.")
				.isEqualTo(valueOf(value));
	}

	@Test
	public void testConvertThrowsExceptionForAlphabeticStrings() {
		assertThatThrownBy(() -> converter.convert("abc"))
				.isInstanceOf(ConversionMalformedValueException.class);
	}

	@Test
	public void testConvertThrowsExceptionForNonIntegerNumericStrings() {
		assertThatThrownBy(() -> converter.convert("12.1"))
				.isInstanceOf(ConversionMalformedValueException.class);
	}

}
