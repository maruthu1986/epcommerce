/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.paymenttokens.helper.PaymentTokenLinksFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceLinkUtil;

/**
 * Strategy to assign create payment token form links to a payment method info representation.
 */
@Singleton
@Named("addPaymentTokenFormLinkToPaymentMethodInfoStrategy")
public final class AddPaymentTokenFormLinkToPaymentMethodInfoStrategy implements ResourceStateLinkHandler<InfoEntity> {
	private final PaymentTokenLinksFactory paymentTokenLinksFactory;

	/**
	 * Constructor.
	 *
	 * @param paymentTokenLinksFactory the {@link com.elasticpath.rest.resource.paymenttokens.helper.PaymentTokenLinksFactory}
	 */
	@Inject
	AddPaymentTokenFormLinkToPaymentMethodInfoStrategy(
			@Named("paymentTokenLinksFactory")
			final PaymentTokenLinksFactory paymentTokenLinksFactory) {
		this.paymentTokenLinksFactory = paymentTokenLinksFactory;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<InfoEntity> infoRepresentation) {
		final Collection<ResourceLink> result;

		if (PaymentMethodCommonsConstants.PAYMENT_METHOD_INFO_NAME.equals(infoRepresentation.getEntity().getName())) {
			Collection<ResourceLink> orderLinks = ResourceLinkUtil.findLinksByRel(infoRepresentation,
					PaymentMethodCommonsConstants.ORDER_REL);
			
			if (orderLinks.isEmpty()) {
				result = Collections.emptyList();
			} else {
				ResourceLink orderResourceLink = orderLinks.iterator().next();

				String orderUri = orderResourceLink.getUri();
				ResourceLink formLink = paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(orderUri);
				result = Collections.singleton(formLink);
			}
		} else {
			result = Collections.emptyList();
		}

		return result;
	}
}
