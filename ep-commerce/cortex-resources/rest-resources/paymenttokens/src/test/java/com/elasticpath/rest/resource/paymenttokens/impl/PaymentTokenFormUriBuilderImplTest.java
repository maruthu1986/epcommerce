/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test for {@link PaymentTokenFormUriBuilderImpl}.
 */
public class PaymentTokenFormUriBuilderImplTest {
	private static final String OWNER_URI = "/ownerUri";
	private static final String RESOURCE_SERVER_NAME = "resourceServerName";
	private final PaymentTokenFormUriBuilder builder = new PaymentTokenFormUriBuilderImpl(RESOURCE_SERVER_NAME);
	
	@Test
	public void ensureCorrectUriConstruction() {
		String paymentTokenFormUri = builder
				.setSourceUri(OWNER_URI)
				.build();
		String expectedUri = URIUtil.format(RESOURCE_SERVER_NAME, OWNER_URI, Form.URI_PART);
		assertEquals("PaymentToken Form Uri is not correct.", expectedUri, paymentTokenFormUri);
	}
	
	@Test(expected = AssertionError.class)
	public void ensureAssertionOnMissingOwnerUri() {
		builder.setSourceUri(null)
				.build();
	}

}
