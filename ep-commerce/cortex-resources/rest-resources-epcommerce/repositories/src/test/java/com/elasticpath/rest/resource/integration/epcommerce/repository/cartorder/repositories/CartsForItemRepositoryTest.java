/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_GUID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Test for {@link CartsForItemRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartsForItemRepositoryTest {

	public static final String LINE_ITEM_SKU_CODE = "LINE_ITEM_SKU_CODE_1234";
	@InjectMocks
	private CartsForItemRepository<ItemIdentifier, CartIdentifier> repository;
	@Mock
	private ShoppingCart cart;
	@Mock
	private ProductSku productSku;
	@Mock
	private ShoppingItem shoppingItem;
	@Mock
	private Store store;
	@Mock
	private IdentifierTransformerProvider identifierTransformerProvider;
	@Mock
	private IdentifierTransformer<IdentifierPart<Map<String, String>>> identifierTransformer;
	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private ItemRepository itemRepository;

	@Test
	public void getElementsWhenItemInCart() {
		ItemIdentifier itemIdentifier = IdentifierTestFactory.buildItemIdentifier(SCOPE, SKU_CODE);

		setupMocksForGetElements(itemIdentifier);

		when(productSku.getSkuCode()).thenReturn(LINE_ITEM_SKU_CODE);
		when(cart.getCartItems(LINE_ITEM_SKU_CODE)).thenReturn(Collections.singletonList(shoppingItem));

		final Observable<CartIdentifier> elements = repository.getElements(itemIdentifier);

		elements.test()
				.assertNoErrors()
				.assertValueCount(1);
	}

	@Test
	public void getElementsWhenItemNotInCart() {
		ItemIdentifier itemIdentifier = IdentifierTestFactory.buildItemIdentifier(SCOPE, SKU_CODE);
		setupMocksForGetElements(itemIdentifier);

		repository.getElements(itemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	private void setupMocksForGetElements(final ItemIdentifier itemIdentifier) {
		String encodedItemId = "encodedItemId";
		when(identifierTransformerProvider.<IdentifierPart<Map<String, String>>>forUriPart(ItemIdentifier.ITEM_ID)).thenReturn(identifierTransformer);
		when(identifierTransformer.identifierToUri(itemIdentifier.getItemId())).thenReturn(encodedItemId);
		when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(Single.just(cart));
		when(itemRepository.getSkuForItemIdAsSingle(encodedItemId)).thenReturn(Single.just(productSku));

		when(cart.getStore()).thenReturn(store);
		when(store.getCode()).thenReturn(SCOPE);
		when(cart.getGuid()).thenReturn(CART_GUID);
	}

	@Test
	public void getCartContainingProductSkuWhenCartFound() {
		when(productSku.getSkuCode()).thenReturn(LINE_ITEM_SKU_CODE);
		when(cart.getCartItems(LINE_ITEM_SKU_CODE)).thenReturn(Collections.singletonList(shoppingItem));
		when(cart.getStore()).thenReturn(store);
		when(store.getCode()).thenReturn(SCOPE);
		when(cart.getGuid()).thenReturn(CART_GUID);

		repository.getCartContainingProductSku(cart, productSku)
				.test()
				.assertNoErrors()
				.assertValueCount(1);
	}

	@Test
	public void getCartContainingProductSkuWhenCartNull() {
		repository.getCartContainingProductSku(null, productSku)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	@Test
	public void getCartContainingProductSkuWhenProductSkuNull() {
		repository.getCartContainingProductSku(cart, null)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	@Test
	public void getCartContainingProductSkuWhenCartDoesNotContainProductSku() {
		repository.getCartContainingProductSku(cart, productSku)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

}