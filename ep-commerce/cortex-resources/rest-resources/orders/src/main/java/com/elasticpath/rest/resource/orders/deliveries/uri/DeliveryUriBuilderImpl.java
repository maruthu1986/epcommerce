/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
/*
 *
 */
package com.elasticpath.rest.resource.orders.deliveries.uri;

import com.elasticpath.rest.schema.uri.DeliveryUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds the URI of an order's delivery.
 */
@Deprecated
public final class DeliveryUriBuilderImpl implements DeliveryUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "orders";
	private static final String URI_PART = "deliveries";

	private String scope;
	private String orderId;
	private String deliveryId;


	@Override
	public DeliveryUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public DeliveryUriBuilder setOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	@Override
	public DeliveryUriBuilder setDeliveryId(final String deliveryId) {
		this.deliveryId = deliveryId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required";
		assert orderId != null : "orderId required";
		assert deliveryId != null : "deliveryId required";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, orderId, URI_PART, deliveryId);
	}
}
