/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods;

import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Repository class for {@link com.elasticpath.plugin.payment.dto.PaymentMethod} objects.
 */
public interface CustomerPaymentMethodsRepository {

	/**
	 * Retrieve a specific {@link PaymentMethod} by payment method id.
	 *
	 * @param customerGuid the customer guid
	 * @param paymentMethodId the payment method id
	 * @return ExecutionResult with a specific {@link PaymentMethod}
	 */
	ExecutionResult<PaymentMethod> findPaymentMethodByCustomerGuidAndPaymentMethodId(String customerGuid, String paymentMethodId);

	/**
	 * Gets the customer's {@link PaymentMethod}s.
	 *
	 *
	 * @param customerGuid the customer guid
	 * @return ExecutionResult with the list of payment methods
	 */
	ExecutionResult<CustomerPaymentMethods> findPaymentMethodsByCustomerGuid(String customerGuid);

	/**
	 * Find the default {@link PaymentMethod} for a customer by the customer guid.
	 *
	 * @param customerGuid the customer guid
	 * @return ExecutionResult with the default {@link PaymentMethod} for the given customer
	 */
	ExecutionResult<PaymentMethod> findDefaultPaymentMethodByCustomerGuid(String customerGuid);
}
