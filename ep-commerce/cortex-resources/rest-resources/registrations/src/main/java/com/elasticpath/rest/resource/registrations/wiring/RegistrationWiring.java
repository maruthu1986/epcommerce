/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.registrations.wiring;


import javax.inject.Named;

import com.google.inject.multibindings.MapBinder;

import com.elasticpath.rest.authorization.parameter.PermissionParameterStrategy;
import com.elasticpath.rest.authorization.parameter.UnauthenticatedParameterStrategy;
import com.elasticpath.rest.definition.registrations.NewAccountRegistrationFormResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Peaberry plumbing class used for importing required services.
 */
@Named
public class RegistrationWiring extends AbstractHelixModule {

	private static final String UNAUTHENTICATED = "unauthenticated";

	@Override
	protected String resourceName() {
		return NewAccountRegistrationFormResource.FAMILY;
	}

	@Override
	protected void registerParameterResolvers(final MapBinder<String, PermissionParameterStrategy> resolvers) {
		super.registerParameterResolvers(resolvers);
		resolvers.addBinding(UNAUTHENTICATED).toInstance(new UnauthenticatedParameterStrategy());
	}
}
