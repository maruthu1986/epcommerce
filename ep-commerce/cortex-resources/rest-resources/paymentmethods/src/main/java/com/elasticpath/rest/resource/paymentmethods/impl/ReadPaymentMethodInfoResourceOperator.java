/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Info;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.constant.PaymentMethodsConstants;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Strategy for reading payment method info for an order.
 */
@Singleton
@Named("readPaymentMethodInfoResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ReadPaymentMethodInfoResourceOperator implements ResourceOperator {

	private static final Logger LOG = LoggerFactory.getLogger(ReadPaymentMethodInfoResourceOperator.class);

	private final String resourceServerName;
	private final PaymentMethodLookup paymentMethodLookup;
	private final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param paymentMethodLookup the payment method lookup
	 * @param paymentMethodInfoUriBuilderFactory the payment method info uri builder factory
	 */
	@Inject
	public ReadPaymentMethodInfoResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup,
			@Named("paymentMethodInfoUriBuilderFactory")
			final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory) {
		this.paymentMethodLookup = paymentMethodLookup;
		this.resourceServerName = resourceServerName;
		this.paymentMethodInfoUriBuilderFactory = paymentMethodInfoUriBuilderFactory;
	}


	/**
	 * READ payment method selector for other resource.
	 *
	 * @param resourceName the resource server name
	 * @param orderRepresentation the order
	 * @param operation the resource operation
	 * @return the operation result with the billing info
	 */
	@Path({ Info.PATH_PART, AnyResourceUri.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadPaymentMethodInfoForOrder(
			@ResourceName
			final String resourceName,
			@AnyResourceUri
			final ResourceState<OrderEntity> orderRepresentation,
			final ResourceOperation operation) {

		String orderId = orderRepresentation.getEntity().getOrderId();
		String scope = orderRepresentation.getScope();

		ExecutionResult<ResourceLink> selectedPaymentMethodLinkResult;
		try {
			selectedPaymentMethodLinkResult = paymentMethodLookup.getSelectedPaymentMethodLinkForOrder(scope, orderId);
		} catch (BrokenChainException bce) {
			selectedPaymentMethodLinkResult = bce.getBrokenResult();
		}

		Collection<ResourceLink> links = new ArrayList<>();
		if (selectedPaymentMethodLinkResult.isSuccessful()) {
			links.add(selectedPaymentMethodLinkResult.getData());
		} else {
			ResourceStatus resourceStatus = selectedPaymentMethodLinkResult.getResourceStatus();
			if (!resourceStatus.equals(ResourceStatus.NOT_FOUND)) {
				LOG.warn("Unexpected error - {} {}", resourceStatus, selectedPaymentMethodLinkResult.getErrorMessage());
			}
		}

		String orderUri = ResourceStateUtil.getSelfUri(orderRepresentation);
		String selfUri = createPaymentMethodInfoUri(orderUri);
		Self self = SelfFactory.createSelf(selfUri);

		String paymentMethodSelectorUri = URIUtil.format(resourceServerName, Selector.URI_PART, orderUri);
		ResourceLink paymentMethodSelectorLink = ResourceLinkFactory.create(paymentMethodSelectorUri,
				ControlsMediaTypes.SELECTOR.id(), SelectorRepresentationRels.SELECTOR, PaymentMethodRels.PAYMENTMETHODINFO_REV);
		links.add(paymentMethodSelectorLink);

		ResourceLink orderLink = ResourceLinkFactory.createFromSelf(orderRepresentation.getSelf(),
				PaymentMethodCommonsConstants.ORDER_REL, PaymentMethodRels.PAYMENTMETHODINFO_REV);
		links.add(orderLink);

		ResourceState<InfoEntity> infoResourceState = ResourceState.Builder
				.create(InfoEntity.builder()
						.withInfoId(orderId)
						.withName(PaymentMethodsConstants.PAYMENT_METHOD_INFO_NAME)
						.build())
				.withSelf(self)
				.withScope(scope)
				.addingLinks(links)
				.build();

		return OperationResultFactory.createReadOK(infoResourceState, operation);
	}

	/**
	 * Creates a payment method info URI from the given order URI.
	 * @param orderUri the order URI
	 * @return the payment method info URI
	 */
	String createPaymentMethodInfoUri(final String orderUri) {
		return paymentMethodInfoUriBuilderFactory.get()
				.setSourceUri(orderUri)
				.build();
	}
}
