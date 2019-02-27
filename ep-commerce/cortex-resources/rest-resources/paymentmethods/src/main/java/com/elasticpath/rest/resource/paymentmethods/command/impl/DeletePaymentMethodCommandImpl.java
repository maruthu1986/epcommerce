/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodWriter;
import com.elasticpath.rest.resource.paymentmethods.command.DeletePaymentMethodCommand;

/**
 * Implementation of {@link DeletePaymentMethodCommand}.
 */
@Named
public final class DeletePaymentMethodCommandImpl implements DeletePaymentMethodCommand {
	private final PaymentMethodWriter paymentMethodWriter;

	private String paymentMethodId;


	/**
	 * Constructor for injection.
	 *
	 * @param paymentMethodWriter the payment method writer
	 */
	@Inject
	public DeletePaymentMethodCommandImpl(
			@Named("paymentMethodWriter")
			final PaymentMethodWriter paymentMethodWriter) {
		this.paymentMethodWriter = paymentMethodWriter;
	}


	@Override
	public ExecutionResult<Void> execute() {
		return paymentMethodWriter.deletePaymentMethod(paymentMethodId);
	}

	/**
	 * Delete payment method command builder.
	 */
	@Named("deletePaymentMethodCommandBuilder")
	public static class BuilderImpl implements Builder {

		private final DeletePaymentMethodCommandImpl cmd;

		/**
		 * Constructor for injection.
		 *
		 * @param cmd the cmd
		 */
		@Inject
		public BuilderImpl(final DeletePaymentMethodCommandImpl cmd) {
			this.cmd = cmd;
		}

		@Override
		public Builder setPaymentMethodId(final String paymentMethodId) {
			cmd.paymentMethodId = paymentMethodId;
			return this;
		}

		@Override
		public DeletePaymentMethodCommand build() {
			assert cmd.paymentMethodId != null : "paymentMethodId required";
			return cmd;
		}
	}
}
