/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.integrations.payment.tokens.handler.CreatePaymentTokenHandler;
import com.elasticpath.rest.integrations.payment.tokens.transformer.PaymentTokenTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Implement {@link CreatePaymentTokenHandler} for orders.
 */
@Named("createOrderPaymentTokenHandler")
public class CreateOrderPaymentTokenHandler implements CreatePaymentTokenHandler {
	private final CartOrderRepository cartOrderRepository;
	private final PaymentTokenTransformer paymentTokenTransformer;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository the {@link CartOrderRepository}
	 * @param paymentTokenTransformer the {@link PaymentTokenTransformer}
	 */
	@Inject
	public CreateOrderPaymentTokenHandler(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("paymentTokenTransformer")
			final PaymentTokenTransformer paymentTokenTransformer) {
		this.cartOrderRepository = cartOrderRepository;
		this.paymentTokenTransformer = paymentTokenTransformer;
	}

	@Override
	public PaymentTokenOwnerType getHandledOwnerType() {
		return PaymentTokenOwnerType.ORDER_TYPE;
	}

	@Override
	public ExecutionResult<PaymentTokenEntity> createPaymentTokenForOwner(final PaymentToken paymentToken,
																			final String decodedOwnerId,
																			final String scope) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(scope, decodedOwnerId));

		PaymentMethod existingPaymentMethodOnCartOrder = cartOrder.getPaymentMethod();
		Boolean isPaymentTokenPreExisting = paymentToken.equals(existingPaymentMethodOnCartOrder);

		if (isPaymentTokenPreExisting) {
			return ExecutionResultFactory.createReadOK(
					paymentTokenTransformer.transformToEntity((PaymentToken) cartOrder.getPaymentMethod()));
		}

		cartOrder.usePaymentMethod(paymentToken);
		CartOrder updatedCartOrder = Assign.ifSuccessful(cartOrderRepository.saveCartOrder(cartOrder));

		return ExecutionResultFactory.createCreateOKWithData(
				paymentTokenTransformer.transformToEntity((PaymentToken) updatedCartOrder.getPaymentMethod()), false);
	}
}
