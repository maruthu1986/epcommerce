/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.uri.impl;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI pointing to the address form.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class AddressFormUriBuilderImpl implements AddressFormUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "addresses";

	private String scope;

	@Override
	public AddressFormUriBuilderImpl setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, Form.URI_PART);
	}
}
