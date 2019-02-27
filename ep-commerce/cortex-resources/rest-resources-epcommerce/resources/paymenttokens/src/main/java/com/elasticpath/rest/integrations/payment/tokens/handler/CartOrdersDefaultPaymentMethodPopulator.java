/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Update cart orders default payment methods.
 */
public interface CartOrdersDefaultPaymentMethodPopulator {

	/**
	 * Update cart order default payment methods, if they don't exist.
	 *
	 * @param customer customer to get cart orders for.
	 * @param paymentMethod payment to set as default on cart orders
	 * @param scope scope of cart orders.
	 */
	void updateAllCartOrdersPaymentMethods(Customer customer, PaymentMethod paymentMethod, String scope);

}
