/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.alias.command.ReadDefaultPaymentMethodChoiceCommand;
import com.elasticpath.rest.resource.paymentmethods.alias.command.ReadDefaultPaymentMethodCommand;
import com.elasticpath.rest.resource.paymentmethods.command.SelectPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Processes the resource operation which reads the default payment method.
 */
@Singleton
@Named("readDefaultPaymentMethodResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Default.PATH_PART})
public final class DefaultPaymentMethodResourceOperatorImpl implements ResourceOperator {

	private final Provider<ReadDefaultPaymentMethodCommand.Builder> readDefaultPaymentMethodCommandBuilder;
	private final Provider<SelectPaymentMethodCommand.Builder> selectPaymentMethodCommandBuilder;
	private final Provider<ReadDefaultPaymentMethodChoiceCommand.Builder> readDefaultPaymentMethodChoiceCommandBuilder;


	/**
	 * Constructor for injection.
	 *
	 * @param readDefaultPaymentMethodCommandBuilder the read default payment method command builder
	 * @param readDefaultPaymentMethodChoiceCommandBuilder the read payment method choice command builder
	 * @param selectPaymentMethodCommandBuilder the select payment method command builder
	 */
	@Inject
	public DefaultPaymentMethodResourceOperatorImpl(
			@Named("readDefaultPaymentMethodCommandBuilder")
			final Provider<ReadDefaultPaymentMethodCommand.Builder> readDefaultPaymentMethodCommandBuilder,
			@Named("readDefaultPaymentMethodChoiceCommandBuilder")
			final Provider<ReadDefaultPaymentMethodChoiceCommand.Builder> readDefaultPaymentMethodChoiceCommandBuilder,
			@Named("selectPaymentMethodCommandBuilder")
			final Provider<SelectPaymentMethodCommand.Builder> selectPaymentMethodCommandBuilder) {

		this.readDefaultPaymentMethodCommandBuilder = readDefaultPaymentMethodCommandBuilder;
		this.readDefaultPaymentMethodChoiceCommandBuilder = readDefaultPaymentMethodChoiceCommandBuilder;
		this.selectPaymentMethodCommandBuilder = selectPaymentMethodCommandBuilder;
	}


	/**
	 * Process READ of default payment method.
	 *
	 * @param scope the scope
	 * @param operation the ResourceOperation
	 * @return the operation result containing the payment method information
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadDefault(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		ReadDefaultPaymentMethodCommand readPaymentMethodCommand = readDefaultPaymentMethodCommandBuilder.get()
				.setScope(scope)
				.build();

		ExecutionResult<ResourceState<ResourceEntity>> result = readPaymentMethodCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Selects the default payment method.
	 *
	 * @param scope the scope
	 * @param order the order
	 * @param operation the ResourceOperation
	 * @return the operation result with the payment info
	 */
	@Path({Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processSelectDefaultPaymentMethod(
			@Scope
			final String scope,
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		SelectPaymentMethodCommand selectPaymentMethodCommand = selectPaymentMethodCommandBuilder.get()
				.setOrder(order)
				.setScope(scope)
				.setPaymentMethodId(Default.URI_PART)
				.build();

		ExecutionResult<ResourceState<ResourceEntity>> result = selectPaymentMethodCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Reads the default payment method for a resource.
	 *
	 * @param scope the scope
	 * @param resourceUri the resource uri
	 * @param operation the ResourceOperation
	 * @return the operation result with the selected paymentmethod
	 */
	@Path({Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadDefaultPaymentMethodChoice(
			@Scope
			final String scope,
			@AnyResourceUri
			final String resourceUri,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ResourceEntity>> result = readDefaultPaymentMethodChoiceCommandBuilder.get()
				.setScope(scope)
				.setOrderUri(resourceUri)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
