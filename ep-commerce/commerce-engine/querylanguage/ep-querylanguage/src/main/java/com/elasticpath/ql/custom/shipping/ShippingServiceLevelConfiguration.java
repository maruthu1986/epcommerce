/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.shipping;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EPQL fields and field descriptors for ShippingServiceLevel.
 */
public class ShippingServiceLevelConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT ssl.guid FROM ShippingServiceLevelImpl ssl");
		addSortField("ssl.guid", EpQLSortOrder.ASC);
	}
}
