/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.parser.query;

import java.util.List;

import org.apache.lucene.search.Query;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * Represents the wrapper for lucene query.
 */
public class LuceneQuery implements NativeQuery {

	private final Query query;
	private List<SolrQuery.SortClause> sortClauses;

	/**
	 * Constructs the wrapper object for lucene query with given arguments.
	 * 
	 * @param query the lucene query
	 */
	public LuceneQuery(final Query query) {
		this.query = query;
	}

	@Override
	public String getNativeQuery() {
		return query.toString();
	}

	/**
	 * Gets the Lucene query.
	 * 
	 * @return the Lucene query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Get the SOLR sort clauses.
	 * @return SOLR sort clauses
	 */
	public List<SolrQuery.SortClause> getSortClauses() {
		return sortClauses;
	}

	/**
	 * Set the SOLR sort clauses.
	 * @param sortClauses SOLR sort clauses
	 */
	public void setSortClauses(final List<SolrQuery.SortClause> sortClauses) {
		this.sortClauses = sortClauses;
	}
}
