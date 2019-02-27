/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.commons.handler.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Registry that permits registration and lookup of {@link PaymentHandler}s.
 */
public final class PaymentHandlerRegistryImpl implements PaymentHandlerRegistry {
	private final List<PaymentHandler> paymentTransformerStrategies;

	/**
	 * Creates a new {@link PaymentHandler} registry.
	 *
	 * @param paymentTransformerStrategies an {@link List} of {@link PaymentHandler}s
	 */
	public PaymentHandlerRegistryImpl(
			final List<PaymentHandler> paymentTransformerStrategies) {
		this.paymentTransformerStrategies = paymentTransformerStrategies;
	}

	@Override
	public PaymentHandler lookupHandler(final ResourceEntity paymentEntity) {
		PaymentHandler handler = null;
		for (PaymentHandler paymentHandler : paymentTransformerStrategies) {
			if (paymentHandler.handledType().isAssignableFrom(paymentEntity.getClass())) {
				handler = paymentHandler;
				break;
			}
		}

		if (handler == null) {
			throw new IllegalStateException(String.format("No handler configured for type: %s", paymentEntity.getClass()));
		}

		return handler;
	}

	@Override
	public Set<String> getHandledPaymentRepresentationTypes() {
		HashSet<String> handledTypes = new HashSet<>(paymentTransformerStrategies.size());
		for (PaymentHandler paymentHandler : paymentTransformerStrategies) {
			handledTypes.add(paymentHandler.representationType());
		}
		return handledTypes;
	}
}