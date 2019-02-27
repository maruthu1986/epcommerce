/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.core.messaging.dataimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.elasticpath.core.messaging.dataimport.DataImportEventType.DataImportEventTypeLookup;

/**
 * Test class for {@link DataImportEventTypeLookup}.
 */
public class DataImportEventTypeLookupTest {

	private final DataImportEventTypeLookup lookup = new DataImportEventTypeLookup();

	@Test
	public void verifyLookupFindsExistingValue() throws Exception {
		assertEquals("Unexpected EventType returned by lookup", DataImportEventType.IMPORT_JOB_COMPLETED,
				lookup.lookup(DataImportEventType.IMPORT_JOB_COMPLETED.getName()));
	}

	@Test
	public void verifyLookupReturnsNullWhenNoSuchValue() throws Exception {
		assertNull("Expected null returned when lookup by a name with no matches", lookup.lookup("noSuchName"));
	}

}
