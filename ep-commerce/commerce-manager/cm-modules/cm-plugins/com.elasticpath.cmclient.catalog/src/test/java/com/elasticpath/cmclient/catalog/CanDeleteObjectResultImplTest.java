/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Test for CanDeleteObjectResultImpl.
 */
public class CanDeleteObjectResultImplTest extends TestCase {

	/**
	 * Test that default constructor returns canDelete result that
	 * denotes that object can be deleted.
	 */
	@Test
	public void testDefaultConstructor() {
		
		CanDeleteObjectResult result = new CanDeleteObjectResultImpl();
		assertTrue(result.canDelete());
		assertEquals(0, result.getReason());
		assertNull(result.getMessage());
		
	}
	
	/**
	 * Test that parameterized constructor with 0 reason returns canDelete result that
	 * denotes that object can be deleted.
	 */
	@Test
	public void testParameterizedConstructor() {
		
		try {
			new CanDeleteObjectResultImpl(0);
			fail();
		} catch (IllegalArgumentException iae) { // NOPMD
			// test passes
		}
		
	}
	
	/**
	 * Test that parameterized constructor with reason returns canDelete result that
	 * denotes that object cannot be deleted.
	 */
	@Test
	public void testParameterizedConstructorWithReason() {
		
		CanDeleteObjectResult result = new CanDeleteObjectResultImpl(1);
		assertFalse(result.canDelete());
		assertEquals(1, result.getReason());
		assertNull(result.getMessage());
		
	}

	/**
	 * Test that parameterized constructor with reason returns canDelete result that
	 * denotes that object cannot be deleted.
	 */
	@Test
	public void testParameterizedConstructorWithReasonAndMessage() {
		
		CanDeleteObjectResult result = new CanDeleteObjectResultImpl(1, "some string"); //$NON-NLS-1$
		assertFalse(result.canDelete());
		assertEquals(1, result.getReason());
		assertEquals("some string", result.getMessage()); //$NON-NLS-1$
		
	}
	
}
