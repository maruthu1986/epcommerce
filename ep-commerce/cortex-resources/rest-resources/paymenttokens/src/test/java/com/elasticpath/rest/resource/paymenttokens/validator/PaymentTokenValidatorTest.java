/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.validator;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;

/**
 * Tests the {@link PaymentTokenValidator}.
 */
public class PaymentTokenValidatorTest {
	public static final String TEST_DISPLAY_VALUE = "testDisplayValue";
	public static final String TEST_VALUE = "testValue";
	private final PaymentTokenValidator validator = new PaymentTokenValidator();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void ensurePaymentTokenWithNoRequestBodyFailsValidationWithCorrectError() {
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));
		ExecutionResult<Void> result = validator.validate(null);

		assertExecutionResult(result)
				.isFailure()
				.errorMessage(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY);
	}

	@Test
	public void ensurePaymentTokenWithMissingFieldFailsValidationWithCorrectError() {
		PaymentTokenEntity paymentToken = PaymentTokenEntity.builder()
						.withDisplayName(TEST_DISPLAY_VALUE)
						.build();
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		ExecutionResult<Void> result = validator.validate(paymentToken);

		assertExecutionResult(result)
				.errorMessage(PaymentTokenValidator.MISSING_PAYMENT_TOKEN_FIELDS);
	}

	@Test
	public void ensurePaymentTokenWithCorrectFieldsPassesValidation() {
		PaymentTokenEntity paymentToken = PaymentTokenEntity.builder()
						.withDisplayName(TEST_DISPLAY_VALUE)
						.withToken(TEST_VALUE)
						.build();

		ExecutionResult<Void> result = validator.validate(paymentToken);

		assertExecutionResult(result)
				.isSuccessful();
	}
}
