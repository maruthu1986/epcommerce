/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.USER_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.WISHLIST_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistTestFactory.buildWishlistIdentifier;

import com.google.common.collect.ImmutableList;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.wishlists.WishlistEntity;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Test for {@link WishlistEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WishlistEntityRepositoryImplTest {

	private static final String WISHLIST_GUID = "guid";

	private final WishlistIdentifier wishlistIdentifier = buildWishlistIdentifier();

	@Mock
	private WishList wishList;

	@InjectMocks
	private WishlistEntityRepositoryImpl<WishlistEntity, WishlistIdentifier> repository;

	@Mock
	private WishlistRepository wishlistRepository;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Test
	public void verifyFindOneReturnsNotFoundWhenWishListIsNotFound() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(wishlistIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsWishlistEntity() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishList.getGuid()).thenReturn(WISHLIST_GUID);

		repository.findOne(wishlistIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(wishlistEntity -> wishlistEntity.getWishlistId().equals(WISHLIST_GUID));
	}

	@Test
	public void verifyFindAllReturnNotFoundWhenWishListIdsNotFound() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(wishlistRepository.getWishlistIds(USER_ID, SCOPE)).thenReturn(Observable.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findAll(SCOPE_IDENTIFIER_PART)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindAllReturnWishlistIdentifiers() {
		String wishlistId0 = "0";
		String wishlistId1 = "1";

		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(wishlistRepository.getWishlistIds(USER_ID, SCOPE)).thenReturn(Observable.fromIterable(ImmutableList.of(wishlistId0, wishlistId1)));

		repository.findAll(SCOPE_IDENTIFIER_PART)
				.test()
				.assertNoErrors()
				.assertValueAt(0, wishlistIdentifier -> wishlistIdentifier.getWishlistId().getValue().equals(wishlistId0))
				.assertValueAt(1, wishlistIdentifier -> wishlistIdentifier.getWishlistId().getValue().equals(wishlistId1));
	}
}
