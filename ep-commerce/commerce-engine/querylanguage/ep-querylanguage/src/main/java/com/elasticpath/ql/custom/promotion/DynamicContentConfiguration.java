/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;
import com.elasticpath.ql.parser.FetchType;

/**
 * Holds mapping between EpQL fields and field descriptors for dynamic content.
 */
public class DynamicContentConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("select dc.guid FROM DynamicContentImpl dc");
		addSortField("dc.guid", EpQLSortOrder.ASC);
		setFetchType(FetchType.GUID);
	}
	
}
