/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.uri.impl;

import com.elasticpath.rest.schema.uri.TaxesUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Default implementation of {@link TaxesUriBuilder}.
 */
public final class TaxesUriBuilderImpl implements TaxesUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "taxes";
	private String sourceUri;


	@Override
	public TaxesUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceURI is required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, sourceUri);
	}
}
