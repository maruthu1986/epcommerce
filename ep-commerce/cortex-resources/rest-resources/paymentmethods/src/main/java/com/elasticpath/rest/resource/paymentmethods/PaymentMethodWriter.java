/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Writer class for create/update operations on payment information data.
 */
public interface PaymentMethodWriter {

	/**
	 * Update selected payment method for order.
	 * If order does not already have a previously selected payment method, return false.
	 * Else, update the payment method selection and return true.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @param paymentMethodId the payment method ID
	 * @return an execution result indicating success or failure
	 */
	ExecutionResult<Boolean> updatePaymentMethodSelectionForOrder(String scope, String orderId, String paymentMethodId);

	/**
	 * Delete a payment method on profile.
	 *
	 * @param paymentMethodId the payment method id
	 * @return an {@link ExecutionResult} indicating success/failure of deleting the payment method
	 * NOT_FOUND if profile or payment method is not found
	 * SERVER_ERROR if payment method fails to be deleted
	 */
	ExecutionResult<Void> deletePaymentMethod(String paymentMethodId);
}
