/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.pagination;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

/**
 * Test class for {@link PaginatedResult}.
 */
public class PaginatedResultTest {

	private static final int PAGE = 1;
	private static final int RESULTS_PER_PAGE = 10;
	private static final int NUMBER_OF_RESULTS = 110;
	private static final int EXPECTED_NUMBER_OF_PAGES = NUMBER_OF_RESULTS / RESULTS_PER_PAGE;

	/**
	 * Test get number of pages.
	 */
	@Test
	public void testGetNumberOfPages() {

		Collection<String> emptyList = Collections.emptyList();
		PaginatedResult paginatedResult =
				new PaginatedResult(emptyList, PAGE, RESULTS_PER_PAGE, NUMBER_OF_RESULTS);

		int numberOfPages = paginatedResult.getNumberOfPages();

		assertEquals("Number of pages does not match expected value.", EXPECTED_NUMBER_OF_PAGES, numberOfPages);

	}


	/**
	 * Test get number of pages when there are no results.
	 */
	@Test
	public void testGetNumberOfPagesWhenThereAreNoResults() {

		Collection<String> emptyList = Collections.emptyList();
		PaginatedResult paginatedResult =
				new PaginatedResult(emptyList, PAGE, RESULTS_PER_PAGE, 0);

		int numberOfPages = paginatedResult.getNumberOfPages();

		assertEquals("There Should be 1 page with 0 results.", 1, numberOfPages);
	}

}
