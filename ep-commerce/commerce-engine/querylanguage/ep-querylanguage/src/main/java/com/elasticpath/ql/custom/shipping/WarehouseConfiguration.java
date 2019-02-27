/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.shipping;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EPQL fields and field descriptors for Warehouse.
 */
public class WarehouseConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT w.code FROM WarehouseImpl w");
		addSortField("w.code", EpQLSortOrder.ASC);
	}
}
