/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.ItemLookupUriBuilder;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;

/**
 * A factory for creating ItemLookupUriBuilder objects.
 */
@Singleton
@Named("itemLookupUriBuilderFactory")
public final class ItemLookupUriBuilderFactoryImpl implements ItemLookupUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	ItemLookupUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public ItemLookupUriBuilder get() {
		return new ItemLookupUriBuilderImpl(resourceServerName);
	}

}
