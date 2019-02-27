/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods;

import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Resolves unique payment method identifiers.
 */
public interface PaymentMethodIdentifierResolver {
	/**
	 * Gets identifer for payment method.
	 *
	 * @param paymentMethod the payment method
	 * @return the unique identifier for the payment method.
	 */
	String getIdentifierForPaymentMethod(PaymentMethod paymentMethod);
}
