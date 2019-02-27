/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.availabilities.AvailabilityForItemResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Peaberry plumbing class used for importing required services and registering additional parameter resolvers.
 */
@Named
public class AvailabilitiesWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return AvailabilityForItemResource.FAMILY;
	}
}
