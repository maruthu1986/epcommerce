/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.validation;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests validators.
 */
public class EpValidatorFactoryTest {
	
	private static final String ZERO = "0"; //$NON-NLS-1$
	private static final String POSITIVE_NUMBER_LESS_THAN_ONE = "0.01"; //$NON-NLS-1$
	private static final String POSITIVE_NUMBER_GREATER_THAN_ONE = "2"; //$NON-NLS-1$
	private static final String NEGATIVE_NUMBER_GREATER_THAN_MINUS_ONE = "-0.01"; //$NON-NLS-1$
	private static final String NEGATIVE_NUMBER_LESS_THAN_MINUS_ONE = "-2"; //$NON-NLS-1$

	@Rule
	public TestContext context = new TestContext();

	/**
	 * Tests PHONE validator.
	 */
	@Test
	public void testPhoneValidator() {
		final String phone1 = "122-4545 234"; //$NON-NLS-1$
		assertTrue(EpValidatorFactory.PHONE_REQUIRED.validate(phone1).isOK());
		final String phone2 = "+1 (122) 4523-234"; //$NON-NLS-1$
		assertTrue(EpValidatorFactory.PHONE_REQUIRED.validate(phone2).isOK());
		final String phone3 = "122-4545 abcd"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PHONE_REQUIRED.validate(phone3).isOK());
		final String phone4 = "122/45.45 34.34"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PHONE_REQUIRED.validate(phone4).isOK());
		final String emptyStringPhone = ""; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PHONE_REQUIRED.validate(emptyStringPhone).isOK());
		final String oneDigitPhone = "2"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PHONE_REQUIRED.validate(oneDigitPhone).isOK());
		final String twoDigitPhone = "29"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PHONE_REQUIRED.validate(twoDigitPhone).isOK());
		final String emptyPhone = ""; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PHONE_REQUIRED.validate(emptyPhone).isOK());
		assertTrue(EpValidatorFactory.PHONE_IGNORE_SPACES.validate(emptyPhone).isOK());
	}

	/**
	 * Tests email validator.
	 */
	@Test
	public void testEmailValidator() {
		final String email1 = "abc@abc.com"; //$NON-NLS-1$
		assertTrue(EpValidatorFactory.EMAIL.validate(email1).isOK());

		final String emptyStringEmail = ""; //$NON-NLS-1$
		assertTrue(EpValidatorFactory.EMAIL.validate(emptyStringEmail).isOK());

		final String oneCharEmail = "f"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.EMAIL.validate(oneCharEmail).isOK());

		final String falseEmail1 = ".abc@abc.com"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.EMAIL.validate(falseEmail1).isOK());

		final String falseEmail2 = "@abc@abc.com"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.EMAIL.validate(falseEmail2).isOK());

		final String falseEmail3 = "abc@abc.com@"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.EMAIL.validate(falseEmail3).isOK());

		final String falseEmail4 = "abc@abc.com."; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.EMAIL.validate(falseEmail4).isOK());
	}

	/**
	 * Test product name validator.
	 */
	@Test
	public void testProductNameValidator() {
		final String productName1 = "Probe3(*+-&"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PRODUCT_NAME.validate(productName1).isOK());
		final String productNameEmptyString = ""; //$NON-NLS-1$
		assertTrue(EpValidatorFactory.PRODUCT_NAME.validate(productNameEmptyString).isOK());
		final String productNameLongString = "very very long_text_ very very long_text_ very very long_text_ very very long_text_"; //$NON-NLS-1$
		assertFalse(EpValidatorFactory.PRODUCT_NAME.validate(productNameLongString).isOK());

	}

	/**
	 * Test non negative, non zero decimal validator.
	 */
	@Test
	public void testNonNegativeNonZeroDecimalValidator() {
		assertFalse(EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL.validate(NEGATIVE_NUMBER_LESS_THAN_MINUS_ONE).isOK());
		assertFalse(EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL.validate(NEGATIVE_NUMBER_GREATER_THAN_MINUS_ONE).isOK());
		assertFalse(EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL.validate(ZERO).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL.validate(POSITIVE_NUMBER_LESS_THAN_ONE).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL.validate(POSITIVE_NUMBER_GREATER_THAN_ONE).isOK());
	}

	/**
	 * Test non negative validator.
	 */
	@Test
	public void testNonNegativeDecimalValidator() {
		assertFalse(EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL.validate(NEGATIVE_NUMBER_LESS_THAN_MINUS_ONE).isOK());
		assertFalse(EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL.validate(NEGATIVE_NUMBER_GREATER_THAN_MINUS_ONE).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL.validate(ZERO).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL.validate(POSITIVE_NUMBER_LESS_THAN_ONE).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL.validate(POSITIVE_NUMBER_GREATER_THAN_ONE).isOK());
	}

	/**
	 * Test non negative high scale validator.
	 */
	@Test
	public void testNonNegativeHighScaleDecimalValidator() {
		assertFalse(EpValidatorFactory.NON_NEGATIVE_HIGH_SCALE_BIG_DECIMAL.validate(NEGATIVE_NUMBER_LESS_THAN_MINUS_ONE).isOK());
		assertFalse(EpValidatorFactory.NON_NEGATIVE_HIGH_SCALE_BIG_DECIMAL.validate(NEGATIVE_NUMBER_GREATER_THAN_MINUS_ONE).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_HIGH_SCALE_BIG_DECIMAL.validate(ZERO).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_HIGH_SCALE_BIG_DECIMAL.validate(POSITIVE_NUMBER_LESS_THAN_ONE).isOK());
		assertTrue(EpValidatorFactory.NON_NEGATIVE_HIGH_SCALE_BIG_DECIMAL.validate(POSITIVE_NUMBER_GREATER_THAN_ONE).isOK());
	}

	/**
	 * Test non positive validator.
	 */
	@Test
	public void testNonPositiveDecimalValidator() {
		assertTrue(EpValidatorFactory.NON_POSITIVE_BIG_DECIMAL.validate(NEGATIVE_NUMBER_LESS_THAN_MINUS_ONE).isOK());
		assertTrue(EpValidatorFactory.NON_POSITIVE_BIG_DECIMAL.validate(NEGATIVE_NUMBER_GREATER_THAN_MINUS_ONE).isOK());
		assertTrue(EpValidatorFactory.NON_POSITIVE_BIG_DECIMAL.validate(ZERO).isOK());
		assertFalse(EpValidatorFactory.NON_POSITIVE_BIG_DECIMAL.validate(POSITIVE_NUMBER_LESS_THAN_ONE).isOK());
		assertFalse(EpValidatorFactory.NON_POSITIVE_BIG_DECIMAL.validate(POSITIVE_NUMBER_GREATER_THAN_ONE).isOK());
	}

}
