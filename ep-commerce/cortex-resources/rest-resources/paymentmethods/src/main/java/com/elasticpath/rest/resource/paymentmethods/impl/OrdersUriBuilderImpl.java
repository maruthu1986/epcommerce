/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates order URIs.
 */
@Deprecated
public final class OrdersUriBuilderImpl implements OrdersUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "orders";

	private String scope;
	private String orderId;

	@Override
	public OrdersUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public OrdersUriBuilder setOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert orderId != null : "orderId required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, orderId);
	}
}
