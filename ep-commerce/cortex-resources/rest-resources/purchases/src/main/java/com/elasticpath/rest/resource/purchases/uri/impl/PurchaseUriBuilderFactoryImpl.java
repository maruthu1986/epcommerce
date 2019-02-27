/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.uri.impl;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Default implementation of {@link PurchaseUriBuilderFactory}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = PurchaseUriBuilderFactory.class)
public final class PurchaseUriBuilderFactoryImpl implements PurchaseUriBuilderFactory {

	@Override
	public PurchaseUriBuilder get() {
		return new PurchaseUriBuilderImpl();
	}
}
