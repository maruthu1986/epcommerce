/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.pagination.transform.impl;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.collections.PaginationEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.rel.PaginationResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link PaginatedLinksTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PaginatedLinksTransformerTest {

	private static final int EXPECTED_LINKS_SIZE = 3;
	private static final String BASE_URI = "base_uri";
	private static final int THREE_RESULTS_FOUND = 3;
	private static final String RESULT_ITEM_ID = "resultItemId";
	private static final String TEST_SCOPE = "testScope";
	private static final String ITEM_URI = "/mock/items/result/uri";

	@Mock
	private ItemsUriBuilder mockItemsUriBuilder;
	@Mock
	private ItemsUriBuilderFactory mockItemUriBuilderFactory;

	@InjectMocks
	private PaginatedLinksTransformerImpl paginatedLinksTransformer;


	/**
	 * Initialize mock classes.
	 */
	@Before
	public void setUp() {
		when(mockItemUriBuilderFactory.get())
			.thenReturn(mockItemsUriBuilder);

		when(mockItemsUriBuilder.setItemId(anyString()))
			.thenReturn(mockItemsUriBuilder);

		when(mockItemsUriBuilder.setScope(TEST_SCOPE))
			.thenReturn(mockItemsUriBuilder);
	}

	/**
	 * Tests transform to representation with a single result.
	 */
	@Test
	public void testsTransformToRepresentationWithOneResult() {

		int currentPage = 1;
		int numberOfPages = 1;
		int totalResultsFound = 1;
		int pageSize = 1;
		int numberOfResultsOnPage = 1;
		List<String> pageResults = Collections.singletonList(RESULT_ITEM_ID);

		PaginationDto testPaginationDto = createPaginationDto(currentPage, numberOfPages, totalResultsFound,
				pageSize, numberOfResultsOnPage, pageResults);

		when(mockItemsUriBuilder.build())
			.thenReturn(ITEM_URI);

		ResourceState<PaginatedLinksEntity> resultRepresentation =
				paginatedLinksTransformer.transformToResourceState(testPaginationDto, TEST_SCOPE, BASE_URI);

		ResourceLink expectedItemLink = ElementListFactory.createElement(ITEM_URI, ItemsMediaTypes.ITEM.id());

		assertResourceState(resultRepresentation)
				.self(SelfFactory.createSelf(BASE_URI))
				.linkCount(1)
				.containsLink(expectedItemLink);

		PaginationEntity expectedPaginationEntity =
				createPaginationEntity(currentPage, numberOfPages, totalResultsFound, pageSize, numberOfResultsOnPage);

		assertEquals(expectedPaginationEntity, resultRepresentation.getEntity().getPagination());
	}

	/**
	 * Tests transform to representation from results not on first page.
	 */
	@Test
	public void testsTransformToRepresentationFromResultsNotOnFirstPage() {

		int currentPage = 2;
		int numberOfPages = 2;
		int totalResultsFound = 2;
		int pageSize = 1;
		int numberOfResultsOnPage = 1;
		List<String> pageResults = Collections.singletonList(RESULT_ITEM_ID);

		PaginationDto testPaginationDto = createPaginationDto(currentPage, numberOfPages, totalResultsFound,
				pageSize, numberOfResultsOnPage, pageResults);

		when(mockItemsUriBuilder.build())
			.thenReturn(ITEM_URI);

		ResourceState<PaginatedLinksEntity> resultRepresentation =
				paginatedLinksTransformer.transformToResourceState(testPaginationDto, TEST_SCOPE, BASE_URI);

		ResourceLink expectedItemLink = ElementListFactory.createElement(ITEM_URI, ItemsMediaTypes.ITEM.id());
		String expectedPreviousPageUri = URIUtil.format(BASE_URI, PageNumber.URI_PART, "1");
		ResourceLink expectedPreviousPageLink =	ResourceLinkFactory.createNoRev(expectedPreviousPageUri,
						CollectionsMediaTypes.PAGINATED_LINKS.id(), PaginationResourceRels.PREVIOUS_PAGE_REL);

		String expectedUri = URIUtil.format(BASE_URI, PageNumber.URI_PART, Integer.toString(currentPage));

		assertResourceState(resultRepresentation)
				.self(SelfFactory.createSelf(expectedUri))
				.linkCount(2)
				.containsLink(expectedItemLink)
				.containsLink(expectedPreviousPageLink);

		PaginationEntity expectedPaginationEntity =
				createPaginationEntity(currentPage, numberOfPages, totalResultsFound, pageSize, numberOfResultsOnPage);

		assertEquals(expectedPaginationEntity, resultRepresentation.getEntity().getPagination());
	}

	/**
	 * Tests transform to representation with multiple results.
	 */
	@Test
	public void testsTransformToRepresentationWithMultipleResult() {
		int currentPage = 1;
		int numberOfPages = 2;
		int totalResultsFound = THREE_RESULTS_FOUND;
		int pageSize = 2;
		int numberOfResultsOnPage = 2;
		String itemCodeOne = "resultOne";
		String itemCodeTwo = "resultTwo";
		List<String> pageResults = Arrays.asList(itemCodeOne, itemCodeTwo);

		PaginationDto testPaginationDto = createPaginationDto(currentPage, numberOfPages, totalResultsFound,
				pageSize, numberOfResultsOnPage, pageResults);

		when(mockItemsUriBuilder.build())
			.thenReturn(URIUtil.format(ITEM_URI, itemCodeOne))
			.thenReturn(URIUtil.format(ITEM_URI, itemCodeTwo));

		ResourceState<PaginatedLinksEntity> resultRepresentation =
				paginatedLinksTransformer.transformToResourceState(testPaginationDto, TEST_SCOPE, BASE_URI);
		String expectedNextPageUri = URIUtil.format(BASE_URI, PageNumber.URI_PART, "2");
		ResourceLink expectedNextPageLink =	ResourceLinkFactory.createNoRev(expectedNextPageUri,
				CollectionsMediaTypes.PAGINATED_LINKS.id(), PaginationResourceRels.NEXT_PAGE_REL);

		assertResourceState(resultRepresentation)
				.self(SelfFactory.createSelf(BASE_URI))
				.linkCount(EXPECTED_LINKS_SIZE)
				.containsLink(ElementListFactory.createElement(URIUtil.format(ITEM_URI, itemCodeOne), ItemsMediaTypes.ITEM.id()))
				.containsLink(ElementListFactory.createElement(URIUtil.format(ITEM_URI, itemCodeTwo), ItemsMediaTypes.ITEM.id()))
				.containsLink(expectedNextPageLink);

		PaginationEntity expectedPaginationEntity =
				createPaginationEntity(currentPage, numberOfPages, totalResultsFound, pageSize, numberOfResultsOnPage);

		assertEquals(expectedPaginationEntity, resultRepresentation.getEntity().getPagination());
	}

	/**
	 * Tests transform to representation with containing next and previous pages.
	 */
	@Test
	public void testsTransformToRepresentationWithNextAndPreviousPages() {

		int currentPage = 2;
		final int numberOfPages = 3;
		final int totalResultsFound = 3;
		int pageSize = 1;
		int numberOfResultsOnPage = 1;
		List<String> pageResults = Collections.singletonList(RESULT_ITEM_ID);

		PaginationDto testPaginationDto = createPaginationDto(currentPage, numberOfPages, totalResultsFound,
				pageSize, numberOfResultsOnPage, pageResults);

		when(mockItemsUriBuilder.build())
			.thenReturn(ITEM_URI);

		ResourceState<PaginatedLinksEntity> resultRepresentation =
				paginatedLinksTransformer.transformToResourceState(testPaginationDto, TEST_SCOPE, BASE_URI);

		ResourceLink expectedItemLink = ElementListFactory.createElement(ITEM_URI, ItemsMediaTypes.ITEM.id());

		String expectedNextPageUri = URIUtil.format(BASE_URI, PageNumber.URI_PART, "3");
		ResourceLink expectedNextPageLink =	ResourceLinkFactory.createNoRev(expectedNextPageUri,
				CollectionsMediaTypes.PAGINATED_LINKS.id(), PaginationResourceRels.NEXT_PAGE_REL);

		String expectedPreviousPageUri = URIUtil.format(BASE_URI, PageNumber.URI_PART, "1");
		ResourceLink expectedPreviousPageLink =	ResourceLinkFactory.createNoRev(expectedPreviousPageUri,
				CollectionsMediaTypes.PAGINATED_LINKS.id(), PaginationResourceRels.PREVIOUS_PAGE_REL);

		String expectedSelfUri = URIUtil.format(BASE_URI, PageNumber.URI_PART, "2");

		assertResourceState(resultRepresentation)
				.self(SelfFactory.createSelf(expectedSelfUri))
				.linkCount(EXPECTED_LINKS_SIZE)
				.containsLink(expectedItemLink)
				.containsLink(expectedNextPageLink)
				.containsLink(expectedPreviousPageLink);

		PaginationEntity expectedPaginationEntity =
				createPaginationEntity(currentPage, numberOfPages, totalResultsFound, pageSize, numberOfResultsOnPage);

		assertEquals(expectedPaginationEntity, resultRepresentation.getEntity().getPagination());
	}

	/**
	 * Tests transform to representation with zero results.
	 */
	@Test
	public void testsTransformToRepresentationWithZeroResults() {
		int currentPage = 1;
		int numberOfPages = 1;
		int totalResultsFound = 0;
		int pageSize = 2;
		int numberOfResultsOnPage = 0;
		List<String> pageResults = Collections.emptyList();

		PaginationDto testPaginationDto = createPaginationDto(currentPage, numberOfPages, totalResultsFound,
				pageSize, numberOfResultsOnPage, pageResults);

		ResourceState<PaginatedLinksEntity> resultRepresentation =
				paginatedLinksTransformer.transformToResourceState(testPaginationDto, TEST_SCOPE, BASE_URI);

		assertResourceState(resultRepresentation)
				.linkCount(0);

		PaginationEntity expectedPaginationEntity =
				createPaginationEntity(currentPage, numberOfPages, totalResultsFound, pageSize, numberOfResultsOnPage);

		assertEquals(expectedPaginationEntity, resultRepresentation.getEntity().getPagination());
	}

	private PaginationDto createPaginationDto(final int currentPage,
			final int numberOfPages,
			final int totalResultsFound,
			final int pageSize,
			final int numberOfResultsOnPage,
			final List<String> pageResults) {

		return ResourceTypeFactory.createResourceEntity(PaginationDto.class)
				.setCurrentPage(currentPage)
				.setNumberOfPages(numberOfPages)
				.setNumberOfResultsOnPage(numberOfResultsOnPage)
				.setPageResults(pageResults)
				.setPageSize(pageSize)
				.setTotalResultsFound(totalResultsFound);
	}

	private PaginationEntity createPaginationEntity(final int currentPage, final int numberOfPages, final int totalResultsFound,
			final int pageSize, final int numberOfResultsOnPage) {
		return PaginationEntity.builder()
				.withCurrent(currentPage)
				.withPageSize(pageSize)
				.withResultsOnPage(numberOfResultsOnPage)
				.withPages(numberOfPages)
				.withResults(totalResultsFound)
				.build();
	}
}
