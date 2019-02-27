/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.uri.impl;

import com.elasticpath.rest.resource.addresses.shipping.uri.Shipping;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the Shipping addresses list resource.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class ShippingAddressListUriBuilderImpl implements ShippingAddressListUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "addresses";

	private String scope;

	@Override
	public ShippingAddressListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, Shipping.URI_PART);
	}
}
