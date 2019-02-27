/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.wiring;

import static org.ops4j.peaberry.Peaberry.service;

import javax.inject.Named;

import com.google.inject.multibindings.MapBinder;

import com.elasticpath.rest.authorization.parameter.PermissionParameterStrategy;
import com.elasticpath.rest.definition.shipmentdetails.ShipmentDetailsIdIdentifierPart;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsIdParameterService;
import com.elasticpath.rest.resource.shipmentdetails.permission.ShipmentDetailsIdParameterStrategy;

/**
 * Wiring for shipmentdetails resource.
 */
@Named
public class ShipmentDetailsWiring extends AbstractHelixModule {
	@Override
	protected String resourceName() {
		return ShippingOptionResource.FAMILY;
	}

	@Override
	protected void registerParameterResolvers(final MapBinder<String, PermissionParameterStrategy> resolvers) {
		super.registerParameterResolvers(resolvers);
		resolvers.addBinding(ShipmentDetailsIdIdentifierPart.URI_PART_NAME).toInstance(new ShipmentDetailsIdParameterStrategy());
	}

	@Override
	protected void configurePrototypes() {
		bind(ShipmentDetailsIdParameterService.class).toProvider(service(ShipmentDetailsIdParameterService.class).single());
	}
}
