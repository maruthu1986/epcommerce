/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.recommendation.impl;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.ITEM_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.recommendations.ItemRecommendationGroupIdentifier;
import com.elasticpath.rest.definition.recommendations.ItemRecommendationGroupsIdentifier;
import com.elasticpath.rest.definition.recommendations.PaginatedRecommendationsIdentifier;
import com.elasticpath.rest.id.Identifier;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.IntegerIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.pagination.PaginationEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.ItemRecommendationsRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.pagination.PaginatedResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;

/**
 * Test for {@link PaginatedLinksEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaginatedLinksEntityRepositoryImplTest {

	private static final String GROUP_ID = "upsell";
	private static final ImmutableList<String> RESULT_IDS = ImmutableList.of(encodeItemId("ID_1"), encodeItemId("ID_2"));

	private ItemsIdentifier items;
	@Mock
	private IdentifierTransformerProvider identifierTransformerProvider;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private ItemRecommendationsRepository itemRecommendationsRepository;

	@InjectMocks
	private PaginatedLinksEntityRepositoryImpl repository;
	@Mock
	private IdentifierTransformer<Identifier> transformer;
	@Mock
	private ProductSku productSku;
	@Mock
	private Product product;
	@Mock
	private Store store;
	@Mock
	private PaginatedResult paginatedResult;

	private static String encodeItemId(final String skuCode) {
		return CompositeIdUtil.encodeCompositeId(ImmutableSortedMap.of(ItemRepository.SKU_CODE_KEY, skuCode));
	}

	@Test
	public void  testGetRecommendedItemsFromGroup() {
		final int pageNumber = 1;
		PaginatedRecommendationsIdentifier identifier = mockPaginatedRecommendationsIdentifier(pageNumber);

		setUpRecommendationRepository(pageNumber);

		repository.getRecommendedItemsFromGroup(identifier)
				.test()
				.assertValue(paginatedResult);
	}

	@Test
	public void  testGetPagingLinksForTheFirstPage() {
		final int pageNumber = 1;
		PaginatedRecommendationsIdentifier identifier = mockPaginatedRecommendationsIdentifier(pageNumber);

		setUpRecommendationRepository(pageNumber);

		final int numPages = 10;
		when(paginatedResult.getNumberOfPages()).thenReturn(numPages);

		repository.getPagingLinks(identifier)
				.test()
				.assertValueCount(1);
	}

	@Test
	public void  testGetPagingLinksForTheMiddlePage() {
		final int pageNumber = 3;
		PaginatedRecommendationsIdentifier identifier = mockPaginatedRecommendationsIdentifier(pageNumber);

		setUpRecommendationRepository(pageNumber);

		final int numPages = 10;
		when(paginatedResult.getNumberOfPages()).thenReturn(numPages);


		repository.getPagingLinks(identifier)
				.test()
				.assertValueCount(2);
	}

	@Test
	public void  testGetElements() {
		final int pageNumber = 1;

		setUpRecommendationRepository(pageNumber);
		PaginatedRecommendationsIdentifier identifier = mockPaginatedRecommendationsIdentifier(pageNumber);

		when(paginatedResult.getResultIds()).thenReturn(RESULT_IDS);

		List<ItemIdentifier> expectedResult = RESULT_IDS.stream()
				.map(resultId -> ItemIdentifier.builder()
						.withItemId(CompositeIdentifier.of(CompositeIdUtil.decodeCompositeId(resultId)))
						.withItems(items)
						.build())
				.collect(Collectors.toList());

		repository.getElements(identifier)
				.test()
				.assertValueSequence(expectedResult);
	}

	@Test
	public void  testGetPaginationInformation() {
		final int pageNumber = 1;

		PaginatedRecommendationsIdentifier identifier = mockPaginatedRecommendationsIdentifier(pageNumber);
		setUpRecommendationRepository(pageNumber);

		final int pageNum = 10;
		final int results = 1;

		when(paginatedResult.getCurrentPage()).thenReturn(pageNumber);
		when(paginatedResult.getNumberOfPages()).thenReturn(pageNum);
		when(paginatedResult.getResultsPerPage()).thenReturn(results);
		when(paginatedResult.getTotalNumberOfResults()).thenReturn(pageNum);
		when(paginatedResult.getResultIds()).thenReturn(RESULT_IDS);

		repository.getPaginationInfo(identifier)
				.test()
				.assertValue(PaginationEntity.builder()
						.withCurrent(pageNumber)
						.withPages(pageNum)
						.withPageSize(results)
						.withResults(pageNum)
						.withResultsOnPage(RESULT_IDS.size())
						.build()
				);
	}

	private void setUpRecommendationRepository(final int pageNumber) {
		when(identifierTransformerProvider.forUriPart(ItemIdentifier.ITEM_ID)).thenReturn(transformer);
		when(transformer.identifierToUri(ITEM_IDENTIFIER_PART)).thenReturn(SKU_CODE);
		when(itemRepository.getSkuForItemIdAsSingle(SKU_CODE)).thenReturn(Single.just(productSku));
		when(productSku.getProduct()).thenReturn(product);
		when(storeRepository.findStoreAsSingle(ResourceTestConstants.SCOPE)).thenReturn(Single.just(store));
		when(itemRecommendationsRepository.getRecommendedItemsFromGroup(store, product, GROUP_ID, pageNumber))
				.thenReturn(Single.just(paginatedResult));
	}

	private PaginatedRecommendationsIdentifier mockPaginatedRecommendationsIdentifier(
			final int pageNumber) {

		ItemIdentifier itemIdentifier = IdentifierTestFactory.buildItemIdentifier(ResourceTestConstants.SCOPE, SKU_CODE);
		items = itemIdentifier.getItems();

		ItemRecommendationGroupsIdentifier recommendationGroups = ItemRecommendationGroupsIdentifier.builder()
				.withItem(itemIdentifier)
				.build();
		ItemRecommendationGroupIdentifier itemRecommendationGroup = ItemRecommendationGroupIdentifier.builder()
				.withItemRecommendationGroups(recommendationGroups)
				.withGroupId(StringIdentifier.of(GROUP_ID))
				.build();
		return PaginatedRecommendationsIdentifier.builder()
				.withItemRecommendationGroup(itemRecommendationGroup)
				.withPageId(IntegerIdentifier.of(pageNumber))
				.build();
	}
}
