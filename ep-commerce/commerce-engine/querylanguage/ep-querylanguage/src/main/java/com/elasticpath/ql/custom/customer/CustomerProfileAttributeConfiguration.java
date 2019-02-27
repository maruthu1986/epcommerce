/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.customer;

import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EqQL fields and field descriptors for Customer Profile Attributes.
 */
public class CustomerProfileAttributeConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT a.key FROM AttributeImpl a WHERE a.attributeUsageIdInternal = " + AttributeUsage.CUSTOMERPROFILE);
		addSortField("a.key", EpQLSortOrder.ASC);
	}
}
