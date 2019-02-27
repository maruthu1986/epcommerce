/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Builds the URI pointing to the payment method list.
 */
public final class PaymentMethodListUriBuilderImpl implements PaymentMethodListUriBuilder {

	private final String resourceServerName;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	PaymentMethodListUriBuilderImpl(
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public PaymentMethodListUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(resourceServerName, scope);
	}
}
