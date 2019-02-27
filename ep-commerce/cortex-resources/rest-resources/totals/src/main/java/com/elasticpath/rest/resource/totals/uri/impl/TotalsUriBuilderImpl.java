/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.uri.impl;

import com.elasticpath.rest.schema.uri.TotalsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * TotalsUriBuilderImpl.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class TotalsUriBuilderImpl implements TotalsUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "totals";

	private String sourceUri;

	@Override
	public TotalsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public String build() {
		assert sourceUri != null : "sourceUri required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, sourceUri);
	}
}
