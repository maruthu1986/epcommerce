/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Contains behaviour for retrieving order payment information.
 */
public interface PaymentMethodLookup {
	/**
	 * Find the selected payment method id for the order.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @param orderId the order ID
	 * @return execution result with the selected payment method ID.
	 */
	ExecutionResult<String> findChosenPaymentMethodIdForOrder(String scope, String userId, String orderId);

	/**
	 * Find the selected payment method for the order.
	 *
	 *  @param order the order representation
	 * @return execution result with the selected payment method representation.
	 * NOT_FOUND if no payment is selected for the order or the order is not found
	 * SERVER_ERROR if an error occured at the integration layer in the process of looking up the selected payment method
	 */
	ExecutionResult<ResourceState<PaymentMethodEntity>> findSelectedPaymentMethodForOrder(ResourceState<OrderEntity> order);

	/**
	 * Determines if a Payment is needed for an order.
	 *
	 * @param scope the scope
	 * @param orderId The order id to check
	 * @return true if it's needed
	 */
	ExecutionResult<Boolean> isPaymentRequired(String scope, String orderId);

	/**
	 * Gets the payment method for a specific ID.
	 *
	 * @param scope the scope
	 * @param paymentId the payment id
	 * @return the payment method
	 */
	ExecutionResult<ResourceState<PaymentMethodEntity>> getPaymentMethod(String scope, String paymentId);

	/**
	 * Gets the list of payment methods for the user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the payment methods ids
	 */
	ExecutionResult<Collection<String>> getPaymentMethodIds(String scope, String userId);

	/**
	 * Determines if a payment method is selected for an order.
	 *
	 * @param scope the scope
	 * @param orderId the order id
	 *
	 * @return true if a payment method is selected for the order
	 */
	ExecutionResult<Boolean> isPaymentMethodSelectedForOrder(String scope, String orderId);

	/**
	 * Gets the payment method links for a user.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the {@link ResourceLink}s for a user representing their payment methods
	 */
	ExecutionResult<Collection<ResourceLink>> getPaymentMethodLinksForUser(String scope, String userId);

	/**
	 * Gets the selected payment method {@link ResourceLink} for an order.
	 *
	 * @param scope the scope
	 * @param orderId the order id
	 * @return the selected payment method {@link ResourceLink}
	 */
	ExecutionResult<ResourceLink> getSelectedPaymentMethodLinkForOrder(String scope, String orderId);
	
	/**
	 * Gets the chosen payment method {@link ResourceLink} for an order.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @param orderId the order id
	 * @return the chosen payment method {@link ResourceLink}
	 */
	ExecutionResult<ResourceLink> getSelectorChosenPaymentMethodLink(String scope, String userId, String orderId);
}
