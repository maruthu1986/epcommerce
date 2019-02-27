/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.elasticpath.converter.ConversionMalformedValueException;

/**
 * Test class for {@link StringToLongConverter}.
 */
public class StringToLongConverterTest {

	private final StringToLongConverter converter = new StringToLongConverter();

	@Test
	public void testConvert() {
		final String value = "12";

		assertThat(converter.convert(value))
				.as("Should convert to long with expected value.")
				.isEqualTo(Long.valueOf(value));
	}

	@Test
	public void testConvertThrowsExceptionOnMalformedValue() {
		assertThatThrownBy(() -> converter.convert("abc"))
				.isInstanceOf(ConversionMalformedValueException.class);
	}

}
