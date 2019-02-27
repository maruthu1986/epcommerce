/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilderFactory;

/**
 * Factory for {@link PaymentMethodListUriBuilder}.
 */
@Singleton
@Named("paymentMethodListUriBuilderFactory")
public final class PaymentMethodListUriBuilderFactoryImpl implements PaymentMethodListUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	PaymentMethodListUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PaymentMethodListUriBuilder get() {
		return new PaymentMethodListUriBuilderImpl(resourceServerName);
	}
}
