/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.search.searchengine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.junit.Test;

import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.query.LuceneQuery;

/**
 * Test cases for SolrIndexSearcherImpl.
 */
public class SolrIndexSearcherImplTest {

	/**
	 * Test that a Solr query is correctly constructed from an EP query; the start index is set, the limit is set, and the EpQuery's Lucene query is
	 * set to the Solr query's query string.
	 */
	@Test
	public void testCreateSolrQuery() {
		SolrIndexSearcherImpl searcher = new SolrIndexSearcherImpl();
		final int epQueryLimit = 50;
		final int epQueryStartIndex = 5;
		final int paramLimit = 100;
		final int paramStartIndex = 10;
		final String queryString = "The query string.";
		EpQuery epQuery = new EpQuery();
		LuceneQuery luceneQuery = new LuceneQuery(null) {
			@Override
			public String getNativeQuery() {
				return queryString;
			}

			@Override
			public List<SolrQuery.SortClause> getSortClauses() {
				return new ArrayList<>();
			}
		};
		epQuery.setNativeQuery(luceneQuery);

		// case 1. epQuery.limit is blank, epQuery.start is blank
		SolrIndexSearchResult<Long> result = new SolrIndexSearchResult<>();
		SolrQuery solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, paramLimit, result);

		assertThat(solrQuery.get(CommonParams.START)).isEqualTo(String.valueOf(paramStartIndex));
		assertThat(solrQuery.get(CommonParams.ROWS)).isEqualTo(String.valueOf(paramLimit));
		assertThat(solrQuery.get(CommonParams.Q)).isEqualTo(queryString);

		// case 2. epQuery.limit is blank, epQuery.start is set
		epQuery.setStartIndex(epQueryStartIndex);
		result = new SolrIndexSearchResult<>();
		solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, paramLimit, result);

		assertThat(solrQuery.get(CommonParams.START)).isEqualTo(String.valueOf(epQueryStartIndex + paramStartIndex));
		assertThat(solrQuery.get(CommonParams.ROWS)).isEqualTo(String.valueOf(paramLimit));

		// case 3. epQuery.limit is set, epQuery.start is blank
		epQuery.setStartIndex(0);
		epQuery.setLimit(epQueryLimit);
		result = new SolrIndexSearchResult<>();
		solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, paramLimit, result);

		assertThat(solrQuery.get(CommonParams.START)).isEqualTo(String.valueOf(paramStartIndex));
		assertThat(solrQuery.get(CommonParams.ROWS)).isEqualTo(String.valueOf(epQueryLimit - paramStartIndex));

		// case 4. epQuery.limit is blank, epQuery.start is blank, paramLimit < 0
		epQuery.setStartIndex(0);
		epQuery.setLimit(0);
		result = new SolrIndexSearchResult<>();
		solrQuery = searcher.createSolrQuery(epQuery, paramStartIndex, -1, result);

		assertThat(solrQuery.get(CommonParams.START)).isEqualTo(String.valueOf(paramStartIndex));
		assertThat(solrQuery.get(CommonParams.ROWS)).isEqualTo("0");
	}
}
