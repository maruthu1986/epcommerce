/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilderFactory;

/**
 * Factory for {@link ItemDefinitionsOptionValueUriBuilder}.
 */
@Singleton
@Named("itemDefinitionsOptionValueUriBuilderFactory")
public final class ItemDefinitionsOptionValueUriBuilderFactoryImpl implements ItemDefinitionsOptionValueUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Construct an {@link ItemDefinitionsOptionValueUriBuilderFactory}.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	ItemDefinitionsOptionValueUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ItemDefinitionsOptionValueUriBuilder get() {
		return new ItemDefinitionsOptionValueUriBuilderImpl(resourceServerName);
	}
}
