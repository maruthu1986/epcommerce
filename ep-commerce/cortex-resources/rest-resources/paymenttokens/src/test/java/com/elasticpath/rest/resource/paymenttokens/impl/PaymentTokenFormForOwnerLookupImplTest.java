/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;


import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.paymenttokens.PaymentTokenFormForOwnerLookup;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilder;
import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory;

/**
 * Tests the {@link PaymentTokenFormForOwnerLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenFormForOwnerLookupImplTest {
	public static final String CREATE_PAYMENT_TOKEN_URI = "/createPaymentTokenUri";
	public static final String TEST_ACTION_REL = "testactionrel";
	public static final String TEST_PAYMENT_TOKEN_FORM_SELF_URI = "/testPaymentTokenFormSelfUri";
	public static final String TEST_CREATE_ACTION_URI = "testCreateActionUri";

	@Mock
	private PaymentTokenFormUriBuilder paymentTokenFormUriBuilder;
	@Mock
	private PaymentTokenFormUriBuilderFactory paymentTokenFormUriBuilderFactory;
	@Mock
	private CreatePaymentTokenUriBuilderFactory createPaymentTokenUriBuilderFactory;
	@Mock
	private CreatePaymentTokenUriBuilder createPaymentTokenUriBuilder;

	@Before
	public void setUpTestComponentsAndHappyCollaborators() {
		when(paymentTokenFormUriBuilderFactory.get()).thenReturn(paymentTokenFormUriBuilder);
		when(paymentTokenFormUriBuilder.setSourceUri(TEST_CREATE_ACTION_URI)).thenReturn(paymentTokenFormUriBuilder);
		when(paymentTokenFormUriBuilder.build()).thenReturn(TEST_PAYMENT_TOKEN_FORM_SELF_URI);

		when(createPaymentTokenUriBuilderFactory.get()).thenReturn(createPaymentTokenUriBuilder);
		when(createPaymentTokenUriBuilder.setSourceUri(TEST_CREATE_ACTION_URI)).thenReturn(createPaymentTokenUriBuilder);
		when(createPaymentTokenUriBuilder.build()).thenReturn(CREATE_PAYMENT_TOKEN_URI);
	}

	@Test
	public void ensureFormSelfUriIsBuilt() {
		createReadPaymentTokenFormCommand().readPaymentTokenForm(TEST_CREATE_ACTION_URI, TEST_ACTION_REL);

		verify(paymentTokenFormUriBuilder, times(1)).build();
	}

	@Test
	public void ensureOwnerUriForPaymentTokenFormUriIsSet() {
		createReadPaymentTokenFormCommand().readPaymentTokenForm(TEST_CREATE_ACTION_URI, TEST_ACTION_REL);

		verify(paymentTokenFormUriBuilder, times(1)).setSourceUri(TEST_CREATE_ACTION_URI);
	}

	@Test
	public void ensureCreatePaymentTokenUriIsBuilt() {
		createReadPaymentTokenFormCommand().readPaymentTokenForm(TEST_CREATE_ACTION_URI, TEST_ACTION_REL);

		verify(createPaymentTokenUriBuilder, times(1)).build();
	}

	@Test
	public void ensureOwnerUriForCreatePaymentTokenUriIsSet() {
		createReadPaymentTokenFormCommand().readPaymentTokenForm(TEST_CREATE_ACTION_URI, TEST_ACTION_REL);

		verify(createPaymentTokenUriBuilder, times(1)).setSourceUri(TEST_CREATE_ACTION_URI);
	}

	@Test
	public void ensurePaymentTokenFormRepresentationIsCreatedSuccessfully() {
		ExecutionResult<ResourceState<PaymentTokenEntity>> result =
				createReadPaymentTokenFormCommand().readPaymentTokenForm(TEST_CREATE_ACTION_URI, TEST_ACTION_REL);

		assertExecutionResult(result)
				.isSuccessful();
	}

	@Test
	public void ensurePaymentTokenFormRepresentationIsCreatedCorrectly() {
		ExecutionResult<ResourceState<PaymentTokenEntity>> result =
				createReadPaymentTokenFormCommand().readPaymentTokenForm(TEST_CREATE_ACTION_URI, TEST_ACTION_REL);

		ResourceState<PaymentTokenEntity> paymentTokenRepresentation = result.getData();

		assertResourceState(paymentTokenRepresentation)
				.self(SelfFactory.createSelf(TEST_PAYMENT_TOKEN_FORM_SELF_URI))
				.linkCount(1)
				.containsLink(ResourceLinkFactory.createUriRel(CREATE_PAYMENT_TOKEN_URI,
						TEST_ACTION_REL));
	}

	private PaymentTokenFormForOwnerLookup createReadPaymentTokenFormCommand() {
		return new PaymentTokenFormForOwnerLookupImpl(paymentTokenFormUriBuilderFactory,
				createPaymentTokenUriBuilderFactory);
	}
}
