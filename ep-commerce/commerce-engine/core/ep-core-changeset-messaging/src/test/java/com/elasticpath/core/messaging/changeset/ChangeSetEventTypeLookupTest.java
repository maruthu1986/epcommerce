/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.core.messaging.changeset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.elasticpath.core.messaging.changeset.ChangeSetEventType.ChangeSetEventTypeLookup;

/**
 * Test class for {@link ChangeSetEventTypeLookup}.
 */
public class ChangeSetEventTypeLookupTest {

	private final ChangeSetEventTypeLookup lookup = new ChangeSetEventTypeLookup();

	@Test
	public void verifyLookupFindsExistingValue() throws Exception {
		assertEquals("Unexpected EventType returned by lookup",
					 ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, lookup.lookup(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH.getName()));
	}

	@Test
	public void verifyLookupReturnsNullWhenNoSuchValue() throws Exception {
		assertNull("Expected null returned when lookup by a name with no matches",
						  lookup.lookup("noSuchName"));
	}

}