/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.uri.impl;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.AddressUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = AddressUriBuilderFactory.class)
public final class AddressUriBuilderFactoryImpl implements AddressUriBuilderFactory {

	@Override
	public AddressUriBuilder get() {
		return new AddressUriBuilderImpl();
	}
}
