/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.PaymentMethodIdentifierResolver;

/**
 * Resolves unique payment method identifiers.
 *
 * This implementation relies on the UidPk off the {@link AbstractPaymentMethodImpl} for a unique identifier.
 */
@Singleton
@Named("paymentMethodIdentifierResolver")
public class PaymentMethodIdentifierResolverImpl implements PaymentMethodIdentifierResolver {
	@Override
	public String getIdentifierForPaymentMethod(final PaymentMethod paymentMethod) {
		return String.valueOf(((AbstractPaymentMethodImpl) paymentMethod).getUidPk());
	}
}
