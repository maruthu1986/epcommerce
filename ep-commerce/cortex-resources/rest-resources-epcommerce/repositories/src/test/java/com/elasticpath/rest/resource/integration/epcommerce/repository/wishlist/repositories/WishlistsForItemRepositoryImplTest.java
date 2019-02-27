/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildItemIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.ITEM_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.WISHLIST_ID;

import io.reactivex.Maybe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Test for {@link WishlistsForItemRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WishlistsForItemRepositoryImplTest {

	private static final String ENCODED_ID = "itemId";

	private final ItemIdentifier itemIdentifier = buildItemIdentifier(SCOPE, SKU_CODE);

	@Mock
	private WishList wishList;

	@InjectMocks
	private WishlistsForItemRepositoryImpl<ItemIdentifier, WishlistIdentifier> repository;

	@Mock
	private WishlistRepository wishlistRepository;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IdentifierTransformerProvider identifierTransformerProvider;

	@Before
	public void setUp() {
		when(identifierTransformerProvider.forUriPart(ItemIdentifier.ITEM_ID).identifierToUri(ITEM_IDENTIFIER_PART)).thenReturn(ENCODED_ID);
	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenWishlistsContainingItemReturnNotFound() {
		when(wishlistRepository.findWishlistsContainingItem(ENCODED_ID)).thenReturn(Maybe.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.getElements(itemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnWishlistIdentifier() {
		when(wishlistRepository.findWishlistsContainingItem(ENCODED_ID)).thenReturn(Maybe.just(wishList));
		when(wishList.getStoreCode()).thenReturn(SCOPE);
		when(wishList.getGuid()).thenReturn(WISHLIST_ID);

		repository.getElements(itemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(wishlistIdentifier -> wishlistIdentifier.getWishlistId().getValue().equals(WISHLIST_ID));
	}
}
