/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Marker interface to type the Command.
 */
public interface ReadPaymentMethodCommand extends Command<ResourceState<PaymentMethodEntity>> {

	/**
	 * Marker interface to type the Builder.
	 */
	interface Builder extends Command.Builder<ReadPaymentMethodCommand> {

		/**
		 * Sets the scope.
		 *
		 * @param scope the scope to set
		 * @return this builder
		 */
		Builder setScope(String scope);

		/**
		 * Sets the payment method id.
		 *
		 * @param paymentMethodId to set.
		 * @return this builder.
		 */
		Builder setPaymentMethodId(String paymentMethodId);
	}
}
