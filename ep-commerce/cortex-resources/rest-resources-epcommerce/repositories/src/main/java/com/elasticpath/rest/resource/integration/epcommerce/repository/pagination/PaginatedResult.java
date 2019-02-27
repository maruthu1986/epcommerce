/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.pagination;

import java.util.Collection;

/**
 * Wraps results to carry parameters in and out of a transformer.
 */
public class PaginatedResult {

	private final Collection<String> resultIds;
	private final int currentPage;
	private final int resultsPerPage;
	private final int totalNumberOfResults;

	/**
	 * Instantiates a new paginated result.
	 *
	 * @param resultIds the result ids
	 * @param currentPage the current page
	 * @param resultsPerPage the results per page
	 * @param totalNumberOfResults the total number of results
	 */
	public PaginatedResult(final Collection<String> resultIds, final int currentPage, final int resultsPerPage,
			final int totalNumberOfResults) {
		assert resultIds != null : "cannot pass in null resultIds";
		this.currentPage = currentPage;
		this.resultsPerPage = resultsPerPage;
		this.resultIds = resultIds;
		this.totalNumberOfResults = totalNumberOfResults;
	}

	public Collection<String> getResultIds() {
		return resultIds;
	}

	public int getTotalNumberOfResults() {
		return totalNumberOfResults;
	}

	/**
	 * Gets the current page.
	 *
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Gets the results per page.
	 *
	 * @return the resultsPerPage
	 */
	public int getResultsPerPage() {
		return resultsPerPage;
	}

	/**
	 * Gets the number of pages.
	 *
	 * @return the numberOfPages
	 */
	public int getNumberOfPages() {
		int calculatedPages;

		if (totalNumberOfResults == 0) {
			calculatedPages = 1;
		} else {
			calculatedPages = (int) Math.ceil((double) totalNumberOfResults / resultsPerPage);
		}
		return calculatedPages;
	}
}
