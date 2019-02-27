/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import com.elasticpath.rest.schema.uri.PromotionsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * URI Builder for promotions resource.
 */
public final class PromotionsUriBuilderImpl implements PromotionsUriBuilder {

	private final String resourceServerName;

	private String promotionId;
	private String scope;
	private String sourceUri;
	private String promotionType;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resourceServerName.
	 */
	public PromotionsUriBuilderImpl(
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PromotionsUriBuilder setPromotionId(final String promotionId) {
		this.promotionId = promotionId;
		return this;
	}

	@Override
	public PromotionsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public PromotionsUriBuilder setSourceUri(final String sourceUri) {
		this.sourceUri = sourceUri;
		return this;
	}

	@Override
	public PromotionsUriBuilder setPromotionType(final String promotionType) {
		this.promotionType = promotionType;
		return this;
	}

	@Override
	public String build() {
		assert !(promotionId != null && sourceUri == null && scope == null) : "Promotion id should be set with a source uri or a scope.";
		assert !(promotionType != null && sourceUri == null) : "sourceUri should be set if promotionType is set.";
		assert !(sourceUri != null && scope != null) : "scope not required if sourceURI set.";

		return URIUtil.format(resourceServerName, sourceUri, scope, promotionId, promotionType);
	}
}
