/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link com.elasticpath.rest.resource.paymenttokens.impl.CreatePaymentTokenUriBuilderImpl}.
 */
public class CreatePaymentTokenUriBuilderImplTest {
	public static final String RESOURCE_SERVER_NAME = "resourceServerName";
	public static final String TEST_ASSOCIATED_URI = "testAssociatedUri";
	private final CreatePaymentTokenUriBuilder createPaymentTokenUriBuilder = new CreatePaymentTokenUriBuilderImpl(RESOURCE_SERVER_NAME);

	@Test
	public void ensureCreatePaymentTokenUriIsBuiltCorrectly() {
		String createPaymentTokenUri = createPaymentTokenUriBuilder.setSourceUri(TEST_ASSOCIATED_URI)
				.build();

		assertEquals("The built URI should be the same as expected", URIUtil.format(RESOURCE_SERVER_NAME, TEST_ASSOCIATED_URI),
				createPaymentTokenUri);
	}

	@Test(expected = AssertionError.class)
	public void ensureAssertionErrorIsThrownWhenAssociatedUriNotSet() {
		createPaymentTokenUriBuilder.setSourceUri(null)
				.build();
	}
}
