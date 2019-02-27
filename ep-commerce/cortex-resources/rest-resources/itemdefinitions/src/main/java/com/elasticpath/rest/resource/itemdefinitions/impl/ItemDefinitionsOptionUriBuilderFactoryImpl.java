/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilderFactory;

/**
 * Factory for {@link ItemDefinitionsOptionUriBuilder}.
 */
@Singleton
@Named("itemDefinitionsOptionUriBuilderFactory")
public final class ItemDefinitionsOptionUriBuilderFactoryImpl implements ItemDefinitionsOptionUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Construct an ItemsUriBuilderFactory.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	ItemDefinitionsOptionUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ItemDefinitionsOptionUriBuilder get() {
		return new ItemDefinitionsOptionUriBuilderImpl(resourceServerName);
	}
}
