/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SKU_CODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.LineItemIdentifierRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Test for {@link LineItemEntityRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemEntityRepositoryTest {

	@InjectMocks
	private LineItemEntityRepository<LineItemEntity, LineItemIdentifier> repository;
	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private LineItemEntity lineItemEntity;
	@Mock
	private ShoppingCart cart;
	@Mock
	private ShoppingItem shoppingItem;
	@Mock
	private ProductSku productSku;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ProductSkuRepository productSkuRepository;
	@Mock
	private CartItemModifiersRepository cartItemModifiersRepository;
	@Mock
	private LineItemIdentifierRepository lineItemIdentifierRepository;

	private static final String SHOPPING_ITEM_SKU_GUID = "shoppingItemSkuGuid";
	private static final String SHOPPING_ITEM_GUID = "shoppingItemGuid";
	private static final String ENCODED_ITEM_ID = "encodedItemId";

	@Test
	public void successfullyFindOneItem() {
		setupMocksForFindOne(LINE_ITEM_ID, ENCODED_ITEM_ID);
		when(shoppingItem.getFields()).thenReturn(null);

		LineItemIdentifier lineItemIdentifier = IdentifierTestFactory.buildLineItemIdentifier(SCOPE, CART_ID, LINE_ITEM_ID);
		repository.findOne(lineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(1)
				.assertValue(entity -> entity.getItemId().equals(ENCODED_ITEM_ID)
						&& entity.getConfiguration().getDynamicProperties().isEmpty());
	}

	@Test
	public void successfullyFindOneConfigurableItem() {
		setupMocksForFindOne(LINE_ITEM_ID, ENCODED_ITEM_ID);
		String value = "value";
		when(shoppingItem.getFields()).thenReturn(Collections.singletonMap("key", value));
		CartItemModifierField cartItemModifierField = mock(CartItemModifierField.class);
		when(cartItemModifierField.getCode()).thenReturn("code");
		when(cartItemModifiersRepository.findCartItemModifierValues(CART_ID, LINE_ITEM_ID))
				.thenReturn(Single.just(Collections.singletonMap(cartItemModifierField, value)));

		LineItemIdentifier lineItemIdentifier = IdentifierTestFactory.buildLineItemIdentifier(SCOPE, CART_ID, LINE_ITEM_ID);
		repository.findOne(lineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(1)
				.assertValue(entity -> entity.getItemId().equals(ENCODED_ITEM_ID)
						&& entity.getConfiguration().getDynamicProperties().size() == 1);
	}

	private void setupMocksForFindOne(final String lineItemId, final String encodedItemId) {
		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(cart));
		when(shoppingCartRepository.getShoppingItem(lineItemId, cart)).thenReturn(Single.just(shoppingItem));
		when(shoppingItem.getSkuGuid()).thenReturn(SHOPPING_ITEM_SKU_GUID);
		when(productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(SHOPPING_ITEM_SKU_GUID)).thenReturn(Single.just(productSku));
		when(itemRepository.getItemIdForSkuAsSingle(productSku)).thenReturn(Single.just(encodedItemId));
		when(shoppingItem.getGuid()).thenReturn(SHOPPING_ITEM_GUID);
		when(shoppingItem.getQuantity()).thenReturn(1);
	}

	@Test
	public void successfullyAddNewItemToCart() {
		final LineItemIdentifier expectedLineItemIdentifier = mock(LineItemIdentifier.class);

		LineItemEntity lineItemEntity = setupMocksForAddToCart();
		when(shoppingItem.getQuantity()).thenReturn(1);
		when(lineItemIdentifierRepository.buildLineItemIdentifier(cart, shoppingItem))
				.thenReturn(expectedLineItemIdentifier);

		repository.addToCart(lineItemEntity)
				.test()
				.assertNoErrors()
				.assertValue(submitResult -> submitResult.getStatus().equals(SubmitStatus.CREATED))
				.assertValue(submitResult -> submitResult.getIdentifier().equals(expectedLineItemIdentifier));
	}

	@Test
	public void successfullyAddExistingItemToCart() {
		LineItemEntity lineItemEntity = setupMocksForAddToCart();
		when(shoppingItem.getQuantity()).thenReturn(2);

		repository.addToCart(lineItemEntity)
				.test()
				.assertNoErrors()
				.assertValue(submitResult -> submitResult.getStatus().equals(SubmitStatus.UPDATED));
	}

	private LineItemEntity setupMocksForAddToCart() {
		int quantity = 1;
		LineItemEntity lineItemEntity = mock(LineItemEntity.class);
		when(lineItemEntity.getItemId()).thenReturn(ENCODED_ITEM_ID);
		when(lineItemEntity.getQuantity()).thenReturn(quantity);
		when(lineItemEntity.getCartId()).thenReturn(CART_ID);

		LineItemConfigurationEntity lineItemConfigurationEntity = mock(LineItemConfigurationEntity.class);
		when(lineItemEntity.getConfiguration()).thenReturn(lineItemConfigurationEntity);
		Map<String, String> fields = Collections.emptyMap();
		when(lineItemConfigurationEntity.getDynamicProperties()).thenReturn(fields);

		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(cart));
		when(shoppingCartRepository.addItemToCart(cart, ENCODED_ITEM_ID, quantity, fields)).thenReturn(Single.just(shoppingItem));
		when(shoppingItem.getQuantity()).thenReturn(quantity);
		return lineItemEntity;
	}

	@Test
	public void performUpdateWhenQuantityZero() {

		when(lineItemEntity.getQuantity()).thenReturn(0);
		when(shoppingItem.getGuid()).thenReturn(SHOPPING_ITEM_SKU_GUID);
		when(shoppingCartRepository.removeItemFromCart(cart, SHOPPING_ITEM_SKU_GUID)).thenReturn(Completable.complete());

		repository.performUpdate(lineItemEntity, cart, shoppingItem, SKU_CODE)
				.test()
				.assertComplete();

		verify(shoppingCartRepository).removeItemFromCart(cart, SHOPPING_ITEM_SKU_GUID);
	}

	@Test
	public void performUpdateWhenQuantityPositive() {
		when(lineItemEntity.getQuantity()).thenReturn(1);
		when(repository.updateItemInCart(lineItemEntity, cart, shoppingItem, SKU_CODE)).thenReturn(Completable.complete());

		repository.performUpdate(lineItemEntity, cart, shoppingItem, SKU_CODE)
				.test()
				.assertComplete();
	}

}