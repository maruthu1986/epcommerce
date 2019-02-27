/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.core.messaging.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.elasticpath.core.messaging.order.OrderEventType.OrderEventTypeLookup;

/**
 * Test class for {@link OrderEventTypeLookup}.
 */
public class OrderEventTypeLookupTest {

	private final OrderEventTypeLookup lookup = new OrderEventTypeLookup();

	@Test
	public void verifyLookupFindsExistingValue() throws Exception {
		assertEquals("Unexpected EventType returned by lookup",
				OrderEventType.ORDER_CREATED, lookup.lookup(OrderEventType.ORDER_CREATED.getName()));
	}

	@Test
	public void verifyLookupReturnsNullWhenNoSuchValue() throws Exception {
		assertNull("Expected null returned when lookup by a name with no matches",
						lookup.lookup("noSuchName"));
	}

}