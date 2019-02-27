/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;

/**
 * Factory for {@link PaymentMethodUriBuilder}.
 */
@Singleton
@Named("paymentMethodUriBuilderFactory")
public final class PaymentMethodUriBuilderFactoryImpl implements PaymentMethodUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	PaymentMethodUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PaymentMethodUriBuilder get() {
		return new PaymentMethodUriBuilderImpl(resourceServerName);
	}
}
