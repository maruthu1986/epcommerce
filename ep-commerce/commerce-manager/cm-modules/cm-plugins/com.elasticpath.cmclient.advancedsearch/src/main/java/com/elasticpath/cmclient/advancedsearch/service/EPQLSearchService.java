/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.service;

import com.elasticpath.cmclient.advancedsearch.service.impl.ValidationStatus;
import com.elasticpath.search.searchengine.EpQlSearchResult;

/**
 * Interface that provides methods for working with EP query.
 */
public interface EPQLSearchService {

	/**
	 * Searches UIDs by given EP Query.
	 * 
	 * @param query the EP Query
	 * @param startIndex the initial index to display results for
	 * @param maxResults the maximum results to return from given start index (maximum per page)
	 * @return the results as instance of <code>SolrIndexSearchResult</code>
	 */
	EpQlSearchResult search(String query, int startIndex, int maxResults);

	/**
	 * Validates the EP query.
	 * 
	 * @param query an EPQueryLanguage (EPQL) string
	 * @return the validation status for given query
	 */
	ValidationStatus validate(String query);

}