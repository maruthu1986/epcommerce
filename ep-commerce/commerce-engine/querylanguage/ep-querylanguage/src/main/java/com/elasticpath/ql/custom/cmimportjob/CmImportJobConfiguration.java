/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.cmimportjob;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EPQL fields and field descriptors for Import Jobs.
 */
public class CmImportJobConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("SELECT im.guid FROM ImportJobImpl im");
		addSortField("im.guid", EpQLSortOrder.ASC);
	}
}
