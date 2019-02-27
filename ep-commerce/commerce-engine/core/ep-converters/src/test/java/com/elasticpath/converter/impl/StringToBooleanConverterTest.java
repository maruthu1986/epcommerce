/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.converter.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Test class for {@link StringToBooleanConverter}.
 */
public class StringToBooleanConverterTest {

	private final StringToBooleanConverter stringToBooleanConverter = new StringToBooleanConverter();

	@Test
	public void verifyConvertHandlesLowerCaseTrue() {
		assertThat(stringToBooleanConverter.convert("true"))
				.isTrue();
	}

	@Test
	public void verifyConvertHandlesUpperCaseTrue() {
		assertThat(stringToBooleanConverter.convert("TRUE"))
				.isTrue();
	}

	@Test
	public void verifyConvertHandlesSentenceCaseTrue() {
		assertThat(stringToBooleanConverter.convert("True"))
				.isTrue();
	}

	@Test
	public void verifyConvertHandlesLowerCaseFalse() {
		assertThat(stringToBooleanConverter.convert("false"))
				.isFalse();
	}

	@Test
	public void verifyConvertHandlesUpperCaseFalse() {
		assertThat(stringToBooleanConverter.convert("FALSE"))
				.isFalse();
	}

	@Test
	public void verifyConvertHandlesSentenceCaseFalse() {
		assertThat(stringToBooleanConverter.convert("False"))
				.isFalse();
	}

	@Test
	public void verifyConvertFallsBackToFalseWhenCannotConvertFromSettingValueString() {
		assertThat(stringToBooleanConverter.convert("foo"))
				.isFalse();
	}

}
