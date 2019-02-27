/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of the public API of <code>CustomerGroupImpl</code>.
 */
public class CustomerGroupImplTest {

	private CustomerGroupImpl customerGroupImpl;

	/**
	 * Preparation for each test.
	 */
	@Before
	public void setUp()  {
		customerGroupImpl = new CustomerGroupImpl();
		customerGroupImpl.initialize();
		final long uidPk = 100000L;
		customerGroupImpl.setUidPk(uidPk);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerGroupImpl.isEnabled()'.
	 */
	@Test
	public void testIsEnabled() {
		assertFalse(customerGroupImpl.isEnabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerGroupImpl.setEnabled()'.
	 */
	@Test
	public void testSetEnabled() {
		assertFalse(customerGroupImpl.isEnabled());
		customerGroupImpl.setEnabled(true);
		assertTrue(customerGroupImpl.isEnabled());
		customerGroupImpl.setEnabled(false);
		assertFalse(customerGroupImpl.isEnabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerGroupImpl.getDescription()'.
	 */
	@Test
	public void testGetDescription() {
		assertNull(customerGroupImpl.getDescription());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CustomerGroupImpl.setDescription()'.
	 */
	@Test
	public void testSetDescription() {
		final String description = "any_description_text";
		assertNull(customerGroupImpl.getDescription());
		customerGroupImpl.setDescription(description);
		assertEquals(description, customerGroupImpl.getDescription());
	}

}
