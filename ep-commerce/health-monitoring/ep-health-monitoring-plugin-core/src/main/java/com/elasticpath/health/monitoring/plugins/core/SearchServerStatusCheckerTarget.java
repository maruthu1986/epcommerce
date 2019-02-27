/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.plugins.core;

import com.elasticpath.health.monitoring.impl.AbstractSolrStatusCheckerTarget;
import com.elasticpath.service.search.SearchHostLocator;

/**
 * Target class for testing connectivity to an Elastic Path Search Server.
 */
public class SearchServerStatusCheckerTarget extends AbstractSolrStatusCheckerTarget {

	private SearchHostLocator searchHostLocator;

	public void setSearchHostLocator(final SearchHostLocator searchHostLocator) {
		this.searchHostLocator = searchHostLocator;
	}

	@Override
	protected String getSolrUrl() {
		return searchHostLocator.getSearchHostLocation();
	}

}
