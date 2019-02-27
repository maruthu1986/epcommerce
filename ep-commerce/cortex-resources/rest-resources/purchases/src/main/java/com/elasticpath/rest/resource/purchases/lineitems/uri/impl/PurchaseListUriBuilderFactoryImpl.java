/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.uri.impl;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.PurchaseListUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseListUriBuilderFactory;

/**
 * Factory for {@link PurchaseListUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = PurchaseListUriBuilderFactory.class)
public final class PurchaseListUriBuilderFactoryImpl implements PurchaseListUriBuilderFactory {

	@Override
	public PurchaseListUriBuilder get() {
		return new PurchaseListUriBuilderImpl();
	}
}
