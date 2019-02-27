/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.junit.Test;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test case for {@link AbstractQueryComposerImpl}.
 */
public class AbstractQueryComposerImplTest extends QueryComposerTestCase {
	
	private AbstractQueryComposerImpl queryComposer;
	
	
	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		queryComposer = getQueryComposer();
	}

	private AbstractQueryComposerImpl getQueryComposer() {
		return new AbstractQueryComposerImpl() {

			@Override
			protected Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
				return new BooleanQuery();
			}

			@Override
			protected Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
				return new BooleanQuery();
			}

			@Override
			protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
				return true;
			}
		};
	}
	
	/**
	 * Test method for {@link AbstractQueryComposerImpl#getMatchAllQuery()}.
	 */
	@Test
	public void testMatchAllQuery() {
		// our query _has_ to go through the lucene query parser
		QueryParser parser = new QueryParser(SolrIndexConstants.LUCENE_MATCH_VERSION, "text",
				new SimpleAnalyzer(SolrIndexConstants.LUCENE_MATCH_VERSION));
		Query query = null;
		
		try {
			query = parser.parse(queryComposer.getMatchAllQuery().toString());
		} catch (ParseException e) {
			fail("Should not throw an exception.");
		}
		
		assertNotNull(query);
		assertEquals(query, new MatchAllDocsQuery());
	}
	
	@Override
	public void testWrongSearchCriteria() {
		// not valid for the abstract class
	}
	
	@Override
	public void testEmptyCriteria() {
		// not valid for the abstract class
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return null;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return null;
	}
}
