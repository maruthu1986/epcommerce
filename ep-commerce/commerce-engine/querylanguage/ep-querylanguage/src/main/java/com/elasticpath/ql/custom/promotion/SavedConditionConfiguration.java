/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EPQL fields and field descriptors for Conditional expression.
 */
public class SavedConditionConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("SELECT tc.guid FROM ConditionalExpressionImpl tc WHERE tc.named = true");
		addSortField("tc.guid", EpQLSortOrder.ASC);
	}
}
