/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

/**
 * Target class for testing connectivity to a Solr instance.
 */
public class SolrStatusCheckerTargetImpl extends AbstractSolrStatusCheckerTarget {

	private String solrUrl;

	@Override
	protected String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(final String solrUrl) {
		this.solrUrl = solrUrl;
	}

}
