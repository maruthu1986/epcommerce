/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.navigations.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.navigations.NavigationsResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Navigations wiring.
 */
@Named
public class NavigationsWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return NavigationsResource.FAMILY;
	}
}
