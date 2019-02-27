/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.uri.impl;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = BillingAddressListUriBuilderFactory.class)
public final class BillingAddressListUriBuilderFactoryImpl implements BillingAddressListUriBuilderFactory {

	@Override
	public BillingAddressListUriBuilder get() {
		return new BillingAddressListUriBuilderImpl();
	}
}
