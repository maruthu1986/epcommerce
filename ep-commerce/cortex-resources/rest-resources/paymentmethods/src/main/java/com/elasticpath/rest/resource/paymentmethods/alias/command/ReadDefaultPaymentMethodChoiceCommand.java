/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Command to read the default payment method choice.
 */
public interface ReadDefaultPaymentMethodChoiceCommand extends Command<ResourceState<ResourceEntity>> {

	/**
	 * Builder.
	 */
	interface Builder extends Command.Builder<ReadDefaultPaymentMethodChoiceCommand> {

		/**
		 * Sets the scope.
		 *
		 * @param scope he scope
		 * @return the builder
		 */
		Builder setScope(String scope);

		/**
		 * Sets the order URI.
		 *
		 * @param orderUri the order URI
		 * @return this builder.
		 */
		Builder setOrderUri(String orderUri);
	}
}
