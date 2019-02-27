/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;
import com.elasticpath.ql.parser.FetchType;

/**
 * Holds mapping between EpQL fields and field descriptors for dynamic content delivery.
 */
public class DynamicContentDeliveryConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("select dcd.guid FROM DynamicContentDeliveryImpl dcd");
		addSortField("dcd.guid", EpQLSortOrder.ASC);
		setFetchType(FetchType.GUID);
	}
	
}
