/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.prototype;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.PromotionsUriBuilder;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Factory for creating PromotionsUriBuilder.
 */
@Singleton
@Named("promotionsUriBuilderFactory")
public final class PromotionsUriBuilderFactoryImpl implements PromotionsUriBuilderFactory {

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public PromotionsUriBuilderFactoryImpl(
			@Named("resourceServerName") final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public PromotionsUriBuilder get() {
		return new PromotionsUriBuilderImpl(resourceServerName);
	}
}
