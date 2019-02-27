/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemRelationship;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Wiring for price resource.
 */
@Named
public class PriceWiring extends AbstractHelixModule {
	@Override
	protected String resourceName() {
		return PriceForShipmentLineItemRelationship.FAMILY;
	}
}
