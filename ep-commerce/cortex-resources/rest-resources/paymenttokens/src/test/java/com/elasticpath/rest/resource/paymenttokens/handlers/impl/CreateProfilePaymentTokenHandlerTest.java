/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
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

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.paymenttokens.integration.PaymentTokenWriterStrategy;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;

/**
 * Tests the {@link CreateProfilePaymentTokenHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateProfilePaymentTokenHandlerTest {
	private static final String DECODED_PROFILE_ID = "decodedProfileId";
	private static final String DECODED_PAYMENT_METHOD_ID = "decodedPaymentMethodId";
	private static final String ENCODED_PAYMENT_METHOD_ID = Base32Util.encode(DECODED_PAYMENT_METHOD_ID);
	private static final String PAYMENT_TOKEN_URI = "/paymentTokenUri";
	private static final String TEST_SCOPE = "testScope";

	@Mock
	private PaymentTokenWriterStrategy paymentTokenWriterStrategy;
	@Mock
	private PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	@Mock
	private PaymentMethodUriBuilder paymentMethodUriBuilder;
	@InjectMocks
	private CreateProfilePaymentTokenHandler createProfilePaymentTokenHandler;

	private PaymentTokenEntity paymentToken;
	private ResourceState<ProfileEntity> profileRepresentation;
	private PaymentTokenEntity createdPaymentToken;

	@Before
	public void setUpTestComponentsAndHappyCollaborators() {
		paymentToken = ResourceTypeFactory.createResourceEntity(PaymentTokenEntity.class);
		createdPaymentToken = PaymentTokenEntity.builder()
				.withPaymentMethodId(DECODED_PAYMENT_METHOD_ID)
				.build();


		profileRepresentation = ResourceState.Builder
				.create(ProfileEntity.builder()
						.withProfileId(DECODED_PROFILE_ID)
						.build())
				.withScope(TEST_SCOPE)
				.build();

		when(paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentToken, DECODED_PROFILE_ID, PaymentTokenOwnerType.PROFILE_TYPE, TEST_SCOPE))
				.thenReturn(ExecutionResultFactory.createCreateOKWithData(createdPaymentToken, false));

		when(paymentMethodUriBuilderFactory.get()).thenReturn(paymentMethodUriBuilder);
		when(paymentMethodUriBuilder.setPaymentMethodId(ENCODED_PAYMENT_METHOD_ID)).thenReturn(paymentMethodUriBuilder);
		when(paymentMethodUriBuilder.setScope(TEST_SCOPE)).thenReturn(paymentMethodUriBuilder);
		when(paymentMethodUriBuilder.build()).thenReturn(PAYMENT_TOKEN_URI);
	}

	@Test
	public void verifyHandledRepresentationTypeIsProfile() {
		assertEquals("The profile representation should be the owner representation handled", ProfilesMediaTypes.PROFILE.id(),
				createProfilePaymentTokenHandler.handledOwnerRepresentationType());
	}

	@Test
	public void verifyPaymentTokenIsCreatedForProfile() {
		createProfilePaymentTokenHandler.createPaymentToken(paymentToken, profileRepresentation);

		verify(paymentTokenWriterStrategy, times(1)).createPaymentTokenForOwner(paymentToken, DECODED_PROFILE_ID,
				PaymentTokenOwnerType.PROFILE_TYPE, TEST_SCOPE);
	}

	@Test
	public void ensureValidPaymentTokenForProfileIsCreatedCorrectly() {
		ExecutionResult<PaymentTokenEntity> createResult = createProfilePaymentTokenHandler.createPaymentToken(paymentToken, profileRepresentation);

		assertExecutionResult(createResult)
				.data(createdPaymentToken);
	}

	@Test
	public void ensureNewPaymentTokenAssociatedWithOwnerReturnsCreateOk() {
		ExecutionResult<PaymentTokenEntity> createResult = createProfilePaymentTokenHandler.createPaymentToken(paymentToken, profileRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void ensureReadOkWhenWriterStrategyReturnsReadOk() {
		when(paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentToken, DECODED_PROFILE_ID, PaymentTokenOwnerType.PROFILE_TYPE, TEST_SCOPE))
				.thenReturn(ExecutionResultFactory.createCreateOKWithData(createdPaymentToken, true));

		ExecutionResult<PaymentTokenEntity> createResult = createProfilePaymentTokenHandler.createPaymentToken(paymentToken, profileRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureNotFoundResultIsReturnedWhenProfileDoesNotExist() {
		when(paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentToken, DECODED_PROFILE_ID, PaymentTokenOwnerType.PROFILE_TYPE, TEST_SCOPE))
				.thenReturn(ExecutionResultFactory.<PaymentTokenEntity>createNotFound());

		ExecutionResult<PaymentTokenEntity> createResult = createProfilePaymentTokenHandler.createPaymentToken(paymentToken, profileRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenPaymentTokenFailsToBeAssociatedWithProfile() {
		when(paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentToken, DECODED_PROFILE_ID, PaymentTokenOwnerType.PROFILE_TYPE, TEST_SCOPE))
				.thenReturn(ExecutionResultFactory.<PaymentTokenEntity> createServerError(""));

		ExecutionResult<PaymentTokenEntity> createResult = createProfilePaymentTokenHandler.createPaymentToken(paymentToken, profileRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void verifyPaymentTokenUriForProfileIsBuilt() {
		createProfilePaymentTokenHandler.createPaymentTokenUri(createdPaymentToken, profileRepresentation);

		verify(paymentMethodUriBuilder, times(1)).build();
	}

	@Test
	public void verifyPaymentMethodIdForPaymentTokenUriForProfileIsSet() {
		createProfilePaymentTokenHandler.createPaymentTokenUri(createdPaymentToken, profileRepresentation);

		verify(paymentMethodUriBuilder, times(1)).setPaymentMethodId(ENCODED_PAYMENT_METHOD_ID);
	}

	@Test
	public void verifyScopeForPaymentTokenUriForProfileIsSet() {
		createProfilePaymentTokenHandler.createPaymentTokenUri(createdPaymentToken, profileRepresentation);

		verify(paymentMethodUriBuilder, times(1)).setScope(TEST_SCOPE);
	}

	@Test
	public void ensurePaymentTokenUriForProfileIsReturnedCorrectly() {
		String createdPaymentTokenUri = createProfilePaymentTokenHandler.createPaymentTokenUri(createdPaymentToken, profileRepresentation);

		assertEquals("the created payment token uri should be the same as expected", PAYMENT_TOKEN_URI, createdPaymentTokenUri);
	}
}
