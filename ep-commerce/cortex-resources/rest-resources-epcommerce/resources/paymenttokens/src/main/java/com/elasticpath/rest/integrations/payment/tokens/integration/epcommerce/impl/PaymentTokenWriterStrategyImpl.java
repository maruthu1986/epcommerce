/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.integrations.payment.tokens.handler.CreatePaymentTokenHandler;
import com.elasticpath.rest.integrations.payment.tokens.handler.registry.CreatePaymentTokenHandlerRegistry;
import com.elasticpath.rest.integrations.payment.tokens.transformer.PaymentTokenTransformer;
import com.elasticpath.rest.resource.paymenttokens.integration.PaymentTokenWriterStrategy;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Ep commerce implementation of {@link PaymentTokenWriterStrategy}.
 */
@Named("paymentTokenWriterStrategy")
public class PaymentTokenWriterStrategyImpl implements PaymentTokenWriterStrategy {
	private final PaymentTokenTransformer paymentTokenTransformer;
	private final CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry;

	/**
	 * Default constructor.
	 *
	 * @param paymentTokenTransformer the {@link PaymentTokenTransformer}
	 * @param createPaymentTokenHandlerRegistry the {@link CreatePaymentTokenHandlerRegistry}
	 */
	@Inject
	public PaymentTokenWriterStrategyImpl(
			@Named("paymentTokenTransformer")
			final PaymentTokenTransformer paymentTokenTransformer,
			@Named("createPaymentTokenHandlerRegistry")
			final CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry) {
		this.paymentTokenTransformer = paymentTokenTransformer;
		this.createPaymentTokenHandlerRegistry = createPaymentTokenHandlerRegistry;
	}

	@Override
	public ExecutionResult<PaymentTokenEntity> createPaymentTokenForOwner(final PaymentTokenEntity paymentTokenEntity,
																			final String decodedOwnerId,
																			final PaymentTokenOwnerType ownerType,
																			final String scope) {

		Ensure.notNull(paymentTokenEntity, OnFailure.returnServerError("payment token can not be null"));
		Ensure.notNull(decodedOwnerId, OnFailure.returnServerError("decoded owner id can not be null"));

		PaymentToken paymentToken = paymentTokenTransformer.transformToDomain(paymentTokenEntity);

		CreatePaymentTokenHandler createPaymentTokenHandler = createPaymentTokenHandlerRegistry.lookupHandler(ownerType);
		return createPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, decodedOwnerId, scope);
	}
}
