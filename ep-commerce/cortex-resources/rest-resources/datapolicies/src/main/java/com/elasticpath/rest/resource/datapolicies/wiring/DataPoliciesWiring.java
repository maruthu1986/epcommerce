/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.datapolicies.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.datapolicies.DataPoliciesResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Peaberry plumbing class used for importing required services and registering additional parameter resolvers.
 */
@Named
public class DataPoliciesWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return DataPoliciesResource.FAMILY;
	}
}