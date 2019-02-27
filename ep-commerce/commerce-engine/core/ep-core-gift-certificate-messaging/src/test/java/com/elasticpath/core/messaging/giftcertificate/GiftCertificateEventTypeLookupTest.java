/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.core.messaging.giftcertificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for {@link GiftCertificateEventType.GiftCertificateEventTypeLookup}.
 */
public class GiftCertificateEventTypeLookupTest {

	private final GiftCertificateEventType.GiftCertificateEventTypeLookup lookup = new GiftCertificateEventType.GiftCertificateEventTypeLookup();

	@Test
	public void verifyLookupFindsExistingValue() throws Exception {
		assertEquals("Unexpected EventType returned by lookup", GiftCertificateEventType.GIFT_CERTIFICATE_CREATED,
				lookup.lookup(GiftCertificateEventType.GIFT_CERTIFICATE_CREATED.getName()));
	}

	@Test
	public void verifyLookupReturnsNullWhenNoSuchValue() throws Exception {
		assertNull("Expected null returned when lookup by a name with no matches", lookup.lookup("noSuchName"));
	}

}