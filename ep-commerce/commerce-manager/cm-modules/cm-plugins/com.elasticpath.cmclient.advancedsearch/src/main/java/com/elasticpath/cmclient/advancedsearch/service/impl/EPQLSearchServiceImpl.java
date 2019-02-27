/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.service.impl;

import java.util.Collections;

import com.elasticpath.cmclient.advancedsearch.service.EPQLSearchService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.ql.parser.EpQLParseException;
import com.elasticpath.search.searchengine.EpQLSearchEngine;
import com.elasticpath.search.searchengine.EpQlSearchResult;
import com.elasticpath.search.searchengine.SolrIndexSearchResult;

/**
 * This class provides methods for searching UIDs by given EP query and query validation. 
 */
public class EPQLSearchServiceImpl implements EPQLSearchService {

	private final EpQLSearchEngine searchEngine;

	/**
	 * Constructor initializes EpQL searcher.
	 */
	public EPQLSearchServiceImpl() {
		searchEngine = ServiceLocator.getService("epQLSearchEngine"); //$NON-NLS-1$
	}

	@Override
	public EpQlSearchResult search(final String query, final int startIndex, final int maxResults) {
		if (query == null) {
			SolrIndexSearchResult<Long> solrIndexSearchResult = new SolrIndexSearchResult<>();
			solrIndexSearchResult.setSearchResults(Collections.emptyList());
			solrIndexSearchResult.setStartIndex(0);
			solrIndexSearchResult.setNumFound(0);
			return solrIndexSearchResult;
		}

		return searchEngine.search(query, startIndex, maxResults);
	}

	@Override
	public ValidationStatus validate(final String query) {
		try {
			searchEngine.verify(query);
		} catch (EpQLParseException exception) {
			return new ValidationStatus(false, exception.getLocalizedMessage());
		}

		//FIXME: localize string
		return new ValidationStatus(true, "Query Valid"); //$NON-NLS-1$
	}
}
