/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.USER_ID;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

/**
 * Test for {@link CartEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartEntityRepositoryImplTest {

	@InjectMocks
	private CartEntityRepositoryImpl<CartEntity, CartIdentifier> repository;
	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private ConversionService conversionService;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private ShoppingCart cart;

	@Test
	public void findOne() {
		CartEntity cartEntity = mock(CartEntity.class);
		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(cart));
		when(conversionService.convert(cart, CartEntity.class)).thenReturn(cartEntity);

		repository.findOne(IdentifierTestFactory.buildCartIdentifier(SCOPE, CART_ID))
				.test()
				.assertNoErrors();
	}

	@Test
	public void findAll() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(shoppingCartRepository.findAllCarts(any(), any()))
				.thenReturn(Observable.just(CART_ID));

		repository.findAll(SCOPE_IDENTIFIER_PART)
				.test()
				.assertNoErrors()
				.assertValue(identifier -> identifier.getCartId().getValue().equals(CART_ID));
	}

}