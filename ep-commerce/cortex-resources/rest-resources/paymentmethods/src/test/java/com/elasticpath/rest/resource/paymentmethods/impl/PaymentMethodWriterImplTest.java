/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
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
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodWriterStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Tests the {@link PaymentMethodWriterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodWriterImplTest {
	private static final String DECODED_PAYMENT_METHOD_ID = "testPaymentMethodId";
	private static final String ENCODED_PAYMENT_METHOD_ID = Base32Util.encode(DECODED_PAYMENT_METHOD_ID);
	private static final String DECODED_USER_ID = "decodedUserId";

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private PaymentMethodWriterStrategy paymentMethodWriterStrategy;
	@InjectMocks
	private PaymentMethodWriterImpl paymentMethodWriter;

	@Before
	public void setupHappyCollaborators() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(DECODED_USER_ID);
		when(paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_USER_ID, DECODED_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<Void>createDeleteOK());
	}

	@Test
	public void verifyUserIdentifierIsRetrievedForDeletePaymentMethod() {
		paymentMethodWriter.deletePaymentMethod(ENCODED_PAYMENT_METHOD_ID);

		verify(resourceOperationContext, times(1)).getUserIdentifier();
	}

	@Test
	public void verifyPaymentMethodIsDeleted() {
		paymentMethodWriter.deletePaymentMethod(ENCODED_PAYMENT_METHOD_ID);

		verify(paymentMethodWriterStrategy, times(1)).deletePaymentMethodForProfile(DECODED_USER_ID, DECODED_PAYMENT_METHOD_ID);
	}

	@Test
	public void ensurePaymentMethodCanBeDeletedSuccessfully() {
		ExecutionResult<Void> result = paymentMethodWriter.deletePaymentMethod(ENCODED_PAYMENT_METHOD_ID);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void ensureNotFoundReturnedWhenProfileOrPaymentMethodNotFoundByStrategy() {
		when(paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_USER_ID, DECODED_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<Void>createNotFound());

		ExecutionResult<Void> result = paymentMethodWriter.deletePaymentMethod(ENCODED_PAYMENT_METHOD_ID);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenPaymentMethodFailsToBeDeletedByStrategy() {
		when(paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_USER_ID, DECODED_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<Void>createServerError(""));

		ExecutionResult<Void> result = paymentMethodWriter.deletePaymentMethod(ENCODED_PAYMENT_METHOD_ID);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}
}
