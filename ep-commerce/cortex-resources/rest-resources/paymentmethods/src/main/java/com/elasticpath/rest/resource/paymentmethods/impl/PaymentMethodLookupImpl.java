/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.resource.commons.handler.registry.PaymentHandlerRegistry;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodLookupStrategy;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.resource.paymentmethods.transformer.OrderPaymentMethodTransformer;
import com.elasticpath.rest.resource.paymentmethods.transformer.PaymentMethodTransformer;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Implementation of {@link PaymentMethodLookup}.
 */
@Singleton
@Named("paymentMethodLookup")
public final class PaymentMethodLookupImpl implements PaymentMethodLookup {
	private final PaymentMethodTransformer paymentMethodTransformer;
	private final PaymentMethodLookupStrategy paymentMethodLookupStrategy;
	private final OrderPaymentMethodTransformer orderPaymentMethodTransformer;
	private final PaymentHandlerRegistry paymentMethodHandlerRegistry;
	private final OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory;
	private final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;

	/**
	 * Default Constructor.
	 *
	 * @param paymentMethodUriBuilderFactory      the payment method uri builder factory
	 * @param paymentMethodLookupStrategy         the payment method lookup strategy
	 * @param paymentMethodTransformer            the credit card transformer
	 * @param paymentMethodHandlerRegistry        the payment method handler registry
	 * @param orderPaymentMethodTransformer       the order payemnt method transformer
	 * @param orderPaymentMethodUriBuilderFactory the {@link OrderPaymentMethodUriBuilderFactory}
	 */
	// CHECKSTYLE:OFF
	@SuppressWarnings("PMD.ExcessiveParameterList")
	@Inject
	public PaymentMethodLookupImpl(
			@Named("paymentMethodUriBuilderFactory")
			final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory,
			@Named("paymentMethodLookupStrategy")
			final PaymentMethodLookupStrategy paymentMethodLookupStrategy,
			@Named("paymentMethodTransformer")
			final PaymentMethodTransformer paymentMethodTransformer,
			@Named("orderPaymentMethodTransformer")
			final OrderPaymentMethodTransformer orderPaymentMethodTransformer,
			@Named("paymentMethodHandlerRegistry")
			final PaymentHandlerRegistry paymentMethodHandlerRegistry,
			@Named("orderPaymentMethodUriBuilderFactory")
			final OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory) {
		this.paymentMethodUriBuilderFactory = paymentMethodUriBuilderFactory;
		this.paymentMethodTransformer = paymentMethodTransformer;
		this.paymentMethodLookupStrategy = paymentMethodLookupStrategy;
		this.orderPaymentMethodTransformer = orderPaymentMethodTransformer;
		this.paymentMethodHandlerRegistry = paymentMethodHandlerRegistry;
		this.orderPaymentMethodUriBuilderFactory = orderPaymentMethodUriBuilderFactory;
	}

	@Override
	public ExecutionResult<String> findChosenPaymentMethodIdForOrder(final String scope, final String userId, final String orderId) {

		PaymentMethodEntity paymentMethodEntity = Assign.ifSuccessful(
				paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(scope, userId, orderId));
		String paymentMethodId = paymentMethodEntity.getPaymentMethodId();

		return ExecutionResultFactory.createReadOK(paymentMethodId);
	}

	@Override
	public ExecutionResult<ResourceState<PaymentMethodEntity>> findSelectedPaymentMethodForOrder(final ResourceState<OrderEntity> order) {


		String scope = order.getScope();
		String orderId = order.getEntity().getOrderId();
		PaymentMethodEntity selectedPaymentMethodEntity = Assign.ifSuccessful(paymentMethodLookupStrategy.getOrderPaymentMethod(scope,
				orderId));
		ResourceState<PaymentMethodEntity> selectedPaymentMethodRepresentation = orderPaymentMethodTransformer.transformToRepresentation(
				order,
				selectedPaymentMethodEntity);
		return ExecutionResultFactory.createReadOK(selectedPaymentMethodRepresentation);

	}

	@Override
	public ExecutionResult<Boolean> isPaymentRequired(final String scope, final String orderId) {
		return paymentMethodLookupStrategy.isPaymentRequired(scope, orderId);
	}

