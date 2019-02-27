/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds an URI for item definitions option.
 */
public final class ItemDefinitionsOptionUriBuilderImpl implements ItemDefinitionsOptionUriBuilder {

	private String scope;
	private String itemId;
	private String optionId;

	private final String resourceServerName;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	public ItemDefinitionsOptionUriBuilderImpl(final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}


	@Override
	public ItemDefinitionsOptionUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public ItemDefinitionsOptionUriBuilder setItemId(final String itemId) {
		this.itemId = itemId;
		return this;
	}

	@Override
	public ItemDefinitionsOptionUriBuilder setOptionId(final String optionId) {
		this.optionId = optionId;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, scope, itemId, Options.URI_PART, optionId);
	}
}
