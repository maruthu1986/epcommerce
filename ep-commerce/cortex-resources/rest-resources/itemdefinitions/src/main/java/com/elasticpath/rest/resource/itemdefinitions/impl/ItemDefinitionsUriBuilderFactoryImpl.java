/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder}s.
 */
@Singleton
@Named("itemDefinitionsUriBuilderFactory")
public final class ItemDefinitionsUriBuilderFactoryImpl implements ItemDefinitionsUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	ItemDefinitionsUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ItemDefinitionsUriBuilder get() {
		return new ItemDefinitionsUriBuilderImpl(resourceServerName);
	}
}
