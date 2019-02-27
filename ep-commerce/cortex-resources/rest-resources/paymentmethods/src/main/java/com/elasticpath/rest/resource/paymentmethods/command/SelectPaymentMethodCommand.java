/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Marker interface to type the Command.
 */
public interface SelectPaymentMethodCommand extends Command<ResourceState<ResourceEntity>> {

	/**
	 * Marker interface to type the Builder.
	 */
	interface Builder extends Command.Builder<SelectPaymentMethodCommand> {

		/**
		 * Set the scope of the payment method.
		 *
		 * @param scope the scope
		 * @return this builder
		 */
		Builder setScope(String scope);

		/**
		 * Sets payment method uri.
		 *
		 * @param paymentMethodId payment method ID
		 * @return this builder
		 */
		Builder setPaymentMethodId(String paymentMethodId);

		/**
		 * Sets resource uri.
		 *
		 * @param order the order
		 * @return this builder
		 */
		Builder setOrder(ResourceState<OrderEntity> order);
	}
}
