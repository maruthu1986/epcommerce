/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodWriter;
import com.elasticpath.rest.resource.paymentmethods.command.DeletePaymentMethodCommand;

/**
 * Tests {@link DeletePaymentMethodCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeletePaymentMethodCommandImplTest {
	private static final String TEST_PAYMENT_METHOD_ID = "testPaymentMethodId";

	@Mock
	private PaymentMethodWriter paymentMethodWriter;

	private DeletePaymentMethodCommand deletePaymentMethodCommand;

	@Before
	public void setUpHappyCollaborators() {
		deletePaymentMethodCommand = new DeletePaymentMethodCommandImpl
				.BuilderImpl(new DeletePaymentMethodCommandImpl(paymentMethodWriter))
				.setPaymentMethodId(TEST_PAYMENT_METHOD_ID)
				.build();

		when(paymentMethodWriter.deletePaymentMethod(TEST_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<Void>createDeleteOK());
	}

	@Test
	public void verifyPaymentMethodIsDeleted() {
		deletePaymentMethodCommand.execute();

		verify(paymentMethodWriter, times(1)).deletePaymentMethod(TEST_PAYMENT_METHOD_ID);
	}

	@Test
	public void ensurePaymentMethodCanBeDeleted() {
		ExecutionResult<Void> result = deletePaymentMethodCommand.execute();

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void ensureNotFoundReturnedWhenPaymentMethodOrProfileNotFound() {
		when(paymentMethodWriter.deletePaymentMethod(TEST_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<Void>createNotFound());

		ExecutionResult<Void> result = deletePaymentMethodCommand.execute();

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenPaymentMethodFailsToBeDeleted() {
		when(paymentMethodWriter.deletePaymentMethod(TEST_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<Void>createServerError(""));

		ExecutionResult<Void> result = deletePaymentMethodCommand.execute();

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}
}
