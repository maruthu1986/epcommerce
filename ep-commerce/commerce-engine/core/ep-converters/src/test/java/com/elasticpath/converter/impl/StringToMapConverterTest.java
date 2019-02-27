/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Maps;
import org.junit.Test;

import com.elasticpath.converter.ConversionMalformedValueException;

/**
 * Test class for {@link StringToMapConverter}.
 */
public class StringToMapConverterTest {

	@Test
	public void verifyEmptyStringYieldsEmptyMap() throws Exception {
		assertThat(new StringToMapConverter().convert(""))
				.isEmpty();
	}

	@Test
	public void verifyConvertCreatesMapInstanceWithMultipleValues() {
		final String input = "key1=val1,key2=val2,key3=val3";

		assertThat(new StringToMapConverter().convert(input))
				.containsExactly(
						Maps.immutableEntry("key1", "val1"),
						Maps.immutableEntry("key2", "val2"),
						Maps.immutableEntry("key3", "val3")
				);
	}

	@Test
	public void verifyConvertCreatesMapInstanceWithNullValuesForSettingValueStringWithNoPairSeparators() {
		final String input = "key1=,key2=,key3=";

		assertThat(new StringToMapConverter().convert(input))
				.containsExactly(
						Maps.immutableEntry("key1", null),
						Maps.immutableEntry("key2", null),
						Maps.immutableEntry("key3", null)
				);
	}

	@Test
	public void verifyConvertCreatesMapInstanceWithSinglePair() {
		final String input = "key=val"; // no comma separators

		assertThat(new StringToMapConverter().convert(input))
				.containsExactly(Maps.immutableEntry("key", "val"));
	}

	@Test
	public void verifyConvertThrowsExceptionForSettingValueStringWithNoPairSeparators() {
		assertThatThrownBy(() -> new StringToMapConverter().convert("a,b,c"))
				.isInstanceOf(ConversionMalformedValueException.class);
	}

	@Test
	public void verifyConvertThrowsExceptionWhenSettingValueStringContainsDuplicateKeys() {
		assertThatThrownBy(() -> new StringToMapConverter().convert("key=val1,key=val2"))
				.isInstanceOf(ConversionMalformedValueException.class);
	}

}