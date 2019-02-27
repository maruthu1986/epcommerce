/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Peaberry plumbing class used for importing required services and registering additional parameter resolvers.
 */
@Named
public class PromotionsWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return PurchaseCouponPromotionResource.FAMILY;
	}
}
