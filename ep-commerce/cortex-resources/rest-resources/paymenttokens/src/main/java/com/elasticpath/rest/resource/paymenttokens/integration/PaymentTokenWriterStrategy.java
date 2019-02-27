/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Service to write payment token related data to external system.
 */
public interface PaymentTokenWriterStrategy {
	/**
	 * Create a payment token for the specified owner.
	 *
	 * @param paymentTokenEntity the {@link PaymentTokenEntity}
	 * @param decodedOwnerId the decoded owner id
	 * @param ownerType the owner type
	 * @param scope TODO
	 * @return {@link com.elasticpath.rest.command.ExecutionResult} with updated {@link PaymentTokenEntity} if request successful
	 * CREATE_OK or READ_OK based on whether the owner has a pre-existing token associated with it.
	 * NOT_FOUND if owner does not exist.
	 * SERVER_ERROR if payment token fails to be associated with owner
	 */
	ExecutionResult<PaymentTokenEntity> createPaymentTokenForOwner(PaymentTokenEntity paymentTokenEntity,
																	String decodedOwnerId,
																	PaymentTokenOwnerType ownerType, String scope);
}
