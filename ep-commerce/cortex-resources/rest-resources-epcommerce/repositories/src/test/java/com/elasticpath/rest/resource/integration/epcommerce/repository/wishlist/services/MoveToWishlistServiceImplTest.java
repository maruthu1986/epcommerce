/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildLineItemIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.WISHLIST_ID;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

/**
 * Test for {@link MoveToWishlistServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MoveToWishlistServiceImplTest {

	private final LineItemIdentifier lineItemIdentifier = buildLineItemIdentifier(SCOPE, CART_ID, LINE_ITEM_ID);

	@Mock
	private ShoppingItem shoppingItem;

	private final AddToWishlistResult addToWishlistResult = new AddToWishlistResult(shoppingItem, true);

	@Mock
	private ShoppingCart shoppingCart;

	@InjectMocks
	private MoveToWishlistServiceImpl service;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private WishlistRepository wishlistRepository;

	@Test
	public void verifyMoveReturnNotFoundWhenDefaultWishlistNotFound() {
		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		service.move(lineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyMoveReturnNotFoundWhenShoppingCartNotFound() {
		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.just(WISHLIST_ID));
		when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		service.move(lineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyMoveReturnNotFoundWhenAddToWishlistFails() {
		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.just(WISHLIST_ID));
		when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(Single.just(shoppingCart));
		when(shoppingCartRepository.moveItemToWishlist(shoppingCart, LINE_ITEM_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		service.move(lineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyMoveReturnNotFoundWhenSubmitResultNotFound() {
		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.just(WISHLIST_ID));
		when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(Single.just(shoppingCart));
		when(shoppingCartRepository.moveItemToWishlist(shoppingCart, LINE_ITEM_ID))
				.thenReturn(Single.just(addToWishlistResult));
		when(wishlistRepository.buildSubmitResult(SCOPE, WISHLIST_ID, addToWishlistResult))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		service.move(lineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyMoveReturnSubmitResult() {
		SubmitResult<WishlistLineItemIdentifier> submitResult = SubmitResult.<WishlistLineItemIdentifier>builder()
				.withStatus(SubmitStatus.CREATED)
				.build();

		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.just(WISHLIST_ID));
		when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(Single.just(shoppingCart));
		when(shoppingCartRepository.moveItemToWishlist(shoppingCart, LINE_ITEM_ID))
				.thenReturn(Single.just(addToWishlistResult));
		when(wishlistRepository.buildSubmitResult(SCOPE, WISHLIST_ID, addToWishlistResult))
				.thenReturn(Single.just(submitResult));

		service.move(lineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(submitResult);
	}
}
