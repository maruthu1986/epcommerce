/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.link.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.paymenttokens.helper.PaymentTokenLinksFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy to assign create payment token form link to payment method links representation.
 */
@Singleton
@Named("addPaymentTokenFormLinkToPaymentMethodLinksStrategy")
public final class AddPaymentTokenFormLinkToPaymentMethodLinksStrategy implements ResourceStateLinkHandler<LinksEntity> {
	private final PaymentTokenLinksFactory paymentTokenLinksFactory;

	/**
	 * Constructor.
	 *
	 * @param paymentTokenLinksFactory the {@link com.elasticpath.rest.resource.paymenttokens.helper.PaymentTokenLinksFactory}
	 */
	@Inject
	AddPaymentTokenFormLinkToPaymentMethodLinksStrategy(
			@Named("paymentTokenLinksFactory")
			final PaymentTokenLinksFactory paymentTokenLinksFactory) {
		this.paymentTokenLinksFactory = paymentTokenLinksFactory;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<LinksEntity> linksRepresentation) {
		Collection<ResourceLink> linksToAdd = new ArrayList<>(1);
		if (PaymentMethodCommonsConstants.PAYMENT_METHOD_LINKS_NAME.equals(linksRepresentation.getEntity().getName())) {
			linksToAdd.add(paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(linksRepresentation.getScope()));
		}

		return linksToAdd;
	}
}
