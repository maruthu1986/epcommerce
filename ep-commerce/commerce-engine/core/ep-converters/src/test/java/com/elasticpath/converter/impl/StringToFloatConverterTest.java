/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.elasticpath.converter.ConversionMalformedValueException;

/**
 * Test class for {@link StringToFloatConverter}.
 */
public class StringToFloatConverterTest {

	private final StringToFloatConverter converter = new StringToFloatConverter();

	@Test
	public void testConvert() {
		final String value = "12.0";

		assertThat(converter.convert(value))
				.as("Should convert to float with expected value.")
				.isEqualTo(Float.valueOf(value));
	}

	@Test
	public void testConvertThrowsExceptionOnMalformedValue() {
		assertThatThrownBy(() -> converter.convert("abc"))
				.isInstanceOf(ConversionMalformedValueException.class);
	}

}
