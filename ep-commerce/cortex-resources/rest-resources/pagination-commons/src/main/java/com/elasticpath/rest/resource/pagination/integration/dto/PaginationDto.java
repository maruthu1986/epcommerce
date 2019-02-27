/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.pagination.integration.dto;

import com.elasticpath.rest.schema.ResourceEntity;

import java.util.Collection;

/**
 * The PaginationDto interface.
 */
public interface PaginationDto extends ResourceEntity {

	/**
	 * Sets the total results found.
	 *
	 * @param totalResultsFound the total results found
	 * @return the pagination dto
	 */
	PaginationDto setTotalResultsFound(int totalResultsFound);

	/**
	 * Gets the total results found.
	 *
	 * @return the total results found
	 */
	int getTotalResultsFound();

	/**
	 * Sets the current page.
	 *
	 * @param currentPage the current page
	 * @return the pagination dto
	 */
	PaginationDto setCurrentPage(int currentPage);

	/**
	 * Gets the current page.
	 *
	 * @return the current page
	 */
	int getCurrentPage();

	/**
	 * Sets the page results.
	 *
	 * @param pageResults the page results
	 * @return the pagination dto
	 */
	PaginationDto setPageResults(Collection<String> pageResults);

	/**
	 * Gets the page results.
	 *
	 * @return the page results
	 */
	Collection<String> getPageResults();

	/**
	 * Sets the number of results on a page.
	 *
	 * @param numberOfResultsOnPage the number of results on page
	 * @return the pagination dto
	 */
	PaginationDto setNumberOfResultsOnPage(int numberOfResultsOnPage);

	/**
	 * Gets the number of results on a page.
	 *
	 * @return the number of results on page
	 */
	int getNumberOfResultsOnPage();

	/**
	 * Sets the number of pages.
	 *
	 * @param numberOfPages the number of pages
	 * @return the pagination dto
	 */
	PaginationDto setNumberOfPages(int numberOfPages);

	/**
	 * Gets the number of pages.
	 *
	 * @return the number of pages
	 */
	int getNumberOfPages();

	/**
	 * Sets the number of results per page.
	 *
	 * @param pageSize the number of results per page
	 * @return the pagination dto
	 */
	PaginationDto setPageSize(int pageSize);

	/**
	 * @return the pageSize
	 */
	int getPageSize();
}
