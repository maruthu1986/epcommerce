/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.uri;

import javax.inject.Provider;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.DeliveryUriBuilder;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilderFactory;

/**
 * A factory for creating DeliveryListUriBuilder objects.
 */
@Deprecated
@Component(service = DeliveryUriBuilderFactory.class)
public final class DeliveryUriBuilderFactoryImpl implements Provider<DeliveryUriBuilder>, DeliveryUriBuilderFactory {

	@Override
	public DeliveryUriBuilder get() {
		return new DeliveryUriBuilderImpl();
	}
}
