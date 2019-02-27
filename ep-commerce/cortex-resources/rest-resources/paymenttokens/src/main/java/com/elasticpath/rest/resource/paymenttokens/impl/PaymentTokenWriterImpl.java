/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.paymenttokens.PaymentTokenWriter;
import com.elasticpath.rest.resource.paymenttokens.handlers.CreatePaymentTokenHandler;
import com.elasticpath.rest.resource.paymenttokens.handlers.registry.CreatePaymentTokenHandlerRegistry;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Implementation of the {@link PaymentTokenWriter}.
 */
@Singleton
@Named("paymentTokenWriter")
public class PaymentTokenWriterImpl implements PaymentTokenWriter {

	private final CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry;

	/**
	 * Constructor.
	 * @param createPaymentTokenHandlerRegistry   the {@link CreatePaymentTokenHandlerRegistry}
	 */
	@Inject
	PaymentTokenWriterImpl(
			@Named("createPaymentTokenHandlerRegistry")
			final CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry) {
		this.createPaymentTokenHandlerRegistry = createPaymentTokenHandlerRegistry;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public ExecutionResult<ResourceState<ResourceEntity>> createPaymentToken(final PaymentTokenEntity paymentTokenEntity,
			final ResourceState<?> owningRepresentation) {

		CreatePaymentTokenHandler createPaymentTokenHandler = createPaymentTokenHandlerRegistry.lookupHandler(
				ResourceStateUtil.getSelfType(owningRepresentation));

		ExecutionResult<PaymentTokenEntity> createResult = createPaymentTokenHandler.createPaymentToken(paymentTokenEntity,
				owningRepresentation);

		Ensure.successful(createResult);

		String createdPaymentMethodUri = createPaymentTokenHandler.createPaymentTokenUri(createResult.getData(), owningRepresentation);

		return ExecutionResultFactory.createCreateOK(createdPaymentMethodUri, isNotNewPaymentToken(createResult));
	}

	private boolean isNotNewPaymentToken(final ExecutionResult<PaymentTokenEntity> createResult) {
		return ResourceStatus.READ_OK.equals(createResult.getResourceStatus());
	}
}
