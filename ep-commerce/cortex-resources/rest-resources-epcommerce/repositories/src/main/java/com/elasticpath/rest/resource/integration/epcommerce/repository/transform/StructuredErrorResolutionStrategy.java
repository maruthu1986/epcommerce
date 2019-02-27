/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform;

import io.reactivex.Maybe;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.rest.id.ResourceIdentifier;

/**
 * Strategy for obtaining links for structured error resolutions.
 */
public interface StructuredErrorResolutionStrategy {

	/**
	 * Transforms structured error resolution into a resource identifier.
	 *
	 * @param message a structured error message
	 * @param cortexResourceID the Id of the from cortex.
	 * @return a resource identifier, or Optional.empty() if none exists.
	 */
	Maybe<ResourceIdentifier> getResourceIdentifier(StructuredErrorMessage message, String cortexResourceID);
}
