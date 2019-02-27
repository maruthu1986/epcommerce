/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.payment.token.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.payment.token.PaymentTokenRepository;

/**
 * Default implementation of {@link PaymentTokenRepository}.
 */
@Singleton
@Named("paymentTokenRepository")
public class PaymentTokenRepositoryImpl implements PaymentTokenRepository {
	private final CustomerRepository customerRepository;

	/**
	 * Constructor.
	 *
	 * @param customerRepository the customer repository
	 */
	@Inject
	public PaymentTokenRepositoryImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public ExecutionResult<Void> setDefaultPaymentToken(final String customerGuid, final PaymentToken paymentToken) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(customerGuid));
				CustomerPaymentMethods customerPaymentMethods = customer.getPaymentMethods();
				customerPaymentMethods.setDefault(paymentToken);
				Ensure.successful(customerRepository.updateCustomer(customer));
				return ExecutionResultFactory.createUpdateOK();
			}

		}.execute();
	}

}
