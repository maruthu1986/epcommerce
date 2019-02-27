/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.catalog;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;

/**
 * Holds mapping between EqQL fields and field descriptors for GiftCertificate.
 */
public class GiftCertificateConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT g.guid FROM GiftCertificateImpl g");
		addSortField("g.guid", EpQLSortOrder.ASC);
	}
}
