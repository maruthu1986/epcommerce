/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

@RunWith(MockitoJUnitRunner.class)
public class CartToOrderLinkRepositoryImplTest {

	private static final String CART_WAS_NOT_FOUND = "No cart was found with GUID = ";
	private static final String SCOPE = "some store";
	private static final String CART_ID = "cart id";

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@InjectMocks
	private CartToOrderLinkRepositoryImpl<CartIdentifier, OrderIdentifier> repository;

	@Test
	public void cartDoesNotExistTest() {
		when(shoppingCartRepository.verifyShoppingCartExistsForStore(CART_ID, SCOPE)).thenReturn(Single.just(false));

		repository.getOrderByCartId(SCOPE, CART_ID)
				.test()
				.assertError(ResourceOperationFailure.notFound(CART_WAS_NOT_FOUND + CART_ID));
	}

	@Test
	public void cartExistTest() {
		when(shoppingCartRepository.verifyShoppingCartExistsForStore(CART_ID, SCOPE)).thenReturn(Single.just(true));

		repository.getOrderByCartId(SCOPE, CART_ID)
				.test();

		verify(cartOrderRepository, times(1)).findByCartGuidSingle(CART_ID);
	}
}
