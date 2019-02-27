/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.command.ReadPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of {@link ReadPaymentMethodCommand}.
 */
@Named
public final class ReadPaymentMethodCommandImpl implements ReadPaymentMethodCommand {

	private final PaymentMethodLookup paymentMethodLookup;

	private String scope;
	private String paymentMethodId;


	/**
	 * Constructor for injection.
	 *
	 * @param paymentMethodLookup the payment method lookup
	 */
	@Inject
	public ReadPaymentMethodCommandImpl(
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup) {
		this.paymentMethodLookup = paymentMethodLookup;
	}


	@Override
	public ExecutionResult<ResourceState<PaymentMethodEntity>> execute() {
		ResourceState<PaymentMethodEntity> paymentMethod = Assign.ifSuccessful(
				paymentMethodLookup.getPaymentMethod(scope, paymentMethodId));

		return ExecutionResultFactory.createReadOK(paymentMethod);
	}

	/**
	 * Read payment method command builder.
	 */
	@Named("readPaymentMethodCommandBuilder")
	public static class BuilderImpl implements ReadPaymentMethodCommand.Builder {

		private final ReadPaymentMethodCommandImpl cmd;

		/**
		 * Constructor for injection.
		 *
		 * @param cmd the cmd
		 */
		@Inject
		public BuilderImpl(final ReadPaymentMethodCommandImpl cmd) {
			this.cmd = cmd;
		}

		@Override
		public ReadPaymentMethodCommand build() {
			assert cmd.scope != null : "scope required";
			assert cmd.paymentMethodId != null : "paymentMethodId required";
			return cmd;
		}

		@Override
		public Builder setScope(final String scope) {
			cmd.scope = scope;
			return this;
		}

		@Override
		public Builder setPaymentMethodId(final String paymentMethodId) {
			cmd.paymentMethodId = paymentMethodId;
			return this;
		}
	}
}
