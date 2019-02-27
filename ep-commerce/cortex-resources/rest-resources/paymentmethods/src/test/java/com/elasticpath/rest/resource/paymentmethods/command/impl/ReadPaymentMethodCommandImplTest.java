/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.command.ReadPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Test class for {@link ReadPaymentMethodCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadPaymentMethodCommandImplTest {
	private static final String SCOPE = "scope";
	private static final String PAYMENT_METHOD_ID = "payment_method_id";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PaymentMethodLookup mockPaymentMethodLookup;


	/**
	 * Test read payment method.
	 */
	@Test
	public void testReadPaymentMethod() {
		final ResourceState<PaymentMethodEntity> expectedRepresentation = ResourceState.Builder
				.create(PaymentMethodEntity.builder().build())
				.build();
		shouldFindPaymentMethod(expectedRepresentation);

		ReadPaymentMethodCommand command = createReadPaymentMethodCommand();
		ExecutionResult<ResourceState<PaymentMethodEntity>> result = command.execute();

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedRepresentation);
	}

	/**
	 * Test read with payment method not found.
	 */
	@Test
	public void testReadWithPaymentMethodNotFound() {
		shouldNotFindPaymentMethod();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		ReadPaymentMethodCommand command = createReadPaymentMethodCommand();
		command.execute();
	}

	private ReadPaymentMethodCommand createReadPaymentMethodCommand() {
		ReadPaymentMethodCommandImpl readPaymentMethodCommand =
				new ReadPaymentMethodCommandImpl(mockPaymentMethodLookup);

		ReadPaymentMethodCommand.Builder builder = new ReadPaymentMethodCommandImpl.BuilderImpl(readPaymentMethodCommand);
		return builder.setPaymentMethodId(PAYMENT_METHOD_ID)
				.setScope(SCOPE)
				.build();
	}

	private void shouldFindPaymentMethod(final ResourceState<PaymentMethodEntity> expectedRepresentation) {
		when(mockPaymentMethodLookup.getPaymentMethod(SCOPE, PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(expectedRepresentation));
	}

	private void shouldNotFindPaymentMethod() {
		when(mockPaymentMethodLookup.getPaymentMethod(SCOPE, PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<ResourceState<PaymentMethodEntity>>createNotFound("not_found"));
	}
}
