/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.core.messaging.cmuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.elasticpath.core.messaging.cmuser.CmUserEventType.CmUserEventTypeLookup;

/**
 * Test class for {@link CmUserEventTypeLookup}.
 */
public class CmUserEventTypeLookupTest {

	private final CmUserEventTypeLookup lookup = new CmUserEventTypeLookup();

	@Test
	public void verifyLookupFindsExistingValue() throws Exception {
		assertEquals("Unexpected EventType returned by lookup", CmUserEventType.CM_USER_CREATED,
				lookup.lookup(CmUserEventType.CM_USER_CREATED.getName()));
	}

	@Test
	public void verifyLookupReturnsNullWhenNoSuchValue() throws Exception {
		assertNull("Expected null returned when lookup by a name with no matches", lookup.lookup("noSuchName"));
	}

}