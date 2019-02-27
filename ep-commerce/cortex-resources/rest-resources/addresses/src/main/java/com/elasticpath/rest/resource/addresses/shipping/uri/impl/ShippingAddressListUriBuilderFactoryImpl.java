/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.uri.impl;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = ShippingAddressListUriBuilderFactory.class)
public final class ShippingAddressListUriBuilderFactoryImpl implements ShippingAddressListUriBuilderFactory {

	@Override
	public ShippingAddressListUriBuilder get() {
		return new ShippingAddressListUriBuilderImpl();
	}
}
