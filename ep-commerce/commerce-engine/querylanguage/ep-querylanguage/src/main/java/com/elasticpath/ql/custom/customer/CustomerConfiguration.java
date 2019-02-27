/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.customer;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.EpQLSortOrder;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Holds mapping between EqQL fields and Solr field descriptors for Customer.
 * Currently nothing to map.
 */
public class CustomerConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		addSortField(SolrIndexConstants.USER_ID, EpQLSortOrder.ASC);
	}
}
