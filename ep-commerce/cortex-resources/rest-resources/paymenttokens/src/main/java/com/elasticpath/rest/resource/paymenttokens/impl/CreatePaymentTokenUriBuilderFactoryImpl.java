/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilder;
import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilderFactory;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilderFactory}.
 */
@Singleton
@Named("createPaymentTokenUriBuilderFactory")
public class CreatePaymentTokenUriBuilderFactoryImpl implements CreatePaymentTokenUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	CreatePaymentTokenUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public CreatePaymentTokenUriBuilder get() {
		return new CreatePaymentTokenUriBuilderImpl(resourceServerName);
	}
}
