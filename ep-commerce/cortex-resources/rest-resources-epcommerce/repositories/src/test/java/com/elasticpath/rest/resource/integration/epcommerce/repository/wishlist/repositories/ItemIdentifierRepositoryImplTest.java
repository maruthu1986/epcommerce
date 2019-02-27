/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.ITEM_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.WISHLIST_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistTestFactory.buildWishlistLineItemIdentifier;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Test for {@link ItemIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemIdentifierRepositoryImplTest {

	private final WishlistLineItemIdentifier identifier = buildWishlistLineItemIdentifier();

	@Mock
	private ProductSku productSku;

	@InjectMocks
	private ItemIdentifierRepositoryImpl<WishlistLineItemIdentifier, ItemIdentifier> repository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private WishlistRepository wishlistRepository;

	@Test
	public void verifyGetElementsNotFoundWhenProductSkuNotFound() {
		when(wishlistRepository.getProductSku(WISHLIST_ID, LINE_ITEM_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.getElements(identifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnItemIdentifier() {
		when(wishlistRepository.getProductSku(WISHLIST_ID, LINE_ITEM_ID)).thenReturn(Single.just(productSku));
		when(itemRepository.getItemIdForProductSku(productSku)).thenReturn(ITEM_IDENTIFIER_PART);

		repository.getElements(identifier)
				.test()
				.assertNoErrors()
				.assertValue(itemIdentifier -> itemIdentifier.getItemId().getValue().get(ItemRepository.SKU_CODE_KEY).equals(SKU_CODE));
	}
}
