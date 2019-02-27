/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.registry.impl;


import java.util.List;

import com.elasticpath.rest.integrations.payment.tokens.handler.CreatePaymentTokenHandler;
import com.elasticpath.rest.integrations.payment.tokens.handler.registry.CreatePaymentTokenHandlerRegistry;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Implementation of {@link CreatePaymentTokenHandlerRegistry}.
 */
public class CreatePaymentTokenHandlerRegistryImpl implements CreatePaymentTokenHandlerRegistry {
	private final List<CreatePaymentTokenHandler> createPaymentTokenHandlers;

	/**
	 * Creates a new {@link CreatePaymentTokenHandlerRegistry}.
	 *
	 * @param createPaymentTokenHandlers a list of {@link CreatePaymentTokenHandler}s
	 */
	public CreatePaymentTokenHandlerRegistryImpl(
			final List<CreatePaymentTokenHandler> createPaymentTokenHandlers) {
		this.createPaymentTokenHandlers = createPaymentTokenHandlers;
	}

	@Override
	public CreatePaymentTokenHandler lookupHandler(final PaymentTokenOwnerType ownerType) {
		CreatePaymentTokenHandler handler = null;
		for (CreatePaymentTokenHandler createPaymentTokenHandler : createPaymentTokenHandlers) {
			if (createPaymentTokenHandler.getHandledOwnerType().equals(ownerType)) {
				handler = createPaymentTokenHandler;
				break;
			}
		}

		if (handler == null) {
			throw new IllegalStateException(String.format("No handler configured for owner type: %s", ownerType));
		}

		return handler;
	}
}
