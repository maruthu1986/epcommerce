/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Writer for payment tokens.
 */
public interface PaymentTokenWriter {
	/**
	 * Creates a payment token for an owning resource.
	 *
	 * @param paymentTokenEntity the payment token
	 * @param owningRepresentation the {@link ResourceState} for the owning resource
	 * @return the {@link ResourceState} returned from the creation
	 */
	ExecutionResult<ResourceState<ResourceEntity>> createPaymentToken(PaymentTokenEntity paymentTokenEntity,
			ResourceState<?> owningRepresentation);
}
