/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Test class for {@link StringToCollectionConverter}.
 */
public class StringToCollectionConverterTest {

	@Test
	public void verifyConvertCreatesCollectionWithMultipleValues() {
		final String inputString = "val1,val2,val3";

		assertThat(new StringToCollectionConverter().convert(inputString))
				.containsExactly("val1", "val2", "val3");
	}

	@Test
	public void verifyConvertCreatesCollectionWithSingleValue() {
		final String inputString = "valueDoesNotContainSeparatorCharacter";

		assertThat(new StringToCollectionConverter().convert(inputString))
				.containsExactly(inputString);
	}

	@Test
	public void verifyConvertCreatesEmptyCollectionForSeparatorOnlyString() {
		final String inputString = ",,,,,,,,,";

		assertThat(new StringToCollectionConverter().convert(inputString))
				.isEmpty();
	}

	@Test
	public void verifyConvertCreatesEmptyCollectionForEmptyString() {
		final String inputString = "";

		assertThat(new StringToCollectionConverter().convert(inputString))
				.isEmpty();
	}

}