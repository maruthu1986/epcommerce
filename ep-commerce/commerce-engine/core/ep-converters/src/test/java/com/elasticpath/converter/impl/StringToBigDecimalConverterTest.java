/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.Test;

import com.elasticpath.converter.ConversionMalformedValueException;

/**
 * Test class for {@link StringToBigDecimalConverter}.
 */
public class StringToBigDecimalConverterTest {

	private final StringToBigDecimalConverter converter = new StringToBigDecimalConverter();

	@Test
	public void testConvertSmallNumber() {
		final String value = "12.3";
		assertThat(converter.convert(value))
				.as("Should convert to big decimal with expected value.")
				.isEqualTo(new BigDecimal(value));
	}

	@Test
	public void testConvertReallyBigNumber() {
		final String value = "9" + Double.MAX_VALUE;

		assertThat(converter.convert(value))
				.as("Should convert to big decimal with expected value.")
				.isEqualTo(new BigDecimal(value));
	}

	@Test
	public void testConvertThrowsExceptionOnMalformedValue() {
		assertThatThrownBy(() -> converter.convert("abc"))
				.isInstanceOf(ConversionMalformedValueException.class);
	}

}
