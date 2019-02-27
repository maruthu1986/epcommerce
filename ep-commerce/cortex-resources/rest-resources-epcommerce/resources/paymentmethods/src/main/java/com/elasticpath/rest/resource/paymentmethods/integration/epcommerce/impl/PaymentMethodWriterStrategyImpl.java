/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodWriterStrategy;

/**
 * Implementation of {@link PaymentMethodWriterStrategy}.
 */
@Singleton
@Named("paymentMethodWriterStrategy")
public class PaymentMethodWriterStrategyImpl implements PaymentMethodWriterStrategy {
	private static final String THE_PAYMENT_METHOD_WAS_NOT_FOUND = "The payment method was not found";
	private final CartOrderRepository cartOrderRepository;
	private final CustomerRepository customerRepository;
	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Default Constructor.
	 *
	 * @param cartOrderRepository the cart order repository
	 * @param resourceOperationContext the resource operation context
	 * @param customerRepository the {@link CustomerRepository}
	 */
	@Inject
	public PaymentMethodWriterStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("customerRepository")
			final CustomerRepository customerRepository,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {
		this.cartOrderRepository = cartOrderRepository;
		this.customerRepository = customerRepository;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public ExecutionResult<Boolean> updatePaymentMethodSelectionForOrder(
			final String storeCode, final String cartOrderGuid, final String paymentMethodGuid) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));

		PaymentMethod cartOrderPaymentMethod = cartOrder.getPaymentMethod();
		boolean isUpdate = cartOrderPaymentMethod != null;

		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(resourceOperationContext.getUserIdentifier()));
		PaymentMethod paymentMethod = Assign.ifNotNull(customer.getPaymentMethods().getByUidPk(Long.parseLong(paymentMethodGuid)),
				OnFailure.returnNotFound(THE_PAYMENT_METHOD_WAS_NOT_FOUND));

		cartOrder.usePaymentMethod(paymentMethod);

		Ensure.successful(cartOrderRepository.saveCartOrder(cartOrder));
		return ExecutionResultFactory.createReadOK(isUpdate);
	}

	@Override
	public ExecutionResult<Void> deletePaymentMethodForProfile(final String decodedProfileId, final String decodedPaymentMethodId) {

		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(decodedProfileId));
		CustomerPaymentMethods customerPaymentMethods = customer.getPaymentMethods();

		PaymentMethod paymentMethod = Assign.ifNotNull(customerPaymentMethods.getByUidPk(Long.parseLong(decodedPaymentMethodId)),
				OnFailure.returnNotFound(THE_PAYMENT_METHOD_WAS_NOT_FOUND));
		Ensure.isTrue(customerPaymentMethods.remove(paymentMethod),
				OnFailure.returnServerError("An error occured removing the payment method"));

		Ensure.successful(customerRepository.updateCustomer(customer));

		return ExecutionResultFactory.createDeleteOK();
	}
}
