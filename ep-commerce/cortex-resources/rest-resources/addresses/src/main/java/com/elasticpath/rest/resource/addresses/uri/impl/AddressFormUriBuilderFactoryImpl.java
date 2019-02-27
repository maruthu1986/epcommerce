/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.uri.impl;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.AddressFormUriBuilder;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.AddressFormUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = AddressFormUriBuilderFactory.class)
public final class AddressFormUriBuilderFactoryImpl implements AddressFormUriBuilderFactory {

	@Override
	public AddressFormUriBuilder get() {
		return new AddressFormUriBuilderImpl();
	}
}
