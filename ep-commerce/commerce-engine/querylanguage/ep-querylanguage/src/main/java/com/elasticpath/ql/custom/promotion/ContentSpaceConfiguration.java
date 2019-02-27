/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EqQL fields and field descriptors for ContentSpace.
 */
public class ContentSpaceConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT cs.guid FROM ContentSpaceImpl cs");
		addSortField("cs.guid", EpQLSortOrder.ASC);
	}
}
