/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service to write payment method related data to external system.
 */
public interface PaymentMethodWriterStrategy {

	/**
	 * Update payment method selection for order.
	 * If order does not already have a previously selected payment method, return false.
	 * Else, update the payment method selection and return true.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id
	 * @param decodedPaymentMethodId the decoded payment method id
	 * @return the execution result
	 */
	ExecutionResult<Boolean> updatePaymentMethodSelectionForOrder(String scope, String decodedOrderId, String decodedPaymentMethodId);

	/**
	 * Deletes a payment method for a profile.
	 *
	 *
	 * @param decodedProfileId the decoded profile id
	 * @param decodedPaymentMethodId the decoded payment method id
	 * @return DELETE_OK if delete succeeds.
	 * NOT_FOUND if payment method or profile is not found.
	 * SERVER_ERROR if an error occurs deleting the payment method
	 */
	ExecutionResult<Void> deletePaymentMethodForProfile(String decodedProfileId, String decodedPaymentMethodId);
}
