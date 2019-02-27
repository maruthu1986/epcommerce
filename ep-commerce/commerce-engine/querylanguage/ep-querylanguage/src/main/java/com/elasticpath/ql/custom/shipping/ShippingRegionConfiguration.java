/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.shipping;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EPQL fields and field descriptors for ShippingRegion.
 */
public class ShippingRegionConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT sr.name FROM ShippingRegionImpl sr");
		addSortField("sr.name", EpQLSortOrder.ASC);
	}
}
