/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.uri.impl;

import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the specific address.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class AddressUriBuilderImpl implements AddressUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "addresses";

	private String scope;
	private String addressId;

	@Override
	public AddressUriBuilderImpl setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert addressId != null : "addressId required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, addressId);
	}

	@Override
	public AddressUriBuilderImpl setAddressId(final String addressId) {
		this.addressId = addressId;
		return this;
	}
}
