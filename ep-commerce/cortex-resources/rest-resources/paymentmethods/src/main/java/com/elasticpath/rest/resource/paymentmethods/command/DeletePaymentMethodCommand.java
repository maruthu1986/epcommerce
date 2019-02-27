/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command;

import com.elasticpath.rest.command.Command;

/**
 * Command to delete payment method.
 */
public interface DeletePaymentMethodCommand extends Command<Void> {
	/**
	 * Builds {@link DeletePaymentMethodCommand}s.
	 */
	interface Builder extends Command.Builder<DeletePaymentMethodCommand> {
		/**
		 * Sets the payment method id.
		 *
		 * @param paymentMethodId the payment method id
		 * @return this builder.
		 */
		Builder setPaymentMethodId(String paymentMethodId);
	}
}
