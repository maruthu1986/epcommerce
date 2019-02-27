/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds an URI for item definitions option value.
 */
public final class ItemDefinitionsOptionValueUriBuilderImpl implements ItemDefinitionsOptionValueUriBuilder {

	private String scope;
	private String itemId;
	private String optionId;
	private String valueId;

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	public ItemDefinitionsOptionValueUriBuilderImpl(
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ItemDefinitionsOptionValueUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public ItemDefinitionsOptionValueUriBuilder setItemId(final String itemId) {
		this.itemId = itemId;
		return this;
	}

	@Override
	public ItemDefinitionsOptionValueUriBuilder setOptionId(final String optionId) {
		this.optionId = optionId;
		return this;
	}

	@Override
	public ItemDefinitionsOptionValueUriBuilder setValueId(final String valueId) {
		this.valueId = valueId;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, scope, itemId, Options.URI_PART, optionId, Values.URI_PART, valueId);
	}
}
