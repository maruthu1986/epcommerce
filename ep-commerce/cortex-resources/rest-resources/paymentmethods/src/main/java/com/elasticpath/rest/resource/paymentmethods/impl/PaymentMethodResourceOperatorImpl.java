/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.command.DeletePaymentMethodCommand;
import com.elasticpath.rest.resource.paymentmethods.command.ReadPaymentMethodCommand;
import com.elasticpath.rest.resource.paymentmethods.command.ReadPaymentMethodsListCommand;
import com.elasticpath.rest.resource.paymentmethods.command.SelectPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Operator for paymentmethods resource.
 */
@Singleton
@Named("paymentMethodResourceOperator")
@Path(ResourceName.PATH_PART)
public final class PaymentMethodResourceOperatorImpl implements ResourceOperator {
	private final Provider<SelectPaymentMethodCommand.Builder> selectPaymentMethodCommandBuilder;
	private final Provider<ReadPaymentMethodCommand.Builder> readPaymentMethodCommandBuilder;
	private final Provider<ReadPaymentMethodsListCommand.Builder> readPaymentMethodsListCommandBuilder;
	private final Provider<DeletePaymentMethodCommand.Builder> deletePaymentMethodCommandBuilder;
	private final PaymentMethodLookup paymentMethodLookup;

	/**
	 * Constructor for injection.
	 * @param readPaymentMethodsListCommandBuilder the read payment methods list command builder
	 * @param selectPaymentMethodCommandBuilder the select payment method command builder
	 * @param readPaymentMethodCommandBuilder the read payment method for profile command builder
	 * @param deletePaymentMethodCommandBuilder the {@link com.elasticpath.rest.resource.paymentmethods.command.DeletePaymentMethodCommand}.Builder
	 * @param paymentMethodLookup the payment method lookup
	 */
	@Inject
	public PaymentMethodResourceOperatorImpl(
			@Named("readPaymentMethodsListCommandBuilder")
			final Provider<ReadPaymentMethodsListCommand.Builder> readPaymentMethodsListCommandBuilder,
			@Named("selectPaymentMethodCommandBuilder")
			final Provider<SelectPaymentMethodCommand.Builder> selectPaymentMethodCommandBuilder,
			@Named("readPaymentMethodCommandBuilder")
			final Provider<ReadPaymentMethodCommand.Builder> readPaymentMethodCommandBuilder,
			@Named("deletePaymentMethodCommandBuilder")
			final Provider<DeletePaymentMethodCommand.Builder> deletePaymentMethodCommandBuilder,
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup) {

		this.readPaymentMethodsListCommandBuilder = readPaymentMethodsListCommandBuilder;
		this.selectPaymentMethodCommandBuilder = selectPaymentMethodCommandBuilder;
		this.readPaymentMethodCommandBuilder = readPaymentMethodCommandBuilder;
		this.deletePaymentMethodCommandBuilder = deletePaymentMethodCommandBuilder;
		this.paymentMethodLookup = paymentMethodLookup;
	}


	/**
	 * Process READ of the paymentmethods list.
	 *
	 * @param scope the scope
	 * @param operation the ResourceOperation.
	 * @return the operation result containing the payment method information
	 */
	@Path(Scope.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadPaymentMethodsList(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		Command<ResourceState<LinksEntity>> readPaymentMethodsListCommand = readPaymentMethodsListCommandBuilder.get()
				.setScope(scope)
				.build();

		ExecutionResult<ResourceState<LinksEntity>> result = readPaymentMethodsListCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process READ of a specific payment method.
	 *
	 * @param scope the scope
	 * @param paymentMethodId the payment method id
	 * @param operation the ResourceOperation.
	 * @return the operation result containing the payment method information
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadPaymentMethod(
			@Scope
			final String scope,
			@ResourceId
			final String paymentMethodId,
			final ResourceOperation operation) {

		Command<ResourceState<PaymentMethodEntity>> readPaymentMethodCommand = readPaymentMethodCommandBuilder.get()
				.setScope(scope)
				.setPaymentMethodId(paymentMethodId)
				.build();

		ExecutionResult<ResourceState<PaymentMethodEntity>> result = readPaymentMethodCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process DELETE of a specific payment method.
	 *
	 * @param paymentMethodId the payment method id
	 * @param operation the ResourceOperation.
	 * @return the operation result containing the delete information
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.DELETE)
	public OperationResult processDeletePaymentMethod(
			@ResourceId
			final String paymentMethodId,
			final ResourceOperation operation) {
		DeletePaymentMethodCommand deletePaymentMethodCommand = deletePaymentMethodCommandBuilder.get()
				.setPaymentMethodId(paymentMethodId)
				.build();

		ExecutionResult<Void> executionResult = deletePaymentMethodCommand.execute();

		return OperationResultFactory.create(
				executionResult.getErrorMessage(),
				executionResult.getResourceStatus(),
				null,
				operation);
	}


	/**
	 * Selects a payment method.
	 *
	 * @param scope the scope
	 * @param paymentMethodId the payment method id
	 * @param order the order uri
	 * @param operation the ResourceOperation
	 * @return the operation result with the payment info
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processSelectPaymentMethod(
			@Scope
			final String scope,
			@ResourceId
			final String paymentMethodId,
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		SelectPaymentMethodCommand selectPaymentMethodCommand = selectPaymentMethodCommandBuilder.get()
				.setScope(scope)
				.setPaymentMethodId(paymentMethodId)
				.setOrder(order)
				.build();

		ExecutionResult<ResourceState<ResourceEntity>> result = selectPaymentMethodCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Reads the selected payment method for the order.
	 *
	 * @param order the order
	 * @param operation the operation
	 * @return the {@link OperationResult} with the selected payment method representation.
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadSelectedPaymentMethodForOrder(
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaymentMethodEntity>> result = paymentMethodLookup.findSelectedPaymentMethodForOrder(order);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

}
