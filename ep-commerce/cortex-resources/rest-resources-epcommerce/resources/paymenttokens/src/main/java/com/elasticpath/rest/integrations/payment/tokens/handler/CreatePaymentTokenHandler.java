/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Handles the creation of a payment token at an integration level.
 */
public interface CreatePaymentTokenHandler {
	/**
	 * Gets the handler {@link PaymentTokenOwnerType}.
	 *
	 * @return the handled {@link PaymentTokenOwnerType}
	 */
	PaymentTokenOwnerType getHandledOwnerType();

	/**
	 * Creates a payment token for an owner.
	 *
	 *
	 * @param paymentToken the {@link PaymentTokenEntity}
	 * @param decodedOwnerId the decoded owner id
	 * @param scope TODO
	 * @return {@link ExecutionResult} with updated {@link PaymentTokenEntity} if request successful
	 * NOT_FOUND if owner does not exist.
	 * SERVER_ERROR if payment token fails to be associated with owner
	 */
	ExecutionResult<PaymentTokenEntity> createPaymentTokenForOwner(PaymentToken paymentToken, String decodedOwnerId, String scope);
}
