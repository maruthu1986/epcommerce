/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.resource.commons.handler.registry.PaymentHandlerRegistry;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.PaymentMethodsResourceLinkFactory;
import com.elasticpath.rest.resource.paymentmethods.integration.alias.DefaultPaymentMethodLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;

/**
 * Lookup for the default BillingAddress From Core.
 */
@Singleton
@Named("defaultPaymentMethodLookup")
public final class DefaultPaymentMethodLookupImpl implements DefaultPaymentMethodLookup {

	private final DefaultPaymentMethodLookupStrategy defaultPaymentMethodLookupStrategy;
	private final PaymentMethodsResourceLinkFactory paymentMethodsResourceLinkFactory;
	private final PaymentHandlerRegistry paymentMethodHandlerRegistry;


	/**
	 * Constructor.
	 *
	 * @param defaultPaymentMethodLookupStrategy the default payment method lookup strategy
	 * @param paymentMethodsResourceLinkFactory the payment method resource link factory
	 * @param paymentMethodHandlerRegistry the payment method handler registry
	 */
	@Inject
	public DefaultPaymentMethodLookupImpl(
			@Named("defaultPaymentMethodLookupStrategy")
			final DefaultPaymentMethodLookupStrategy defaultPaymentMethodLookupStrategy,
			@Named("paymentMethodsResourceLinkFactory")
			final PaymentMethodsResourceLinkFactory paymentMethodsResourceLinkFactory,
			@Named("paymentMethodHandlerRegistry")
			final PaymentHandlerRegistry paymentMethodHandlerRegistry) {
		this.defaultPaymentMethodLookupStrategy = defaultPaymentMethodLookupStrategy;
		this.paymentMethodsResourceLinkFactory = paymentMethodsResourceLinkFactory;
		this.paymentMethodHandlerRegistry = paymentMethodHandlerRegistry;
	}


	@Override
	public ExecutionResult<String> getDefaultPaymentMethodId(final String scope, final String userId) {
		return defaultPaymentMethodLookupStrategy.getDefaultPaymentMethodId(scope, userId);
	}

	@Override
	public ExecutionResult<ResourceLink> getDefaultPaymentMethodElementLink(final String scope, final String userId) {

		PaymentMethodEntity defaultPaymentMethod = Assign.ifSuccessful(
				defaultPaymentMethodLookupStrategy.getDefaultPaymentMethod(scope, userId));
		PaymentHandler paymentMethodHandler = paymentMethodHandlerRegistry.lookupHandler(defaultPaymentMethod);

		ResourceLink defaultPaymentMethodElementLink = paymentMethodsResourceLinkFactory.createDefaultPaymentMethodElementLink(scope,
				paymentMethodHandler.representationType());

		return ExecutionResultFactory.createReadOK(defaultPaymentMethodElementLink);
	}
}
