/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.CustomerPaymentMethodsRepository;

/**
 * Repository for {@link PaymentMethod}s.
 */
@Singleton
@Named("customerPaymentMethodsRepository")
public class CustomerPaymentMethodsRepositoryImpl implements CustomerPaymentMethodsRepository {
	private final CustomerRepository customerRepository;

	/**
	 * Constructor.
	 *
	 * @param customerRepository the customer repository
	 */
	@Inject
	public CustomerPaymentMethodsRepositoryImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public ExecutionResult<PaymentMethod> findPaymentMethodByCustomerGuidAndPaymentMethodId(final String userGuid, final String paymentMethodId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(userGuid));
				PaymentMethod paymentMethod = Assign.ifNotNull(customer.getPaymentMethods().getByUidPk(Long.parseLong(paymentMethodId)),
						OnFailure.returnNotFound("The payment method was not found"));
				return ExecutionResultFactory.createReadOK(paymentMethod);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<CustomerPaymentMethods> findPaymentMethodsByCustomerGuid(final String customerGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(customerGuid));
				CustomerPaymentMethods customerPaymentMethods = customer.getPaymentMethods();
				Ensure.isTrue(!customerPaymentMethods.all().isEmpty(),
						OnFailure.returnNotFound("No payment methods found for customer with GUID %s", customerGuid));
				return ExecutionResultFactory.createReadOK(customerPaymentMethods);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<PaymentMethod> findDefaultPaymentMethodByCustomerGuid(final String customerGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(customerGuid));
				PaymentMethod defaultPaymentMethod = Assign.ifNotNull(customer.getPaymentMethods().getDefault(),
						OnFailure.returnNotFound("Could not find default payment method."));
				return ExecutionResultFactory.createReadOK(defaultPaymentMethod);
			}
		}.execute();
	}
}

