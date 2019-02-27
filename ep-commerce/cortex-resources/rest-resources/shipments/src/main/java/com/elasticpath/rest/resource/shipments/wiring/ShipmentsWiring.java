/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.shipments.ShipmentResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Wiring for shipments.
 */
@Named
public class ShipmentsWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return ShipmentResource.FAMILY;
	}
}
