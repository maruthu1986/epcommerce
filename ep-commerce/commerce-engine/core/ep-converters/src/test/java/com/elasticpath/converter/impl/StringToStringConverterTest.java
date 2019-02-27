/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Test class for {@link StringToStringConverter}.
 */
public class StringToStringConverterTest {

	private final StringToStringConverter converter = new StringToStringConverter();

	@Test
	public void testConvert() {
		final String value = "123";

		assertThat(converter.convert(value))
				.as("Should convert to string with expected value.")
				.isEqualTo(value);
	}

}
