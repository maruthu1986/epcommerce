/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodWriter;
import com.elasticpath.rest.resource.paymentmethods.command.SelectPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Command to read an order's payment information and return a representation of it.
 */
@Named
public final class SelectPaymentMethodCommandImpl implements SelectPaymentMethodCommand {

	private final String resourceServerName;
	//needs to be a Provider because we make several ReadResourceCommands.
	private final Provider<ReadResourceCommand.Builder> readResourceCommandBuilderProvider;
	private final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	private final PaymentMethodWriter paymentMethodWriter;

	private String scope;
	private String paymentMethodId;
	private ResourceState<OrderEntity> order;


	/**
	 * Constructor for injection.
	 *
	 * @param resourceServerName resource server name.
	 * @param readResourceCommandBuilderProvider the read resource command builder provider
	 * @param paymentMethodUriBuilderFactory the payment method uri builder factory
	 * @param paymentMethodWriter the payment method writer
	 */
	@Inject
	public SelectPaymentMethodCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("readResourceCommandBuilderProvider")
			final ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider,
			@Named("paymentMethodUriBuilderFactory")
			final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory,
			@Named("paymentMethodWriter")
			final PaymentMethodWriter paymentMethodWriter) {

		this.resourceServerName = resourceServerName;
		this.readResourceCommandBuilderProvider = readResourceCommandBuilderProvider;
		this.paymentMethodUriBuilderFactory = paymentMethodUriBuilderFactory;
		this.paymentMethodWriter = paymentMethodWriter;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {

		// Get the payment method
		ResourceState<PaymentMethodEntity> paymentMethod = getPaymentMethod();

		return updateSelectedPaymentMethod(scope, order.getEntity().getOrderId(), paymentMethod.getEntity().getPaymentMethodId());
	}

	/**
	 * Updates the selected payment method to the one with {@code paymentMethodId}.
	 *
	 * @param scope the scope
	 * @param orderId Current order
	 * @param paymentMethodId Payment method to select
	 * @return OK execution result with payment info uri and data if successful, Error execution result otherwise.
	 */
	private ExecutionResult<ResourceState<ResourceEntity>> updateSelectedPaymentMethod(final String scope, final String orderId,
			final String paymentMethodId) {

		boolean updated = Assign.ifSuccessful(paymentMethodWriter.updatePaymentMethodSelectionForOrder(scope, orderId, paymentMethodId));
		String locationUri = URIUtil.format(resourceServerName, Selector.URI_PART, order.getSelf().getUri());
		return ExecutionResultFactory.createCreateOK(locationUri, updated);
	}

	/**
	 * Returns an execution result with the payment method specified.
	 *
	 * @return execution result with the PaymentMethodRepresentation.
	 */
	@SuppressWarnings("unchecked")
	private ResourceState<PaymentMethodEntity> getPaymentMethod() {
		String paymentMethodUri = paymentMethodUriBuilderFactory.get()
				.setScope(scope)
				.setPaymentMethodId(paymentMethodId)
				.build();

		// Build the command for getting the collection of payment method, execute it,
		// and return the results
		ReadResourceCommand readPaymentMethodsCommand = readResourceCommandBuilderProvider.get()
				.setResourceUri(paymentMethodUri)
				.build();
		return (ResourceState<PaymentMethodEntity>) Assign.ifSuccessful(readPaymentMethodsCommand.execute());
	}


	/**
	 * Read billing information command builder.
	 */
	@Named("selectPaymentMethodCommandBuilder")
	public static class BuilderImpl implements SelectPaymentMethodCommand.Builder {

		private final SelectPaymentMethodCommandImpl command;

		/**
		 * Constructor for injection.
		 *
		 * @param command the command instance
		 */
		@Inject
		public BuilderImpl(final SelectPaymentMethodCommandImpl command) {
			this.command = command;
		}

		@Override
		public SelectPaymentMethodCommand build() {
			assert command.scope != null : "scope required";
			assert command.paymentMethodId != null : "paymentMethodId required";
			assert command.order != null : "order required";
			return command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public Builder setOrder(final ResourceState<OrderEntity> order) {
			command.order = order;
			return this;
		}

		@Override
		public Builder setPaymentMethodId(final String paymentMethodId) {
			command.paymentMethodId = paymentMethodId;
			return this;
		}
	}
}
