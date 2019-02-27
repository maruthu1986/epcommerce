/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Marker interface to type the Command.
 */
public interface ReadDefaultPaymentMethodCommand extends Command<ResourceState<ResourceEntity>> {

	/**
	 * Marker interface to type the Builder.
	 */
	interface Builder extends Command.Builder<ReadDefaultPaymentMethodCommand> {

		/**
		 * Sets the scope.
		 *
		 * @param scope he scope
		 * @return the builder
		 */
		Builder setScope(String scope);
	}
}
