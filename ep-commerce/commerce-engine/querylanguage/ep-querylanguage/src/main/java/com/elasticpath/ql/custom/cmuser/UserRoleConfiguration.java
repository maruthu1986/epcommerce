/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.cmuser;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EPQL fields and field descriptors for UserRole.
 */
public class UserRoleConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT u.guid FROM UserRoleImpl u");
		addSortField("u.guid", EpQLSortOrder.ASC);
	}
}
