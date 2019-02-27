/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.schema.ResourceLink;

/**
 * Interface for a lookup to determine the default payment method id.
 */
public interface DefaultPaymentMethodLookup {

	/**
	 * Gets the default payment method id.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the payment method id for user id
	 */
	ExecutionResult<String> getDefaultPaymentMethodId(String scope, String userId);

	/**
	 * Gets the default payment method {@link ResourceLink}.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the default payment method {@link ResourceLink}
	 */
	ExecutionResult<ResourceLink> getDefaultPaymentMethodElementLink(String scope, String userId);
}
