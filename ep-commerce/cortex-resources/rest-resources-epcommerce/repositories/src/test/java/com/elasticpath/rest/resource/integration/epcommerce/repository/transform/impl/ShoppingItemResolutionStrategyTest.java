package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Maybe;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingItemResolutionStrategyTest {
	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String PRODUCT_GUID = "PRODUCT_GUID";
	private static final String SHOPPING_ITEM_GUID = "SHOPPING_ITEM_GUID";
	private static final String MESSAGE_ID = "ANYTHING";

	@InjectMocks
	private ShoppingItemResolutionStrategy shoppingItemResolutionStrategy;

	@Mock
	private Store store;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingItem shoppingItem;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Test
	public void testGetResourceIdentifier() {
		// Given
		when(store.getCode()).thenReturn(STORE_CODE);
		when(shoppingItem.getGuid()).thenReturn(SHOPPING_ITEM_GUID);
		when(shoppingCart.getGuid()).thenReturn(SHOPPING_CART_GUID);
		when(shoppingCart.getCartItemByGuid(SHOPPING_ITEM_GUID)).thenReturn(shoppingItem);
		when(shoppingCart.getStore()).thenReturn(store);
		when(shoppingCartRepository.getShoppingCart(SHOPPING_CART_GUID)).thenReturn(Single.just(shoppingCart));

		// When
		final StructuredErrorResolution structuredErrorMessageResolution = new StructuredErrorResolution(ShoppingItem.class, SHOPPING_ITEM_GUID);
		final StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR, MESSAGE_ID, "",
				new HashMap<>(), structuredErrorMessageResolution);
		Maybe<ResourceIdentifier> resourceIdentifierMaybe =
				shoppingItemResolutionStrategy.getResourceIdentifier(structuredErrorMessage, SHOPPING_CART_GUID);

		// Then
		ResourceIdentifier resourceIdentifier = resourceIdentifierMaybe.blockingGet();
		assertTrue("Resource identifier should be a CartLineItemIdentifier", resourceIdentifier instanceof LineItemIdentifier);
		LineItemIdentifier lineItemIdentifier = (LineItemIdentifier) resourceIdentifier;
		assertEquals(STORE_CODE, lineItemIdentifier.getLineItems().getCart().getScope().getValue());
		assertEquals(SHOPPING_ITEM_GUID, lineItemIdentifier.getLineItemId().getValue());
		assertEquals(SHOPPING_CART_GUID, lineItemIdentifier.getLineItems().getCart().getCartId().getValue());
	}

	@Test
	public void testGetResourceIdentifierWithInvalidMessageId() {
		// When
		final StructuredErrorResolution structuredErrorMessageResolution = new StructuredErrorResolution(Product.class, PRODUCT_GUID);
		final StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR, MESSAGE_ID, "",
				new HashMap<>(), structuredErrorMessageResolution);
		Maybe<ResourceIdentifier> resourceIdentifierMaybe =
				shoppingItemResolutionStrategy.getResourceIdentifier(structuredErrorMessage, SHOPPING_CART_GUID);

		// Then
		assertTrue(resourceIdentifierMaybe.isEmpty().blockingGet());
	}
}