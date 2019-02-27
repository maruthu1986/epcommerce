/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy used to create a {@link PaymentTokenEntity} for an owning resource.
 * @param <T> the resource entity type
 */
public interface CreatePaymentTokenHandler<T extends ResourceEntity> {
	/**
	 * Indicated which implementation of an owning {@link ResourceState} this handler applies to.
	 *
	 * @return the owning {@link ResourceState} type
	 */
	String handledOwnerRepresentationType();

	/**
	 * Creates a {@link PaymentTokenEntity} for the owning {@link ResourceState}.
	 *
	 * @param paymentTokenEntity the {@link PaymentTokenEntity} to create
	 * @param owningResourceState the owning {@link ResourceState}
	 * @return the created {@link PaymentTokenEntity} for the owning {@link ResourceState}
	 * NOT_FOUND if owning representation does not exist
	 * SERVER_ERROR if the payment token fails to be associated with the owning representation
	 */
	ExecutionResult<PaymentTokenEntity> createPaymentToken(PaymentTokenEntity paymentTokenEntity, ResourceState<T> owningResourceState);

	/**
	 * Creates the uri for the newly created {@link PaymentTokenEntity} for the owning {@link ResourceState}.
	 *
	 * @param paymentTokenEntity the {@link PaymentTokenEntity} to create the uri for.
	 * @param owningResourceState the owning {@link ResourceState}
	 * @return the uri for the created {@link PaymentTokenEntity}
	 */
	String createPaymentTokenUri(PaymentTokenEntity paymentTokenEntity, ResourceState<T> owningResourceState);
}
