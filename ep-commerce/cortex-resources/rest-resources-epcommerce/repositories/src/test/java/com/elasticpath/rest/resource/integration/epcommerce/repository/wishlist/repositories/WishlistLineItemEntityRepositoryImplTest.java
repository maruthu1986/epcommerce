/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.ITEM_ID_MAP;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.WISHLIST_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistTestFactory.buildWishlistLineItemIdentifier;

import com.google.common.collect.ImmutableList;
import io.reactivex.Completable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemEntity;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

/**
 * Test for {@link WishlistLineItemEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WishlistLineItemEntityRepositoryImplTest {

	private final WishlistLineItemIdentifier wishlistLineItemIdentifier = buildWishlistLineItemIdentifier();

	private final SubmitResult<WishlistLineItemIdentifier> submitResult = SubmitResult.<WishlistLineItemIdentifier>builder()
			.withIdentifier(wishlistLineItemIdentifier)
			.withStatus(SubmitStatus.CREATED)
			.build();

	@Mock
	private WishList wishList;

	@Mock
	private CartItemModifierField cartItemModifierField;

	@Mock
	private ShoppingItem shoppingItem;

	@Mock
	private Product product;

	@Mock
	private ProductSku productSku;

	private final AddToWishlistResult addToWishlistResult = new AddToWishlistResult(shoppingItem, true);

	@Mock
	private WishlistLineItemEntity entity;

	@InjectMocks
	private WishlistLineItemEntityRepositoryImpl<WishlistLineItemEntity, WishlistLineItemIdentifier> repository;

	@Mock
	private WishlistRepository wishlistRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private CartItemModifiersRepository cartItemModifiersRepository;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
	}

	@Test
	public void verifySubmitReturnNotFoundWhenDefaultWishlistIdNotFound() {
		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));
		when(entity.getItemId()).thenReturn(SKU_CODE);
		when(entity.getWishlistId()).thenReturn(WISHLIST_ID);

		repository.submit(entity, SCOPE_IDENTIFIER_PART)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifySubmitReturnNotFoundWhenWishlistNotFound() {
		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.just(WISHLIST_ID));
		when(entity.getItemId()).thenReturn(SKU_CODE);
		when(entity.getWishlistId()).thenReturn(WISHLIST_ID);
		when(wishlistRepository.addItemToWishlist(WISHLIST_ID, SCOPE, SKU_CODE))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.submit(entity, SCOPE_IDENTIFIER_PART)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifySubmitReturnSubmitResult() {
		when(wishlistRepository.getDefaultWishlistId(SCOPE)).thenReturn(Single.just(WISHLIST_ID));
		when(entity.getItemId()).thenReturn(SKU_CODE);
		when(entity.getWishlistId()).thenReturn(WISHLIST_ID);
		when(wishlistRepository.addItemToWishlist(WISHLIST_ID, SCOPE, SKU_CODE)).thenReturn(Single.just(addToWishlistResult));
		when(wishlistRepository.buildSubmitResult(SCOPE, WISHLIST_ID, addToWishlistResult)).thenReturn(Single.just(submitResult));

		repository.submit(entity, SCOPE_IDENTIFIER_PART)
				.test()
				.assertNoErrors()
				.assertValue(submitResult);
	}

	@Test
	public void verifyFindOneReturnsNotFoundWhenWishlistNotFound() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(wishlistLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsWhenShoppingItemNotFound() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishlistRepository.getShoppingItem(wishList, LINE_ITEM_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(wishlistLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsWhenProductSkuWhenNotFound() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishlistRepository.getShoppingItem(wishList, LINE_ITEM_ID)).thenReturn(Single.just(shoppingItem));
		when(wishlistRepository.getProductSku(wishList, LINE_ITEM_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(wishlistLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsNotFoundWhenItemIdNotFound() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishlistRepository.getShoppingItem(wishList, LINE_ITEM_ID)).thenReturn(Single.just(shoppingItem));
		when(wishlistRepository.getProductSku(wishList, LINE_ITEM_ID)).thenReturn(Single.just(productSku));
		when(itemRepository.getItemIdForSkuAsSingle(productSku)).thenReturn(Single.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.findOne(wishlistLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsWishlistLineItemEntityWithNoConfigurationsWhenShoppingItemFieldsAreNull() {
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishlistRepository.getShoppingItem(wishList, LINE_ITEM_ID)).thenReturn(Single.just(shoppingItem));
		when(wishlistRepository.getProductSku(wishList, LINE_ITEM_ID)).thenReturn(Single.just(productSku));
		when(itemRepository.getItemIdForSkuAsSingle(productSku)).thenReturn(Single.just(SKU_CODE));
		when(shoppingItem.getFields()).thenReturn(null);
		when(wishList.getGuid()).thenReturn(WISHLIST_ID);

		repository.findOne(wishlistLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(wishlistLineItemEntity -> wishlistLineItemEntity.getConfiguration().getDynamicProperties().isEmpty()
						&& wishlistLineItemEntity.getWishlistId().equals(WISHLIST_ID));
	}

	@Test
	public void verifyFindOneReturnsWishlistLineItemEntityWithConfigurationsWhenShoppingItemFieldsAreNotNull() {
		when(productSku.getProduct()).thenReturn(product);
		when(wishlistRepository.getWishlist(WISHLIST_ID)).thenReturn(Single.just(wishList));
		when(wishlistRepository.getShoppingItem(wishList, LINE_ITEM_ID)).thenReturn(Single.just(shoppingItem));
		when(wishlistRepository.getProductSku(wishList, LINE_ITEM_ID)).thenReturn(Single.just(productSku));
		when(itemRepository.getItemIdForSkuAsSingle(productSku)).thenReturn(Single.just(SKU_CODE));
		when(shoppingItem.getFields()).thenReturn(ITEM_ID_MAP);
		when(cartItemModifiersRepository.findCartItemModifiersByProduct(product)).thenReturn(ImmutableList.of(cartItemModifierField));
		when(cartItemModifierField.getCode()).thenReturn(ItemRepository.SKU_CODE_KEY);
		when(wishList.getGuid()).thenReturn(WISHLIST_ID);

		repository.findOne(wishlistLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(wishlistLineItemEntity ->
						wishlistLineItemEntity.getConfiguration().getDynamicProperties().get(ItemRepository.SKU_CODE_KEY).equals(SKU_CODE)
						&& wishlistLineItemEntity.getWishlistId().equals(WISHLIST_ID));
	}

	@Test
	public void verifyDeleteReturnsErrorWhenWishlistNotFound() {
		when(wishlistRepository.removeItemFromWishlist(WISHLIST_ID, LINE_ITEM_ID))
				.thenReturn(Completable.error(ResourceOperationFailure.notFound(NOT_FOUND)));

		repository.delete(wishlistLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyDeleteReturnsCompletable() {
		when(wishlistRepository.removeItemFromWishlist(WISHLIST_ID, LINE_ITEM_ID)).thenReturn(Completable.complete());

		repository.delete(wishlistLineItemIdentifier)
				.test()
				.assertComplete();
	}
}
