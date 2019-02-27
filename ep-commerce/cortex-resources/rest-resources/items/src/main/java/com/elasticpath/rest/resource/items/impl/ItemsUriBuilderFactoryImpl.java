/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Factory for {@link ItemsUriBuilder}.
 */
@Singleton
@Named("itemsUriBuilderFactory")
public final class ItemsUriBuilderFactoryImpl implements ItemsUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Construct an ItemsUriBuilderFactory.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	ItemsUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ItemsUriBuilder get() {
		return new ItemsUriBuilderImpl(resourceServerName);
	}
}
