/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.taxes.wiring;

import javax.inject.Named;

import com.elasticpath.rest.definition.taxes.TaxesForShipmentRelationship;
import com.elasticpath.rest.helix.api.AbstractHelixModule;

/**
 * Taxes Wiring.
 */
@Named
public class TaxesWiring extends AbstractHelixModule {

	@Override
	protected String resourceName() {
		return TaxesForShipmentRelationship.FAMILY;
	}
}
