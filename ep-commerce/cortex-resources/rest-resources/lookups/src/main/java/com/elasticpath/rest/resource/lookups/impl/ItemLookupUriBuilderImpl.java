/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.lookups.Items;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of the {@link ItemLookupUriBuilder}.
 */
public final class ItemLookupUriBuilderImpl implements ItemLookupUriBuilder {

	private String sourceUri;
	private String formPart;
	private String itemsPart;
	private String scope;
	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	public ItemLookupUriBuilderImpl(
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}


	@Override
	public ItemLookupUriBuilder setFormPart() {
		this.sourceUri = null;
		this.itemsPart = Items.PATH_PART;
		this.formPart = Form.PATH_PART;
		return this;
	}

	@Override
	public ItemLookupUriBuilder setItemsPart() {
		this.sourceUri = null;
		this.itemsPart = Items.PATH_PART;
		return this;
	}

	@Override
	public ItemLookupUriBuilder setScope(final String scope) {
		this.sourceUri = null;
		this.scope = scope;
		return this;

	}

	@Override
	public ItemLookupUriBuilder setSourceUri(final String sourceUri) {
		this.formPart = null;
		this.itemsPart = null;
		this.scope = null;
		this.sourceUri = sourceUri;
		return this;
	}


	@Override
	public String build() {
		assert !(scope == null && (formPart != null || itemsPart != null)) : "scope must be set if formPart or itemPart set.";
		return URIUtil.format(resourceServerName, scope, itemsPart, formPart, sourceUri);
	}
}
