/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.catalogview.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.google.common.collect.Lists;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * The default implementation of <code>CatalogViewResultHistory</code>.
 */
public class CatalogViewResultHistoryImpl extends AbstractEpDomainImpl implements CatalogViewResultHistory {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final Deque<CatalogViewResult> resultStack = new ArrayDeque<>();

	/**
	 * Add the given catalog view request and returns a catalog view result. If we can find similar catalog view request in the history stack,
	 * products in the returned catalog view result will be populated so you don't need to load them again.
	 *
	 * @param newRequest the catalog view request to add
	 * @return a catalog view result of the given catalog view request
	 */
	@Override
	public CatalogViewResult addRequest(final CatalogViewRequest newRequest) {
		while (!resultStack.isEmpty()) {
			final CatalogViewResult catalogViewResult = resultStack.removeFirst();
			final CatalogViewRequest catalogViewRequest = catalogViewResult.getCatalogViewRequest();
			try {
				final int compareResult = newRequest.compare(catalogViewRequest);
				if (compareResult == 0) {
					catalogViewResult.setCatalogViewRequest(newRequest);
					resultStack.addFirst(catalogViewResult);
					return catalogViewResult;
				} else if (compareResult > 0) {
					// The new request is a more specific request.
					// Currently, It means that the new request has more filters.
					final CatalogViewResult newCatalogViewResult = generateNewSearchResult(newRequest);
					newCatalogViewResult.replicateData(catalogViewResult);
					resultStack.addFirst(catalogViewResult);
					resultStack.addFirst(newCatalogViewResult);
					return newCatalogViewResult;
				} else if (compareResult < 0) {
					// The new request is a more generic request
					// Currently, It means that the new request has less filters.
					continue;
				}
			} catch (CatalogViewRequestUnmatchException e) {
				resultStack.clear();
				break;
			}
		}

		final CatalogViewResult newSearchResult = generateNewSearchResult(newRequest);
		resultStack.addFirst(newSearchResult);
		return newSearchResult;
	}

	private CatalogViewResult generateNewSearchResult(final CatalogViewRequest newSearchRequest) {
		final CatalogViewResult newSearchResult;
		if (newSearchRequest instanceof SearchRequest) {
			newSearchResult = getBean(ContextIdNames.SEARCH_RESULT);
			newSearchResult.setCatalogViewRequest(newSearchRequest);
		} else {
			newSearchResult = getBean(ContextIdNames.BROWSING_RESULT);
			newSearchResult.setCatalogViewRequest(newSearchRequest);
		}
		return newSearchResult;
	}

	/**
	 * Returns the size of catalog view results.
	 *
	 * @return the size of catalog view results
	 */
	@Override
	public int size() {
		return resultStack.size();
	}

	/**
	 * Returns the last catalog view result.
	 *
	 * @return the last catalog view result
	 */
	@Override
	public CatalogViewResult getLastResult() {
		if (resultStack.isEmpty()) {
			return null;
		}
		return resultStack.peekFirst();
	}

	/**
	 * Returns the catalog view results as a <code>List</code>. The most recent catalog view result is at the tail.
	 *
	 * @return the catalog view result as a <code>List</code>.
	 */
	@Override
	public List<CatalogViewResult> getResultList() {
		return Lists.newArrayList(resultStack.descendingIterator());
	}
}
