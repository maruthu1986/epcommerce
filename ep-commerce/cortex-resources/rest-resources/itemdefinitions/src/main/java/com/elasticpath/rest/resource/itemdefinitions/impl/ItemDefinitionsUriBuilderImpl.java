/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Builds a URI for an item definition.
 */
public final class ItemDefinitionsUriBuilderImpl implements ItemDefinitionsUriBuilder {

	private String scope;
	private String itemId;

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	public ItemDefinitionsUriBuilderImpl(final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}


	@Override
	public ItemDefinitionsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public ItemDefinitionsUriBuilder setItemId(final String itemId) {
		this.itemId = itemId;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, scope, itemId);
	}
}
