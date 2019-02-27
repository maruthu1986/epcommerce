/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;

/**
 * Implements {@link PaymentMethodInfoUriBuilderFactory}.
 */
@Named("paymentMethodInfoUriBuilderFactory")
public class PaymentMethodInfoUriBuilderFactoryImpl implements PaymentMethodInfoUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	PaymentMethodInfoUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PaymentMethodInfoUriBuilder get() {
		return new PaymentMethodInfoUriBuilderImpl(resourceServerName);
	}
}
