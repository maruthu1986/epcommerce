/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.uri.impl;

import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Default implementation of {@link PurchaseUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class PurchaseUriBuilderImpl implements PurchaseUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "purchases";

	private String scope;
	private String purchaseId;

	@Override
	public PurchaseUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public PurchaseUriBuilder setPurchaseId(final String encodedPurchaseId) {
		this.purchaseId = encodedPurchaseId;
		return this;
	}

	@Override
	public PurchaseUriBuilder setDecodedPurchaseId(final String decodedPurchaseId) {
		if (decodedPurchaseId != null) {
			this.purchaseId = Base32Util.encode(decodedPurchaseId);
		}
		return this;
	}
	
	@Override
	public String build() {
		assert scope != null;
		assert purchaseId != null;
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, purchaseId);
	}

}
