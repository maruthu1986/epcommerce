/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.helper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.paymenttokens.PaymenttokensMediaTypes;
import com.elasticpath.rest.resource.paymenttokens.rels.PaymentTokensResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory;

/**
 * Helper class for creating payment token links.
 */
@Singleton
@Named("paymentTokenLinksFactory")
public class PaymentTokenLinksFactory {
	private final PaymentTokenFormUriBuilderFactory paymentTokenFormUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param paymentTokenFormUriBuilderFactory the {@link PaymentTokenFormUriBuilderFactory}
	 */
	@Inject
	public PaymentTokenLinksFactory(
			@Named("paymentTokenFormUriBuilderFactory")
			final PaymentTokenFormUriBuilderFactory paymentTokenFormUriBuilderFactory) {
		this.paymentTokenFormUriBuilderFactory = paymentTokenFormUriBuilderFactory;
	}

	/**
	 * Get the payment token form link for an owner.
	 *
	 * @param ownerUri the owner uri
	 * @return the {@link ResourceLink} to the payment token form.
	 */
	public ResourceLink createPaymentTokenFormLinkForOwner(final String ownerUri) {
		String formUri = paymentTokenFormUriBuilderFactory.get()
				.setSourceUri(ownerUri)
				.build();

		return ResourceLinkFactory.createNoRev(formUri,
				PaymenttokensMediaTypes.PAYMENT_TOKEN.id(), PaymentTokensResourceRels.CREATE_PAYMENT_TOKEN_FORM_REL);
	}
}