	@Override
	public ExecutionResult<ResourceState<PaymentMethodEntity>> getPaymentMethod(final String scope, final String paymentMethodId) {


		String decodedPaymentMethodId = Base32Util.decode(paymentMethodId);
		PaymentMethodEntity paymentMethodEntity = Assign.ifSuccessful(
				paymentMethodLookupStrategy.getPaymentMethod(scope, decodedPaymentMethodId));
		ResourceState<PaymentMethodEntity> representation = paymentMethodTransformer.transformToRepresentation(scope, paymentMethodEntity);
		return ExecutionResultFactory.createReadOK(representation);

	}

	@Override
	public ExecutionResult<Collection<String>> getPaymentMethodIds(final String scope, final String userId) {

		Collection<String> paymentMethodIds;
		try {
			paymentMethodIds = Assign.ifSuccessful(paymentMethodLookupStrategy.getPaymentMethodIds(scope, userId));
		} catch (BrokenChainException bce) {
			return bce.getBrokenResult();
		}
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(paymentMethodIds));

	}

	@Override
	public ExecutionResult<Boolean> isPaymentMethodSelectedForOrder(final String scope, final String orderId) {
		return paymentMethodLookupStrategy.isPaymentMethodSelectedForOrder(scope, orderId);
	}

	@Override
	public ExecutionResult<Collection<ResourceLink>> getPaymentMethodLinksForUser(final String scope, final String userId) {

		Collection<PaymentMethodEntity> paymentMethods = Assign.ifSuccessful(
				paymentMethodLookupStrategy.getPaymentMethodsForUser(scope, userId));
		Collection<ResourceLink> paymentMethodLinks = new ArrayList<>(paymentMethods.size());

		for (PaymentMethodEntity paymentMethod : paymentMethods) {
			PaymentHandler paymentMethodHandler = paymentMethodHandlerRegistry.lookupHandler(paymentMethod);
			String paymentMethodUri = paymentMethodUriBuilderFactory.get()
					.setScope(scope)
					.setPaymentMethodId(Base32Util.encode(paymentMethod.getPaymentMethodId()))
					.build();
			ResourceLink paymentMethodLink = ElementListFactory.createElementOfList(paymentMethodUri,
					paymentMethodHandler.representationType());
			paymentMethodLinks.add(paymentMethodLink);
		}

		return ExecutionResultFactory.createReadOK(paymentMethodLinks);

	}

	@Override
	public ExecutionResult<ResourceLink> getSelectedPaymentMethodLinkForOrder(final String scope, final String orderId) {

		PaymentMethodEntity selectedPaymentMethod = Assign.ifSuccessful(
				paymentMethodLookupStrategy.getOrderPaymentMethod(scope, orderId));

		PaymentHandler paymentMethodHandler = paymentMethodHandlerRegistry.lookupHandler(selectedPaymentMethod);
		String paymentMethodUri = orderPaymentMethodUriBuilderFactory.get()
				.setScope(scope)
				.setOrderId(Base32Util.encode(orderId))
				.build();
		ResourceLink selectedPaymentMethodLink = ResourceLinkFactory.createNoRev(paymentMethodUri,
				paymentMethodHandler.representationType(), PaymentMethodRels.PAYMENTMETHOD_REL);

		return ExecutionResultFactory.createReadOK(selectedPaymentMethodLink);

	}

	@Override
	public ExecutionResult<ResourceLink> getSelectorChosenPaymentMethodLink(final String scope, final String userId, final String orderId) {

		PaymentMethodEntity chosenPaymentMethod = Assign.ifSuccessful(
				paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(scope, userId, orderId));
		ResourceLink selectedPaymentMethodLink = createPaymentMethodLink(scope, chosenPaymentMethod);
		return ExecutionResultFactory.createReadOK(selectedPaymentMethodLink);

	}

	private ResourceLink createPaymentMethodLink(final String scope, final PaymentMethodEntity selectedPaymentMethod) {
		PaymentHandler paymentMethodHandler = paymentMethodHandlerRegistry.lookupHandler(selectedPaymentMethod);
		String paymentMethodUri = paymentMethodUriBuilderFactory.get()
				.setScope(scope)
				.setPaymentMethodId(Base32Util.encode(selectedPaymentMethod.getPaymentMethodId()))
				.build();

		return ResourceLinkFactory.createNoRev(paymentMethodUri,
				paymentMethodHandler.representationType(), PaymentMethodRels.PAYMENTMETHOD_REL);
	}
}
