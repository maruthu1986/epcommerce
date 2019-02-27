/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;

/**
 * Implementation of {@link OrderPaymentMethodUriBuilderFactory}.
 */
@Singleton
@Named("orderPaymentMethodUriBuilderFactory")
public class OrderPaymentMethodUriBuilderFactoryImpl implements OrderPaymentMethodUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	OrderPaymentMethodUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public OrderPaymentMethodUriBuilder get() {
		return new OrderPaymentMethodUriBuilderImpl(resourceServerName);
	}
}
