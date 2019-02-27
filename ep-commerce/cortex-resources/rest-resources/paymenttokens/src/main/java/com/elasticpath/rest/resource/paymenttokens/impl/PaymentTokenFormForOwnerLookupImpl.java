/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory;

/**
 * Reads a payment token form.
 */
@Singleton
@Named("paymentTokenFormForOwnerLookup")
public class PaymentTokenFormForOwnerLookupImpl implements com.elasticpath.rest.resource.paymenttokens.PaymentTokenFormForOwnerLookup {

	private final Provider<PaymentTokenFormUriBuilder> paymentTokenFormUriBuilderFactory;
	private final CreatePaymentTokenUriBuilderFactory createPaymentTokenUriBuilderFactory;

	/**
	 * Default constructor.
	 *
	 * @param paymentTokenFormUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory}
	 * @param createPaymentTokenUriBuilderFactory the {@link CreatePaymentTokenUriBuilderFactory}
	 */
	@Inject
	public PaymentTokenFormForOwnerLookupImpl(
			@Named("paymentTokenFormUriBuilderFactory")
			final PaymentTokenFormUriBuilderFactory paymentTokenFormUriBuilderFactory,
			@Named("createPaymentTokenUriBuilderFactory")
			final CreatePaymentTokenUriBuilderFactory createPaymentTokenUriBuilderFactory) {
		this.paymentTokenFormUriBuilderFactory = paymentTokenFormUriBuilderFactory;
		this.createPaymentTokenUriBuilderFactory = createPaymentTokenUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<PaymentTokenEntity>> readPaymentTokenForm(final String createActionUri, final String createActionRel) {

		String selfUri = paymentTokenFormUriBuilderFactory.get()
				.setSourceUri(createActionUri)
				.build();
		String createPaymentTokenUri = createPaymentTokenUriBuilderFactory.get()
				.setSourceUri(createActionUri)
				.build();

		ResourceLink submitCreatePaymentTokenLink =
				ResourceLinkFactory.createUriRel(createPaymentTokenUri,
						createActionRel);
		Self formSelf = SelfFactory.createSelf(selfUri);
		ResourceState<PaymentTokenEntity> paymentTokenForm = ResourceState.Builder
				.create(PaymentTokenEntity.builder()
						.withDisplayName("")
						.withToken("")
						.build())
				.addingLinks(submitCreatePaymentTokenLink)
				.withSelf(formSelf)
				.build();

		return ExecutionResultFactory.createReadOK(paymentTokenForm);
	}
}
