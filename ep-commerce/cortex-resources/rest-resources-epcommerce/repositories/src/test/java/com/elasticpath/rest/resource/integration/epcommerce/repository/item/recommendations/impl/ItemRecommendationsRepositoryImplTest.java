/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.recommendations.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Unit tests for {@link ItemRecommendationsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRecommendationsRepositoryImplTest {

	private static final String SCOPE = "scope";
	private static final String GROUP = "upsell";
	private static final String ITEM_ID = "itemId";
	private static final int PAGE_SIZE = 10;

	@Mock
	private ProductAssociationService productAssociationService;
	@InjectMocks
	private ItemRecommendationsRepositoryImpl repository;
	@Mock
	private Store store;
	@Mock
	private Catalog catalog;
	@Mock
	private Product product;
	@Mock
	private ProductAssociation productAssociation;
	@Mock
	private RecommendedItemsPageSizeResolver paginationResolver;
	@Mock
	private ItemRepository itemRepository;

	@Before
	public void setUp() {
		when(store.getCatalog()).thenReturn(catalog);
		when(catalog.getCode()).thenReturn(SCOPE);
		when(paginationResolver.getPageSize()).thenReturn(PAGE_SIZE);
		when(productAssociation.getTargetProduct()).thenReturn(product);
		when(itemRepository.getDefaultItemIdForProductSingle(any(Product.class))).thenReturn(Single.just(ITEM_ID));
	}

	@Test
	public void testGettingRecommendedItemsWhenZeroReturned() {
		repository.getRecommendedItemsFromGroup(store, product, GROUP, 1)
				.test()
				.assertNoErrors();
	}

	@Test
	public void testGettingRecommendedItemsWhenOneReturned() {
		List<ProductAssociation> expectedAssociations = Collections.singletonList(productAssociation);
		when(productAssociationService.findByCriteria(any(ProductAssociationSearchCriteria.class),
				any(Integer.class), any(Integer.class))).thenReturn(expectedAssociations);
		when(productAssociationService.findCountForCriteria(any(ProductAssociationSearchCriteria.class))).thenReturn(Long.valueOf(1));

		repository.getRecommendedItemsFromGroup(store, product, GROUP, 1)
				.test()
				.assertNoErrors();
	}

	@Test
	public void testGettingRecommendedItemsWhenServiceError() {
		when(productAssociationService.findCountForCriteria(any(ProductAssociationSearchCriteria.class))).thenReturn(Long.valueOf(1));
		when(productAssociationService.findByCriteria(any(ProductAssociationSearchCriteria.class),
				any(Integer.class), any(Integer.class))).thenThrow(new EpServiceException(""));

		repository.getRecommendedItemsFromGroup(store, product, GROUP, 1)
				.test()
				.assertError(ResourceOperationFailure.serverError(ItemRecommendationsRepositoryImpl.ITEM_ID_SEARCH_FAILURE));
	}
}
