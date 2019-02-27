/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.example.wiring;

import javax.inject.Named;

import com.google.inject.multibindings.MapBinder;

import com.elasticpath.extensions.rest.resource.example.permissions.ExampleIdParameterStrategy;
import com.elasticpath.rest.authorization.parameter.PermissionParameterStrategy;
import com.elasticpath.rest.definition.example.ExampleIdentifier;
import com.elasticpath.rest.definition.example.ExampleResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Service wiring.
 */
@Named
public class ServiceWiring extends AbstractHelixModule {
	@Override
	protected void configurePrototypes() {
		//bind(ExampleRepository.class).toProvider(service(ExampleRepository.class).single());
	}

	@Override
	protected String resourceName() {
		return ExampleResource.FAMILY;
	}

	@Override
	public void registerParameterResolvers(final MapBinder<String, PermissionParameterStrategy> resolvers) {
		super.registerParameterResolvers(resolvers);
		resolvers.addBinding(ExampleIdentifier.EXAMPLE_ID).toInstance(new ExampleIdParameterStrategy());
	}
}
