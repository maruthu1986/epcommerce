/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.core.messaging.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.elasticpath.core.messaging.customer.CustomerEventType.CustomerEventTypeLookup;

/**
 * Test class for {@link CustomerEventTypeLookup}.
 */
public class CustomerEventTypeLookupTest {

	private final CustomerEventTypeLookup lookup = new CustomerEventTypeLookup();

	@Test
	public void verifyLookupFindsExistingValue() throws Exception {
		assertEquals("Unexpected EventType returned by lookup",
				CustomerEventType.CUSTOMER_REGISTERED, lookup.lookup(CustomerEventType.CUSTOMER_REGISTERED.getName()));
	}

	@Test
	public void verifyLookupReturnsNullWhenNoSuchValue() throws Exception {
		assertNull("Expected null returned when lookup by a name with no matches",
						lookup.lookup("noSuchName"));
	}

}