/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.recommendations.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.recommendations.ItemRecommendationGroupResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Navigations wiring.
 */
@Named
public class RecommendationWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return ItemRecommendationGroupResource.FAMILY;
	}
}