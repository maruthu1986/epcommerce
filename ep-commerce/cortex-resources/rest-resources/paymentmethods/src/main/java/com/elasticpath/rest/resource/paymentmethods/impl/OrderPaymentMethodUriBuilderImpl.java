/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link OrderPaymentMethodUriBuilder}.
 */
public class OrderPaymentMethodUriBuilderImpl implements OrderPaymentMethodUriBuilder {

	private final String resourceServerName;

	private String orderId;
	private String scope;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	public OrderPaymentMethodUriBuilderImpl(
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public OrderPaymentMethodUriBuilder setOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	@Override
	public OrderPaymentMethodUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert orderId != null : "orderId required.";

		String orderUri = new OrdersUriBuilderImpl()
				.setScope(scope)
				.setOrderId(orderId)
				.build();

		return URIUtil.format(resourceServerName, orderUri);
	}
}
