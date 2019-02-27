/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.uri.impl;

import com.elasticpath.rest.resource.addresses.billing.uri.Billing;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the billing address list.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class BillingAddressListUriBuilderImpl implements BillingAddressListUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "addresses";

	private String scope;

	@Override
	public BillingAddressListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, Billing.URI_PART);
	}
}
