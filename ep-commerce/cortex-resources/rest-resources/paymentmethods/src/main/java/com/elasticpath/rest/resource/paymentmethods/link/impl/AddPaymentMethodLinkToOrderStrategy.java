/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.link.impl;

import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create a link to payment info on the order representation.
 */
@Singleton
@Named("addPaymentMethodLinkToOrderStrategy")
public final class AddPaymentMethodLinkToOrderStrategy implements ResourceStateLinkHandler<OrderEntity> {

	private final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory;


	/**
	 * Constructor for injection.
	 *
	 * @param paymentMethodInfoUriBuilderFactory the payment method info URI builder factory
	 */
	@Inject
	public AddPaymentMethodLinkToOrderStrategy(
			@Named("paymentMethodInfoUriBuilderFactory")
			final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory) {
		this.paymentMethodInfoUriBuilderFactory = paymentMethodInfoUriBuilderFactory;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<OrderEntity> order) {

		String orderUri = ResourceStateUtil.getSelfUri(order);

		String paymentmethodInfoUri = createPaymentMethodInfoUri(orderUri);
		ResourceLink paymentMethodInfoLink = ResourceLinkFactory.create(paymentmethodInfoUri, ControlsMediaTypes.INFO.id(),
				PaymentMethodRels.PAYMENTMETHODINFO_REL, PaymentMethodRels.ORDER_REV);
		final Collection<ResourceLink> linksToAdd = new ArrayList<>(1);
		linksToAdd.add(paymentMethodInfoLink);

		return linksToAdd;
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
