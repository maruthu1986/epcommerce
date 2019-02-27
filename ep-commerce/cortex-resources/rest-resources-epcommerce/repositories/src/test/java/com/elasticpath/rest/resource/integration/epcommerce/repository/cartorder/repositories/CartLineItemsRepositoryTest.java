/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;

import java.util.Collections;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.LineItemIdentifierRepository;

/**
 * Test for {@link CartLineItemsRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartLineItemsRepositoryTest {

	@InjectMocks
	private CartLineItemsRepository<CartIdentifier, LineItemIdentifier> repository;
	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private LineItemIdentifierRepository lineItemIdentifierRepository;
	@Mock
	private ShoppingCart cart;

	@Test
	public void getMultipleElements() {
		ShoppingItem shoppingItem1 = mock(ShoppingItem.class);
		ShoppingItem shoppingItem2 = mock(ShoppingItem.class);

		final LineItemIdentifier lineItemIdentifier1 = mock(LineItemIdentifier.class);
		final LineItemIdentifier lineItemIdentifier2 = mock(LineItemIdentifier.class);

		final CartIdentifier cartIdentifier = IdentifierTestFactory.buildCartIdentifier(SCOPE, CART_ID);

		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(cart));
		doReturn(ImmutableList.of(shoppingItem1, shoppingItem2)).when(cart).getRootShoppingItems();


		when(lineItemIdentifierRepository.buildLineItemIdentifier(cartIdentifier, shoppingItem1))
				.thenReturn(lineItemIdentifier1);

		when(lineItemIdentifierRepository.buildLineItemIdentifier(cartIdentifier, shoppingItem2))
				.thenReturn(lineItemIdentifier2);

		repository.getElements(cartIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(2)
				.assertValueAt(0, lineItemIdentifier -> lineItemIdentifier.equals(lineItemIdentifier1))
				.assertValueAt(1, lineItemIdentifier -> lineItemIdentifier.equals(lineItemIdentifier2));
	}

	@Test
	public void getNoElements() {
		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(cart));
		when(cart.getRootShoppingItems()).thenReturn(Collections.emptyList());

		repository.getElements(IdentifierTestFactory.buildCartIdentifier(SCOPE, CART_ID))
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

}