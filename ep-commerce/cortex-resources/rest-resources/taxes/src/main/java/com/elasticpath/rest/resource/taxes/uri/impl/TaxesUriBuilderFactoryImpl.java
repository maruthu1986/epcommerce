/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.uri.impl;

import javax.inject.Provider;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.TaxesUriBuilder;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;

/**
 * Default implementation of {@link TaxesUriBuilderFactory}.
 */
@Component(service = TaxesUriBuilderFactory.class)
public class TaxesUriBuilderFactoryImpl implements Provider<TaxesUriBuilder>, TaxesUriBuilderFactory {

	@Override
	public TaxesUriBuilder get() {
		return new TaxesUriBuilderImpl();
	}

}
