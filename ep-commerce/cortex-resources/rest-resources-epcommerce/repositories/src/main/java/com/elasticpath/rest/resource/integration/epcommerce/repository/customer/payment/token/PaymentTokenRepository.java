/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.payment.token;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Repository class for {@link PaymentToken} objects.
 */
public interface PaymentTokenRepository {
	/**
	 * Add a {@link PaymentToken} for a customer identified by the customer guid.
	 *
	 * @param customerGuid the customer guid
	 * @param paymentToken the payment token
	 * @return Success on successful addition, failure otherwise.
	 */
	ExecutionResult<Void> setDefaultPaymentToken(String customerGuid, PaymentToken paymentToken);
}
