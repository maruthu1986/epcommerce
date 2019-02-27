/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.uri.impl;

import javax.inject.Provider;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.CouponsUriBuilder;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Factory for creating CouponsUriBuilder.
 */
@Deprecated
@Component(service = CouponsUriBuilderFactory.class)
public final class CouponsUriBuilderFactoryImpl implements Provider<CouponsUriBuilder>, CouponsUriBuilderFactory {

	@Override
	public CouponsUriBuilder get() {
		return new CouponsUriBuilderImpl();
	}
}
