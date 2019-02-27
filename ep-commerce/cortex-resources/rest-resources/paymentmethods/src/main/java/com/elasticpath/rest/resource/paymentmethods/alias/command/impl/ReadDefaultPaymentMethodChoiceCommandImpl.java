/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.command.ReadDefaultPaymentMethodChoiceCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ReadDefaultPaymentMethodChoiceCommand}.
 */
@Named
public final class ReadDefaultPaymentMethodChoiceCommandImpl implements ReadDefaultPaymentMethodChoiceCommand {

	private final DefaultPaymentMethodLookup defaultPaymentMethodLookup;
	private final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	private final ResourceOperationContext resourceOperationContext;

	private String scope;
	private String orderUri;


	/**
	 * Constructor for injection.
	 *
	 * @param defaultPaymentMethodLookup the default payment method id lookup
	 * @param paymentMethodUriBuilderFactory the read payment method command builder factory
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	public ReadDefaultPaymentMethodChoiceCommandImpl(
			@Named("defaultPaymentMethodLookup")
			final DefaultPaymentMethodLookup defaultPaymentMethodLookup,
			@Named("paymentMethodUriBuilderFactory")
			final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.defaultPaymentMethodLookup = defaultPaymentMethodLookup;
		this.paymentMethodUriBuilderFactory = paymentMethodUriBuilderFactory;
		this.resourceOperationContext = resourceOperationContext;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {


		String userId = resourceOperationContext.getUserIdentifier();
		String defaultPaymentMethodId = Assign.ifSuccessful(
				defaultPaymentMethodLookup.getDefaultPaymentMethodId(scope, userId));

		String paymentMethodUri = paymentMethodUriBuilderFactory
				.get()
				.setScope(scope)
				.setPaymentMethodId(Base32Util.encode(defaultPaymentMethodId))
				.build();

		String selectorUri = URIUtil.format(paymentMethodUri, Selector.URI_PART, orderUri);
		return ExecutionResultFactory.createSeeOther(selectorUri);
	}

	/**
	 * Read default payment method command builder.
	 */
	@Named("readDefaultPaymentMethodChoiceCommandBuilder")
	public static class BuilderImpl implements Builder {

		private final ReadDefaultPaymentMethodChoiceCommandImpl command;


		/**
		 * Constructor for injection.
		 *
		 * @param command the cmd
		 */
		@Inject
		public BuilderImpl(final ReadDefaultPaymentMethodChoiceCommandImpl command) {
			this.command = command;
		}


		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public Builder setOrderUri(final String orderUri) {
			command.orderUri = orderUri;
			return this;
		}

		@Override
		public ReadDefaultPaymentMethodChoiceCommand build() {
			assert command.scope != null : "Scope must not be null";
			assert command.orderUri != null : "orderUri must not be null";
			return command;
		}
	}
}
