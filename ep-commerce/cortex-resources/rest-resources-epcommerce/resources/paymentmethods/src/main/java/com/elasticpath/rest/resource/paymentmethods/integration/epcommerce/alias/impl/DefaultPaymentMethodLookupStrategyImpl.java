/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.alias.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.CustomerPaymentMethodsRepository;
import com.elasticpath.rest.resource.paymentmethods.integration.alias.DefaultPaymentMethodLookupStrategy;
import com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.transform.PaymentMethodTransformer;

/**
 * Implementation of {@link DefaultPaymentMethodLookupStrategy} specific to DCE.
 */
@Singleton
@Named("defaultPaymentMethodLookupStrategy")
public class DefaultPaymentMethodLookupStrategyImpl implements DefaultPaymentMethodLookupStrategy {

	private final CustomerPaymentMethodsRepository customerPaymentMethodsRepository;
	private final PaymentMethodTransformer paymentMethodTransformer;


	/**
	 * Default Constructor.
	 *
	 * @param customerPaymentMethodsRepository the customer credit card repository
	 * @param paymentMethodTransformer the payment method transformer
	 */
	@Inject
	public DefaultPaymentMethodLookupStrategyImpl(
			@Named("customerPaymentMethodsRepository")
			final CustomerPaymentMethodsRepository customerPaymentMethodsRepository,
			@Named("paymentMethodTransformer")
			final PaymentMethodTransformer paymentMethodTransformer) {
		this.customerPaymentMethodsRepository = customerPaymentMethodsRepository;
		this.paymentMethodTransformer = paymentMethodTransformer;
	}


	@Override
	public ExecutionResult<String> getDefaultPaymentMethodId(final String storeCode, final String customerGuid) {

		PaymentMethodEntity paymentMethodEntity =
				Assign.ifSuccessful(getDefaultPaymentMethod(storeCode, customerGuid));

		return ExecutionResultFactory.createReadOK(paymentMethodEntity.getPaymentMethodId());
	}

	@Override
	public ExecutionResult<PaymentMethodEntity> getDefaultPaymentMethod(final String scope, final String customerGuid) {

		PaymentMethod defaultPaymentMethod =
				Assign.ifSuccessful(customerPaymentMethodsRepository.findDefaultPaymentMethodByCustomerGuid(customerGuid));

		return ExecutionResultFactory.createReadOK(paymentMethodTransformer.transformToEntity(defaultPaymentMethod));
	}
}
