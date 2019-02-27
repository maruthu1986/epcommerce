/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers.registry;

import java.util.Set;

import com.elasticpath.rest.resource.paymenttokens.handlers.CreatePaymentTokenHandler;

/**
 * Registry that looks up {@link CreatePaymentTokenHandler}s.
 */
@SuppressWarnings("rawtypes")
public interface CreatePaymentTokenHandlerRegistry {

	/**
	 * Find the {@link CreatePaymentTokenHandler} for the owning representation type.
	 *
	 *
	 * @param ownerRepresentationType the owning representation type
	 * @return the {@link CreatePaymentTokenHandler} for the owning representation type
	 */
	CreatePaymentTokenHandler lookupHandler(String ownerRepresentationType);

	/**
	 * Gets all the handled owner types.
	 *
	 * @return the handled owner types
	 */
	Set<String> getHandledOwnerRepresentationTypes();
}
