/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
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
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceLinkUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Strategy to build a selector for payment information.
 */
@Singleton
@Named("readPaymentMethodSelectorForOrderResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ReadPaymentMethodSelectorForOrderResourceOperator implements ResourceOperator {

	private final String resourceServerName;
	private final Provider<ReadResourceCommand.Builder> readResourceCommandBuilderProvider;
	private final PaymentMethodListUriBuilderFactory paymentMethodsUriBuilderFactory;
	private final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory;
	private final Provider<SelectorResourceStateBuilder> selectorProvider;
	private final PaymentMethodLookup paymentMethodLookup;
	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor for injection.
	 *
	 * @param resourceServerName resource server name
	 * @param readResourceCommandBuilderProvider the read resource command builder provider
	 * @param paymentMethodsUriBuilderFactory the payment methods uri builder factory
	 * @param paymentMethodInfoUriBuilderFactory the payment method info uri builder factory
	 * @param selectorProvider the selector provider
	 * @param paymentMethodLookup the payment method lookup
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	public ReadPaymentMethodSelectorForOrderResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("readResourceCommandBuilderProvider")
			final ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider,
			@Named("paymentMethodListUriBuilderFactory")
			final PaymentMethodListUriBuilderFactory paymentMethodsUriBuilderFactory,
			@Named("paymentMethodInfoUriBuilderFactory")
			final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory,
			@Named("singleSelectorResourceStateBuilder")
			final Provider<SelectorResourceStateBuilder> selectorProvider,
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.resourceServerName = resourceServerName;
		this.readResourceCommandBuilderProvider = readResourceCommandBuilderProvider;
		this.paymentMethodsUriBuilderFactory = paymentMethodsUriBuilderFactory;
		this.paymentMethodInfoUriBuilderFactory = paymentMethodInfoUriBuilderFactory;
		this.selectorProvider = selectorProvider;
		this.paymentMethodLookup = paymentMethodLookup;
		this.resourceOperationContext = resourceOperationContext;
	}

	/**
	 * READ payment method selector for other resource.
	 *
	 * @param resourceName the resource server name
	 * @param order the order
	 * @param operation the resource operation
	 * @return the operation result with the payment method info
	 */
	@Path({Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadPaymentMethodSelector(
			@ResourceName
			final String resourceName,
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		ResourceState<LinksEntity> profilePaymentMethods = getPaymentMethods(order.getScope());
		Collection<ResourceLink> paymentMethodLinks = ResourceLinkUtil.findLinksByRel(profilePaymentMethods,
				ListElementRels.ELEMENT);

		ResourceLink selectedPaymentMethodLink;
		try {
			selectedPaymentMethodLink = Assign.ifSuccessful(
					getSelectedPaymentMethodLink(
							order.getScope(),
							resourceOperationContext.getUserIdentifier(),
							order.getEntity().getOrderId(),
							paymentMethodLinks
					)
			);
		} catch (BrokenChainException bce) {
			selectedPaymentMethodLink = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, null);
		}

		ResourceState<SelectorEntity> paymentInfoSelector = createPaymentMethodSelectorRepresentation(order.getSelf(),
				paymentMethodLinks, selectedPaymentMethodLink);

		return OperationResultFactory.createReadOK(paymentInfoSelector, operation);
	}

	/**
	 * Returns the selected payment method in {@code paymentMethodLinks}.
	 *
	 * @param scope scope to lookup the selected payment method in
	 * @param orderId order to lookup the selected payment method in
	 * @param paymentMethodLinks a collection of payment method links to find the selected one in
	 * @return The selected payment method link
	 */
	private ExecutionResult<ResourceLink> getSelectedPaymentMethodLink(final String scope, final String userId,
																		final String orderId, final Collection<ResourceLink> paymentMethodLinks) {

		ResourceLink chosenPaymentMethodLink = Assign.ifSuccessful(
				paymentMethodLookup.getSelectorChosenPaymentMethodLink(scope, userId, orderId));
		String chosenPaymentMethodUri = chosenPaymentMethodLink.getUri();
		ResourceLink link = null;
		for (ResourceLink paymentMethodLink : paymentMethodLinks) {
			String paymentMethodUri = paymentMethodLink.getUri();
			if (chosenPaymentMethodUri.equals(paymentMethodUri)) {
				link = paymentMethodLink;
				break;
			}
		}

		//selected link no longer exists
		Ensure.notNull(link, OnFailure.returnNotFound());
		return ExecutionResultFactory.createReadOK(link);
	}

	/**
	 * Returns an execution result with the payment methods.
	 *
	 * @param scope The scope to search for customer's payment methods on
	 * @return An execution result with the payment methods if execution is successful, a failure execution result otherwise.
	 */
	@SuppressWarnings("unchecked")
	private ResourceState<LinksEntity> getPaymentMethods(final String scope) {
		// Build the URI for retrieving the payment methods.
		String paymentMethodsUri = paymentMethodsUriBuilderFactory.get()
				.setScope(scope)
				.build();

		// Build the command for getting the collection of payment methods, execute it, and return the results
		ReadResourceCommand readPaymentMethodsCommand = readResourceCommandBuilderProvider.get()
				.setResourceUri(paymentMethodsUri)
				.setExpectedType(CollectionsMediaTypes.LINKS.id())
				.build();
		return (ResourceState<LinksEntity>) Assign.ifSuccessful(readPaymentMethodsCommand.execute());
	}

	/**
	 * Creates a selector representation of order payment information from the {@code paymentMethodLinks}, setting the
	 * {@code selectedPaymentMethod} choice to selected if it is found in the collection of links. <br>
	 * The {@code paymentMethodLinks} are turned into choices and built into the representation. <br>
	 * The order link is also built into the representation. <br>
	 * If selectedPaymentMethod is null, then none of the payment methods are selected.
	 *
	 * @param orderSelf The self link for order
	 * @param paymentMethodLinks The input payment method links to build the choices from
	 * @param selectedPaymentMethod The payment method to set to selected
	 * @return a representation of order payment selector representation
	 */
	private ResourceState<SelectorEntity> createPaymentMethodSelectorRepresentation(final Self orderSelf,
															final Collection<ResourceLink> paymentMethodLinks,
															final ResourceLink selectedPaymentMethod) {

		String orderUri = orderSelf.getUri();

		String selfUri = URIUtil.format(resourceServerName, Selector.URI_PART, orderUri);
		SelectorResourceStateBuilder selectorResourceStateBuilder = selectorProvider.get()
				.setName(PaymentMethodsConstants.PAYMENT_METHOD_SELECTOR_NAME)
				.setSelfUri(selfUri);

		// Turn each link into a choice link and set the selected one if found
		for (ResourceLink paymentMethodLink : paymentMethodLinks) {
			String selectorUri = URIUtil.format(paymentMethodLink.getUri(), Selector.URI_PART, orderUri);
			ResourceLink paymentMethodChoiceLink = ResourceLinkFactory.createUriType(selectorUri, CollectionsMediaTypes.LINKS.id());
			selectorResourceStateBuilder.addChoice(paymentMethodChoiceLink);
		}

		if (paymentMethodLinks.contains(selectedPaymentMethod)) {
			String selectorUri = URIUtil.format(selectedPaymentMethod.getUri(), Selector.URI_PART, orderUri);
			ResourceLink selectedPaymentMethodLink = ResourceLinkFactory.createUriType(selectorUri, CollectionsMediaTypes.LINKS.id());
			selectorResourceStateBuilder.setSelection(selectedPaymentMethodLink);
		}

		// Create a link back to the payment method info object.
		ResourceLink paymentMethodLink = createPaymentMethodInfoLink(orderUri);
		selectorResourceStateBuilder.addLink(paymentMethodLink);

		return selectorResourceStateBuilder.build();
	}

	/**
	 * Creates the payment method info link for a given orderUri.
	 * @param orderUri the orderUri
	 * @return the payment method info link
	 */
	ResourceLink createPaymentMethodInfoLink(final String orderUri) {
		String paymentMethodInfoUri = paymentMethodInfoUriBuilderFactory.get()
				.setSourceUri(orderUri).build();
		return ResourceLinkFactory.create(paymentMethodInfoUri,
				ControlsMediaTypes.INFO.id(),
				PaymentMethodRels.PAYMENTMETHODINFO_REL,
				SelectorRepresentationRels.SELECTOR);
	}
}
