/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SERVER_ERROR;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.WISHLIST_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistTestFactory.buildWishlistIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Test for {@link WishlistLineItemIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WishlistLineItemIdentifierRepositoryImplTest {

	private static final int NUM_TEST_DATA = 2;

	private final WishlistIdentifier wishlistIdentifier = buildWishlistIdentifier();

	@Mock
	private WishList wishList;

	@InjectMocks
	private WishlistLineItemIdentifierRepositoryImpl<WishlistIdentifier, WishlistLineItemIdentifier> repository;

	@Mock
	private WishlistRepository wishlistRepository;

	@Test
	public void verifyGetElementsReturnNotFoundWhenWishlistNotFound() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.getElements(wishlistIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenNoShoppingItemsAreFound() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishList.getAllItems()).thenReturn(Collections.emptyList());

		repository.getElements(wishlistIdentifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnWishlistLineitemIdentifiers() {
		List<ShoppingItem> shoppingItems = new ArrayList<>();

		for (int i = 0; i < NUM_TEST_DATA; i++) {
			String guid = String.valueOf(i);
			ShoppingItem shoppingItem = mock(ShoppingItem.class);
			when(shoppingItem.getGuid()).thenReturn(guid);
			shoppingItems.add(shoppingItem);
		}

		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishList.getAllItems()).thenReturn(shoppingItems);

		repository.getElements(wishlistIdentifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, wishlistLineItemIdentifier -> wishlistLineItemIdentifier.getLineItemId().getValue().equals("0"))
				.assertValueAt(1, wishlistLineItemIdentifier -> wishlistLineItemIdentifier.getLineItemId().getValue().equals("1"));
	}

	@Test
	public void verifyDeleteAllReturnsErrorWhenRemoveAllItemsFails() {
		when(wishlistRepository.removeAllItemsFromWishlist(WISHLIST_ID))
				.thenReturn(Completable.error(ResourceOperationFailure.serverError(SERVER_ERROR)));

		repository.deleteAll(wishlistIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(SERVER_ERROR, ResourceStatus.SERVER_ERROR));
	}

	@Test
	public void verifyDeleteAllReturnsCompleteWhenRemovingItemsSucceed() {
		when(wishlistRepository.removeAllItemsFromWishlist(WISHLIST_ID)).thenReturn(Completable.complete());

		repository.deleteAll(wishlistIdentifier)
				.test()
				.assertComplete();
	}
}
