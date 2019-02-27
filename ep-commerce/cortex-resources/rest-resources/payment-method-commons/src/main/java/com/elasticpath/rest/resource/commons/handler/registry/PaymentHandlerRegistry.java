/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.commons.handler.registry;

import java.util.Set;

import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Registry that looks up {@link PaymentHandler}s.
 */
public interface PaymentHandlerRegistry {

	/**
	 * Looks up a {@link PaymentHandler} for a given payment {@link com.elasticpath.rest.schema.ResourceEntity}.
	 *
	 * @param paymentEntity the payment {@link ResourceEntity} to use for lookup
	 * @return the {@link PaymentHandler} for the payment {@link ResourceEntity}
	 */
	PaymentHandler lookupHandler(ResourceEntity paymentEntity);

	/**
	 * Gets all the handled payment types.
	 *
	 * @return the handled payment types
	 */
	Set<String> getHandledPaymentRepresentationTypes();
}
