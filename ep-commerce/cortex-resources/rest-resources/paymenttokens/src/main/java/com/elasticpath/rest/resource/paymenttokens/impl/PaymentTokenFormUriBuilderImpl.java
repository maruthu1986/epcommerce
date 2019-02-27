/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder}.
 */
public class PaymentTokenFormUriBuilderImpl implements PaymentTokenFormUriBuilder {
	private final String resourceServerName;
	private String ownerUri;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	public PaymentTokenFormUriBuilderImpl(
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PaymentTokenFormUriBuilder setSourceUri(final String ownerUri) {
		this.ownerUri = ownerUri;
		return this;
	}

	@Override
	public String build() {
		assert ownerUri != null : "The owner URI required.";
		return URIUtil.format(resourceServerName, ownerUri, Form.URI_PART);
	}
}
