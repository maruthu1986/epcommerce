/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.uri.impl;

import com.elasticpath.rest.schema.uri.PurchaseListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates purchase list Uri.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class PurchaseListUriBuilderImpl implements PurchaseListUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "purchases";

	private String scope;

	@Override
	public PurchaseListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope);
	}
}
