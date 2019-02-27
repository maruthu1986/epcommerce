/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.helper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.paymenttokens.PaymenttokensMediaTypes;
import com.elasticpath.rest.resource.paymenttokens.rels.PaymentTokensResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory;

/**
 * Tests the {@link PaymentTokenLinksFactory}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenLinksFactoryTest {
	private static final String OWNER_URI = "/ownerUri";
	public static final String PAYMENT_TOKEN_FORM_URI = "/paymentTokenFormUri";

	@Mock
	private PaymentTokenFormUriBuilderFactory uriBuilderFactory;
	@Mock
	private PaymentTokenFormUriBuilder uriBuilder;
	@InjectMocks
	private PaymentTokenLinksFactory paymentTokenLinksFactory;

	@Before
	public void setupHappyCollaborators() {
		when(uriBuilderFactory.get()).thenReturn(uriBuilder);
		when(uriBuilder.setSourceUri(OWNER_URI)).thenReturn(uriBuilder);
		when(uriBuilder.build()).thenReturn(PAYMENT_TOKEN_FORM_URI);
	}

	@Test
	public void verifyUriBuilderFactoryIsInvoked() {
		paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(OWNER_URI);
		verify(uriBuilderFactory, times(1)).get();
	}

	@Test
	public void verifyUriBuilderIsInvokedWithCorrectUri() {
		paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(OWNER_URI);
		verify(uriBuilder, times(1)).setSourceUri(OWNER_URI);
	}

	@Test
	public void verifyUriIsBuilt() {
		paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(OWNER_URI);
		verify(uriBuilder, times(1)).build();
	}

	@Test
	public void ensurePaymentTokenFormLinkForOwnerIsCreatedCorrectly() {
		ResourceLink paymentTokenFormLinkForOwner = paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(OWNER_URI);

		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(PAYMENT_TOKEN_FORM_URI, PaymenttokensMediaTypes.PAYMENT_TOKEN.id(),
				PaymentTokensResourceRels.CREATE_PAYMENT_TOKEN_FORM_REL);

		assertEquals("The created payment token form link should be the same as the expected", expectedLink, paymentTokenFormLinkForOwner);
	}

}
