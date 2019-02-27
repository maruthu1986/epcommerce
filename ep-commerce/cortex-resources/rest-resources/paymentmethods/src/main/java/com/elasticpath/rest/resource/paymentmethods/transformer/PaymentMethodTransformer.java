/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * The transformer to convert a {@link PaymentMethodEntity} to a {@link com.elasticpath.rest.schema.ResourceState}.
 */
@Singleton
@Named("paymentMethodTransformer")
public class PaymentMethodTransformer {

	private final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	private final PaymentMethodListUriBuilderFactory paymentMethodListUriBuilderFactory;

	/**
	 * Default constructor.
	 *  @param paymentMethodUriBuilderFactory the payment method uri builder factory
	 * @param paymentMethodListUriBuilderFactory the payment method list uri builder factory
	 */
	@Inject
	public PaymentMethodTransformer(
			@Named("paymentMethodUriBuilderFactory")
			final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory,
			@Named("paymentMethodListUriBuilderFactory")
			final PaymentMethodListUriBuilderFactory paymentMethodListUriBuilderFactory) {
		this.paymentMethodUriBuilderFactory = paymentMethodUriBuilderFactory;
		this.paymentMethodListUriBuilderFactory = paymentMethodListUriBuilderFactory;
	}

	/**
	 * Transforms a {@link PaymentMethodEntity} to a {@link com.elasticpath.rest.schema.ResourceState}.
	 *
	 * @param scope the scope
	 * @param paymentMethodEntity the {@link PaymentMethodEntity}
	 * @return the representation
	 */
	public ResourceState<PaymentMethodEntity> transformToRepresentation(final String scope, final PaymentMethodEntity paymentMethodEntity) {
		String selfUri = paymentMethodUriBuilderFactory.get()
				.setScope(scope)
				.setPaymentMethodId(Base32Util.encode(paymentMethodEntity.getPaymentMethodId()))
				.build();
		Self self = SelfFactory.createSelf(selfUri);

		String paymentMethodsListUri = paymentMethodListUriBuilderFactory.get()
				.setScope(scope)
				.build();
		ResourceLink paymentMethodListLink = ElementListFactory.createListWithoutElement(paymentMethodsListUri, CollectionsMediaTypes.LINKS.id());

		return ResourceState.Builder.create(paymentMethodEntity)
				.withScope(scope)
				.withSelf(self)
				.addingLinks(paymentMethodListLink)
				.build();
	}
}
