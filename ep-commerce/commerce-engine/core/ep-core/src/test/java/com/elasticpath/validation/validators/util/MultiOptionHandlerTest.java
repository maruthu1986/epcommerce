/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.validation.validators.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for {@link MultiOptionHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MultiOptionHandlerTest {

	private static final String[] MULTI_VALID_FIELD_OPTIONS = {"message", "id", "quantity", "name"};

	/**
	 * Given empty options to validate string, should return true.
	 */
	@Test
	public void shouldBeTrueWithEmptyOptionsToValidate() {
		assertTrue("Expect true given empty options to validate", MultiOptionHandler.isOptionsEmptyBlankOrNull("", MULTI_VALID_FIELD_OPTIONS));
		assertTrue("Expect true given blank options to validate", MultiOptionHandler.isOptionsEmptyBlankOrNull(" ", MULTI_VALID_FIELD_OPTIONS));
	}

	/**
	 * Given null as options to validate, should return true.
	 */
	@Test
	public void shouldBeTrueWithNullOptionsToValidate() {
		assertTrue("Expect true given null options to validate", MultiOptionHandler.isOptionsEmptyBlankOrNull(null, MULTI_VALID_FIELD_OPTIONS));
	}

	/**
	 * Given empty valid field options string, should return true.
	 */
	@Test
	public void shouldBeTrueWithEmptyValidOptions() {
		String[] noValidFieldOptions = {};
		assertTrue("Expect true given empty valid field options", MultiOptionHandler.isOptionsEmptyBlankOrNull("id", noValidFieldOptions));
		assertTrue("Expect true given empty valid field options", MultiOptionHandler.isOptionsEmptyBlankOrNull("", noValidFieldOptions));
		assertTrue("Expect true given empty valid field options", MultiOptionHandler.isOptionsEmptyBlankOrNull(null, noValidFieldOptions));
	}

	/**
	 * Given null as valid field options, should return true.
	 */
	@Test
	public void shouldBeTrueWithNullValidOptions() {
		assertTrue("Expect true given null valid field options", MultiOptionHandler.isOptionsEmptyBlankOrNull("quantity", null));
		assertTrue("Expect true given null valid field options", MultiOptionHandler.isOptionsEmptyBlankOrNull("", null));
		assertTrue("Expect true given null valid field options", MultiOptionHandler.isOptionsEmptyBlankOrNull(null, null));
	}

	/**
	 * Given non-empty options to validate string and non-empty valid field options, should return false.
	 */
	@Test
	public void shouldBeFalseWithOptionsToValidateAndValidOptions() {
		assertFalse("Expect false given options to validate and field options",
				MultiOptionHandler.isOptionsEmptyBlankOrNull("message", MULTI_VALID_FIELD_OPTIONS));
	}

	/**
	 * Given empty options to validate string, should return null.
	 */
	@Test
	public void shouldBeNullWithEmptyOptionsToValidate() {
		assertNull("Expect null given empty options to validate", MultiOptionHandler.getInvalidOptions("", MULTI_VALID_FIELD_OPTIONS));
		assertNull("Expect null given empty options to validate", MultiOptionHandler.getInvalidOptions(" ", MULTI_VALID_FIELD_OPTIONS));
	}

	/**
	 * Given null as options to validate, should return null.
	 */
	@Test
	public void shouldBeNullWithNullOptionsToValidate() {
		assertNull("Expect null given null options to validate", MultiOptionHandler.getInvalidOptions(null, MULTI_VALID_FIELD_OPTIONS));
	}

	/**
	 * Given empty valid field options string, should return null.
	 */
	@Test
	public void shouldBeNullWithEmptyValidOptions() {
		String[] noValidFieldOptions = {};
		assertNull("Expect null given empty valid field options", MultiOptionHandler.getInvalidOptions("id", noValidFieldOptions));
		assertNull("Expect null given empty valid field options", MultiOptionHandler.getInvalidOptions("", noValidFieldOptions));
		assertNull("Expect null given empty valid field options", MultiOptionHandler.getInvalidOptions(null, noValidFieldOptions));
	}

	/**
	 * Given null as valid field options, should return null.
	 */
	@Test
	public void shouldBeNullWithNullValidOptions() {
		assertNull("Expect null given null valid field options", MultiOptionHandler.getInvalidOptions("quantity", null));
		assertNull("Expect null given null valid field options", MultiOptionHandler.getInvalidOptions("", null));
		assertNull("Expect null given null valid field options", MultiOptionHandler.getInvalidOptions(null, null));
	}

	/**
	 * Given unique valid options to validate, should return empty string array.
	 */
	@Test
	public void shouldBeEmptyArrayGivenValidOptions() {
		String[] invalidOptions = new String[]{};
		assertArrayEquals("Expect empty array given valid option to validate",
				invalidOptions, MultiOptionHandler.getInvalidOptions("name", MULTI_VALID_FIELD_OPTIONS));
		assertArrayEquals("Expect empty array given multiple valid options to validate",
				invalidOptions, MultiOptionHandler.getInvalidOptions("message, id", MULTI_VALID_FIELD_OPTIONS));
	}

	/**
	 * Given duplicate valid options to validate, should return non empty string array.
	 */
	@Test
	public void shouldBeNonEmptyArrayGivenDuplicateValidOptions() {
		String[] invalidOptions = {"message"};
		assertArrayEquals("Expect non-empty array given duplicate valid options to validate",
				invalidOptions, MultiOptionHandler.getInvalidOptions("message, message, id", MULTI_VALID_FIELD_OPTIONS));
	}

	/**
	 * Given invalid options to validate, should return non empty string array.
	 */
	@Test
	public void shouldBeNonEmptyArrayGivenInvalidOptions() {
		String[] invalidOptions = {"price"};
		assertArrayEquals("Expect non-empty array given invalid options to validate",
				invalidOptions, MultiOptionHandler.getInvalidOptions("price, id", MULTI_VALID_FIELD_OPTIONS));
	}

}