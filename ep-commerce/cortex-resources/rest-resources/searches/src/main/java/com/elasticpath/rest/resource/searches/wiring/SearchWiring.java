/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.searches.SearchesResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Peaberry plumbing class used for importing required services and registering additional parameter resolvers.
 */
@Named
public class SearchWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return SearchesResource.FAMILY;
	}

}
