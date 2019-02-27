/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import com.elasticpath.health.monitoring.ResponseValidator;
import com.elasticpath.health.monitoring.Status;

/**
 * Base class for providing Solr status checks.
 */
public abstract class AbstractSolrStatusCheckerTarget extends AbstractStatusCheckerTarget implements InitializingBean {

	private String index;

	private int connectionTimeout;
	private ResponseValidator<String> solrResponseValidator;

	private final HttpStatusTargetImpl httpStatusTarget = new HttpStatusTargetImpl();

	/**
	 * @return the base URL for the SOLR server to check.
	 */
	protected abstract String getSolrUrl();

	@Override
	public Status check() {
		return httpStatusTarget.check();
	}

	@Override
	public void afterPropertiesSet() {
		httpStatusTarget.setUrl(getTargetUrl());
		if (connectionTimeout != 0) {
			httpStatusTarget.setConnectTimeout(connectionTimeout);
		}
		if (solrResponseValidator != null) {
			httpStatusTarget.setResponseBodyValidator(solrResponseValidator);
		}
	}

	/**
	 * @return the ping URL for the SOLR instance
	 */
	protected String getTargetUrl() {
		String solrUrl = getSolrUrl();
		solrUrl = StringUtils.replaceOnce(solrUrl, "solr://", "http://");
		return solrUrl + "/" + index + "/select?q=*:*&rows=0&wt=json";
	}

	protected String getIndex() {
		return this.index;
	}

	public void setIndex(final String index) {
		this.index = index;
	}

	protected int getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(final int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	protected ResponseValidator<String> getSolrResponseValidator() {
		return this.solrResponseValidator;
	}

	public void setSolrResponseValidator(final ResponseValidator<String> solrResponseValidator) {
		this.solrResponseValidator = solrResponseValidator;
	}
}
