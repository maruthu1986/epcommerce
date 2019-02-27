/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.pagination;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;

/**
 * Tests {@link PaginatedResultTransformer}.
 */
public class PaginatedResultTransformerTest {

	private static final String TEST_ITEM_ID = "test item id";
	private static final List<String> ITEM_ID_LIST = Collections.singletonList(TEST_ITEM_ID);

	private final PaginatedResultTransformer paginatedResultTransformer = new PaginatedResultTransformer();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		paginatedResultTransformer.transformToDomain(null);
	}

	/**
	 * Test transformToEntity returns a valid {@link PaginationDto}.
	 */
	@Test
	public void testTransformToEntitySuccess() {
		int currentPage = 1;
		int numberOfPages = 1;
		final int pageSize = 1;
		final int resultsPerPage = 5;

		PaginationDto expectedPaginationDto = ResourceTypeFactory.createResourceEntity(PaginationDto.class)
				.setCurrentPage(currentPage)
				.setNumberOfPages(numberOfPages)
				.setNumberOfResultsOnPage(pageSize)
				.setPageResults(ITEM_ID_LIST)
				.setPageSize(resultsPerPage)
				.setTotalResultsFound(ITEM_ID_LIST.size());

		PaginatedResult paginatedResult = new PaginatedResult(ITEM_ID_LIST,
				currentPage, resultsPerPage, ITEM_ID_LIST.size());
		PaginationDto paginationDtoResult = paginatedResultTransformer.transformToEntity(paginatedResult);
		assertEquals("Result pagination DTO does not match expected value.", expectedPaginationDto, paginationDtoResult);
	}
}
