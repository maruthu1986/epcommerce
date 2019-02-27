/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.tax;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EqQL fields and field descriptors for TaxCode.
 */
public class TaxCodeConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT tc.guid FROM TaxCodeImpl tc");
		addSortField("tc.guid", EpQLSortOrder.ASC);
	}
}
