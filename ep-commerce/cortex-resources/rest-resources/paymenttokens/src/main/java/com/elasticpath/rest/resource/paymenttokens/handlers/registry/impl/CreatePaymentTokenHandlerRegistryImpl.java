/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers.registry.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.rest.resource.paymenttokens.handlers.CreatePaymentTokenHandler;
import com.elasticpath.rest.resource.paymenttokens.handlers.registry.CreatePaymentTokenHandlerRegistry;

/**
 * Implementation of {@link CreatePaymentTokenHandlerRegistry}.
 */
@SuppressWarnings("rawtypes")
public class CreatePaymentTokenHandlerRegistryImpl implements CreatePaymentTokenHandlerRegistry {
	private final List<CreatePaymentTokenHandler> createPaymentTokenHandlers;

	/**
	 * Creates a new {@link CreatePaymentTokenHandler} registry.
	 *
	 * @param createPaymentTokenHandlers a {@link List} of {@link CreatePaymentTokenHandler}s
	 */
	public CreatePaymentTokenHandlerRegistryImpl(
			final List<CreatePaymentTokenHandler> createPaymentTokenHandlers) {
		this.createPaymentTokenHandlers = createPaymentTokenHandlers;
	}

	@Override
	public CreatePaymentTokenHandler lookupHandler(final String ownerRepresentationType) {
		CreatePaymentTokenHandler handler = null;
		for (CreatePaymentTokenHandler createPaymentTokenHandler : createPaymentTokenHandlers) {
			if (createPaymentTokenHandler.handledOwnerRepresentationType().equals(ownerRepresentationType)) {
				handler = createPaymentTokenHandler;
				break;
			}
		}

		if (handler == null) {
			throw new IllegalStateException(String.format("No handler configured for representation class: %s", ownerRepresentationType));
		}

		return handler;
	}

	@Override
	public Set<String> getHandledOwnerRepresentationTypes() {
		HashSet<String> handledTypes = new HashSet<>(createPaymentTokenHandlers.size());
		for (CreatePaymentTokenHandler createPaymentTokenHandler : createPaymentTokenHandlers) {
			handledTypes.add(createPaymentTokenHandler.handledOwnerRepresentationType());
		}
		return handledTypes;
	}
}
