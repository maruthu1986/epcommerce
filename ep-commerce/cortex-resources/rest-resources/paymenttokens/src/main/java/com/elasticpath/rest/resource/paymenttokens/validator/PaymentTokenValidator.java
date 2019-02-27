/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.validator;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;

/**
 * Validator for {@link PaymentTokenEntity}.
 * BAD_REQUEST_BODY if validation fails
 */
@Singleton
@Named("paymentTokenValidator")
public class PaymentTokenValidator implements Validator<PaymentTokenEntity> {
	/**
	 * Error message for missing payment token fields.
	 */
	static final String MISSING_PAYMENT_TOKEN_FIELDS = "Missing payment token fields";

	@Override
	public ExecutionResult<Void> validate(final PaymentTokenEntity paymentTokenEntity) {
		Ensure.notNull(paymentTokenEntity, OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		Ensure.isTrue(hasFields(paymentTokenEntity), OnFailure.returnBadRequestBody(MISSING_PAYMENT_TOKEN_FIELDS));
		return ExecutionResultFactory.createUpdateOK();
	}

	private boolean hasFields(final PaymentTokenEntity entity) {
		return entity.getDisplayName() != null && entity.getToken() != null;
	}
}
