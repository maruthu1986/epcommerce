/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.impl;


import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.integrations.payment.tokens.handler.CartOrdersDefaultPaymentMethodPopulator;
import com.elasticpath.rest.integrations.payment.tokens.handler.CreatePaymentTokenHandler;
import com.elasticpath.rest.integrations.payment.tokens.transformer.PaymentTokenTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Implementation of {@link CreatePaymentTokenHandler}.
 */
@Named("createProfilePaymentTokenHandler")
public class CreateProfilePaymentTokenHandler implements CreatePaymentTokenHandler {
	private final CustomerRepository customerRepository;
	private final PaymentTokenTransformer paymentTokenTransformer;
	private final CartOrdersDefaultPaymentMethodPopulator cartOrdersDefaultPaymentMethodPopulator;

	/**
	 * Constructor.
	 *
	 * @param customerRepository the {@link CustomerRepository}
	 * @param paymentTokenTransformer the {@link PaymentTokenTransformer}
	 * @param cartOrdersDefaultPaymentMethodPopulator cart orders default payment method populator.
	 */
	@Inject
	public CreateProfilePaymentTokenHandler(
			@Named("customerRepository")
			final CustomerRepository customerRepository,
			@Named("paymentTokenTransformer")
			final PaymentTokenTransformer paymentTokenTransformer,
			@Named("cartOrdersDefaultPaymentMethodPopulator")
			final CartOrdersDefaultPaymentMethodPopulator cartOrdersDefaultPaymentMethodPopulator) {
		this.customerRepository = customerRepository;
		this.paymentTokenTransformer = paymentTokenTransformer;
		this.cartOrdersDefaultPaymentMethodPopulator = cartOrdersDefaultPaymentMethodPopulator;
	}

	@Override
	public PaymentTokenOwnerType getHandledOwnerType() {
		return PaymentTokenOwnerType.PROFILE_TYPE;
	}

	@Override
	public ExecutionResult<PaymentTokenEntity> createPaymentTokenForOwner(final PaymentToken paymentToken, final String decodedOwnerId,
																		final String scope) {

		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(decodedOwnerId));
		PaymentMethod previousDefaultPaymentMethod = customer.getPaymentMethods().getDefault();
		customer.getPaymentMethods().add(paymentToken);
		Ensure.successful(customerRepository.updateCustomer(customer));
		Customer updateCustomer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(decodedOwnerId));

		PaymentMethod updatedPaymentToken = updateCustomer.getPaymentMethods().resolve(paymentToken);

		Ensure.notNull(updatedPaymentToken, OnFailure.returnStateFailure("An error occurred associating the payment token with the profile"));

		boolean defaultPaymentMethodWasUpdated = previousDefaultPaymentMethod == null
				|| !previousDefaultPaymentMethod.equals(updatedPaymentToken);

		if (defaultPaymentMethodWasUpdated) {
			cartOrdersDefaultPaymentMethodPopulator.updateAllCartOrdersPaymentMethods(updateCustomer, updatedPaymentToken, scope);
		}

		return ExecutionResultFactory.createCreateOKWithData(
				paymentTokenTransformer.transformToEntity((PaymentToken) updatedPaymentToken), false);
	}
}
