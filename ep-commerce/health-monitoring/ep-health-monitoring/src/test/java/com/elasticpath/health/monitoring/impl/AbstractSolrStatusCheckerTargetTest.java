/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

public class AbstractSolrStatusCheckerTargetTest {

	private static final String URL = "http://localhost/search/select?q=*:*&rows=0&wt=json";

	@Test
	public void getTargetUrl() {
		AbstractSolrStatusCheckerTarget abstractSolrStatusCheckerTarget = Mockito.mock(AbstractSolrStatusCheckerTarget.class, Mockito
				.CALLS_REAL_METHODS);
		when(abstractSolrStatusCheckerTarget.getSolrUrl()).thenReturn("solr://localhost");
		abstractSolrStatusCheckerTarget.setIndex("search");
		assertThat(URL).isEqualTo(abstractSolrStatusCheckerTarget.getTargetUrl());
	}
}
