/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Builds the URI pointing to a single payment method.
 */
public final class PaymentMethodUriBuilderImpl implements PaymentMethodUriBuilder {

	private final String resourceServerName;

	private String scope;
	private String paymentMethodId;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	PaymentMethodUriBuilderImpl(
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public PaymentMethodUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public PaymentMethodUriBuilder setPaymentMethodId(final String paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert paymentMethodId != null : "paymentMethodId required.";
		return URIUtil.format(resourceServerName, scope, paymentMethodId);
	}
}
