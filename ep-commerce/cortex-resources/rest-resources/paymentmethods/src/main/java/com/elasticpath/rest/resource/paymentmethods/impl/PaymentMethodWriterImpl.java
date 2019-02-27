/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodWriter;
import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodWriterStrategy;

/**
 * Sets the payment info on a cart order.
 */
@Singleton
@Named("paymentMethodWriter")
public final class PaymentMethodWriterImpl implements PaymentMethodWriter {
	private final PaymentMethodWriterStrategy paymentMethodWriterStrategy;
	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor.
	 *
	 * @param paymentMethodWriterStrategy the {@link PaymentMethodWriterStrategy}
	 * @param resourceOperationContext the {@link ResourceOperationContext}
	 */
	@Inject
	public PaymentMethodWriterImpl(
			@Named("paymentMethodWriterStrategy")
			final PaymentMethodWriterStrategy paymentMethodWriterStrategy,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {
		this.paymentMethodWriterStrategy = paymentMethodWriterStrategy;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public ExecutionResult<Boolean> updatePaymentMethodSelectionForOrder(final String scope, final String orderId, final String paymentMethodId) {
		return paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(scope, orderId, paymentMethodId);
	}

	@Override
	public ExecutionResult<Void> deletePaymentMethod(final String paymentMethodId) {
		String decodedUserId = resourceOperationContext.getUserIdentifier();
		return paymentMethodWriterStrategy.deletePaymentMethodForProfile(decodedUserId, Base32Util.decode(paymentMethodId));
	}
}
