/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;

/**
 * Services that provides look up of payment method from external system.
 */
public interface PaymentMethodLookupStrategy {

	/**
	 * Determines if a Payment is needed for an order.
	 *
	 * @param scope the scope
	 * @param decodedOrderId The decoded order id
	 * @return true if it's needed
	 */
	ExecutionResult<Boolean> isPaymentRequired(String scope, String decodedOrderId);

	/**
	 * Gets a specific payment method based on profile and payment id.
	 *
	 *
	 * @param scope the scope
	 * @param decodedPaymentMethodId the decoded payment method id
	 * @return the payment method
	 */
	ExecutionResult<PaymentMethodEntity> getPaymentMethod(String scope, String decodedPaymentMethodId);

	/**
	 * Gets the list of payment methods for the user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the payment methods ids
	 */
	ExecutionResult<Collection<String>> getPaymentMethodIds(String scope, String userId);

	/**
	 * Gets the list of payment methods for a user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the {@link PaymentMethodEntity}s for a user.
	 */
	ExecutionResult<Collection<PaymentMethodEntity>> getPaymentMethodsForUser(String scope, String userId);

	/**
	 * Determines if a payment method is selected for an order.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order id
	 *
	 * @return true if payment method is selected for the order
	 */
	ExecutionResult<Boolean> isPaymentMethodSelectedForOrder(String scope, String decodedOrderId);
	
	/**
	 * Find the selected payment method for the order.
	 *
	 * @param scope the scope
	 * @param decodedOrderId the decoded order ID
	 * @return execution result with the selected payment method ID.
	 */
	ExecutionResult<PaymentMethodEntity> getOrderPaymentMethod(String scope, String decodedOrderId);

	/**
	 * Gets the selector chosen payment method.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @param decodedOrderId the decoded order id
	 * @return the selector chosen payment method
	 */
	ExecutionResult<PaymentMethodEntity> getSelectorChosenPaymentMethod(String scope, String userId, String decodedOrderId);
	
	
}
