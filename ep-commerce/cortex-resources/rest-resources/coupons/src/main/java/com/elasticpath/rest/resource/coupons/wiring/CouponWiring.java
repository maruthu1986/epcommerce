/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.coupons.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.coupons.CouponinfoResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Coupon Wiring.
 */
@Named
public class CouponWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return CouponinfoResource.FAMILY;
	}
}
