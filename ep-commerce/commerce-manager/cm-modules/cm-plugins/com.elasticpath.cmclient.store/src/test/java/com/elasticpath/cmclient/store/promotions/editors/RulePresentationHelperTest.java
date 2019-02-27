/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions.editors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for {@code RulePresentationHelper}.
 */
public class RulePresentationHelperTest {

	/**
	 * Tests the happy path.
	 */
	@Test
	public void testHappyPath() {
		assertEquals("Registered customer", RulePresentationHelper.toMenuDisplayString("Registered customer"));  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	/**
	 * Tests replacement of one parameter. 
	 */
	@Test
	public void testReplaceOneParameter() {
		assertEquals("Brand is [] ", RulePresentationHelper.toMenuDisplayString("Brand is [{0}]"));  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	/**
	 * Tests replacement of two parameters. 
	 */
	@Test
	public void testReplaceTwoParameters() {
		assertEquals(
				"Get []  free items of SKU [] ",  //$NON-NLS-1$
				RulePresentationHelper.toMenuDisplayString("Get [{0}] free items of SKU [{1}]")); //$NON-NLS-1$
	}
	
	/**
	 * Tests truncating at newline. 
	 */
	@Test
	public void testTruncateAtNewline() {
		assertEquals(
				"Eligibility for promotion [] ",  //$NON-NLS-1$
				RulePresentationHelper.toMenuDisplayString(
						"Eligibility for promotion [{0}]\nPrefix the auto-generated coupon code with: [{1}]")); //$NON-NLS-1$
	}
}
