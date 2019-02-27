/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.alias;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;

/**
 * Services that provides look up of default payment method from external system.
 */
public interface DefaultPaymentMethodLookupStrategy {

	/**
	 * Gets the default payment method id.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the default payment method id
	 */
	ExecutionResult<String> getDefaultPaymentMethodId(String scope, String userId);

	/**
	 * Gets the default payment method.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the default {@link PaymentMethodEntity}
	 */
	ExecutionResult<PaymentMethodEntity> getDefaultPaymentMethod(String scope, String userId);
}
