/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory}.
 */
@Singleton
@Named("paymentTokenFormUriBuilderFactory")
public class PaymentTokenFormUriBuilderFactoryImpl implements PaymentTokenFormUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server provider
	 */
	@Inject
	PaymentTokenFormUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PaymentTokenFormUriBuilder get() {
		return new PaymentTokenFormUriBuilderImpl(resourceServerName);
	}
}
