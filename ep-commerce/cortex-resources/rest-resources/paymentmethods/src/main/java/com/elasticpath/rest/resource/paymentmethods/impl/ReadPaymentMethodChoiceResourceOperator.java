/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Resource Operator for reading the payment method choice.
 */
@Singleton
@Named("readPaymentMethodChoiceResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ReadPaymentMethodChoiceResourceOperator implements ResourceOperator {

	private final String resourceServerName;
	private final ResourceOperationContext operationContext;
	private final PaymentMethodLookup paymentMethodLookup;
	private final ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider;


	/**
	 * Instantiates a new read payment method choice command.
	 *
	 * @param resourceServerName the resource server name
	 * @param operationContext the resource operation context
	 * @param paymentMethodLookup the payment method lookup
	 * @param readResourceCommandBuilderProvider the read resource command builder provider
	 */
	@Inject
	public ReadPaymentMethodChoiceResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup,
			@Named("readResourceCommandBuilderProvider")
			final ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider) {

		this.readResourceCommandBuilderProvider = readResourceCommandBuilderProvider;
		this.resourceServerName = resourceServerName;
		this.operationContext = operationContext;
		this.paymentMethodLookup = paymentMethodLookup;
	}


	/**
	 * Reads a payment method for a resource.
	 *
	 * @param resourceName the resource name
	 * @param scope the scope
	 * @param paymentMethodId the payment method id
	 * @param order the order
	 * @param operation the ResourceOperation
	 * @return the operation result with the selected paymentmethod
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadPaymentMethodChoice(
			@ResourceName
			final String resourceName,
			@Scope
			final String scope,
			@ResourceId
			final String paymentMethodId,
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		String completePaymentMethodUri = URIUtil.format(resourceName, scope, paymentMethodId);

		ResourceState<PaymentMethodEntity> paymentMethod = getPaymentMethodRepresentation(completePaymentMethodUri);
		String validatedPaymentMethodId = paymentMethod.getEntity().getPaymentMethodId();

		Collection<ResourceLink> links = new ArrayList<>();
		ResourceLink descriptionLink = ResourceLinkFactory.createFromSelf(paymentMethod.getSelf(),
				SelectorRepresentationRels.DESCRIPTION);
		links.add(descriptionLink);

		String selectorUri = URIUtil.format(resourceServerName, Selector.URI_PART, order.getSelf().getUri());
		ResourceLink selectorLink = ResourceLinkFactory.createNoRev(selectorUri, ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR);
		links.add(selectorLink);

		String orderId = order.getEntity().getOrderId();

		String selectedPaymentMethodId;
		try {
			selectedPaymentMethodId = Assign.ifSuccessful(
					paymentMethodLookup.findChosenPaymentMethodIdForOrder(
							scope, operationContext.getUserIdentifier(), orderId
					)
			);
		} catch (BrokenChainException bce) {
			selectedPaymentMethodId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		if (ObjectUtils.notEqual(validatedPaymentMethodId, selectedPaymentMethodId)) {
			String uri = operationContext.getResourceOperation().getUri();
			ResourceLink selectActionLink = ResourceLinkFactory.createUriRel(uri, SelectorRepresentationRels.SELECT_ACTION);
			links.add(selectActionLink);
		}

		String paymentMethodUri = ResourceStateUtil.getSelfUri(paymentMethod);
		String selfUri = URIUtil.format(paymentMethodUri, Selector.URI_PART, order.getSelf().getUri());
		Self self = SelfFactory.createSelf(selfUri);

		ResourceState<LinksEntity> linksEntityResourceState = ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(links)
				.build();

		return OperationResultFactory.createReadOK(linksEntityResourceState, operation);
	}

	@SuppressWarnings("unchecked")
	private ResourceState<PaymentMethodEntity> getPaymentMethodRepresentation(final String paymentMethodUri) {
		ReadResourceCommand readOrderCommand = readResourceCommandBuilderProvider.get()
				.setReadLinks(false)
				.setResourceUri(paymentMethodUri)
				.build();
		return (ResourceState<PaymentMethodEntity>) Assign.ifSuccessful(readOrderCommand.execute());
	}


}
