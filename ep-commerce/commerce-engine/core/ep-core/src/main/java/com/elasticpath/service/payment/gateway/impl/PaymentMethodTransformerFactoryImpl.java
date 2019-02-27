/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.payment.gateway.impl;

import java.util.Map;

import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformerFactory;

/**
 * Default implementation of {@link PaymentMethodTransformerFactory}.
 * Uses constructor injection.
 */
public class PaymentMethodTransformerFactoryImpl implements PaymentMethodTransformerFactory {

	private final Map<Class<? extends PaymentMethod>, PaymentMethodTransformer> transformerMap;
	
	/**
	 * Instantiates a {@link PaymentMethodTransformerFactory}.
	 *
	 * @param transformerMap the map between {@link PaymentMethod} classes and transformers.
	 */
	public PaymentMethodTransformerFactoryImpl(final Map<Class<? extends PaymentMethod>, PaymentMethodTransformer> transformerMap) {
		this.transformerMap = transformerMap;
	}
	
	@Override
	public PaymentMethodTransformer getTransformerInstance(final PaymentMethod paymentMethod) {
		for (final Map.Entry<Class<? extends PaymentMethod>, PaymentMethodTransformer> paymentMethodEntry : transformerMap.entrySet()) {
			if (paymentMethodEntry.getKey().isAssignableFrom(paymentMethod.getClass())) {
				return paymentMethodEntry.getValue();
			}
		}
		
		throw new IllegalArgumentException("No transformer found for " + paymentMethod.getClass());
	}
}
