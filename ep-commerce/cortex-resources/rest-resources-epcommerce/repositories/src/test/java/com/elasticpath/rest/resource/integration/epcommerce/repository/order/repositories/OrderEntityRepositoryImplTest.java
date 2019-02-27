/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.cartorder.CartOrderService;

@RunWith(MockitoJUnitRunner.class)
public class OrderEntityRepositoryImplTest {

	private static final String SCOPE = "some store";
	private static final String USER_ID = "user id";
	private static final String CART_GUID = "cart guid";

	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private CartOrderService cartOrderService;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@InjectMocks
	private OrderEntityRepositoryImpl<OrderEntity, OrderIdentifier> repository;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
	}

	@Test
	public void findOrdersForStore() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);

		CartOrder cartOrder = mock(CartOrder.class);
		when(cartOrder.getGuid()).thenReturn(CART_GUID);

		when(shoppingCartRepository.findAllCarts(USER_ID, SCOPE)).thenReturn(Observable.just(CART_GUID));
		when(cartOrderService.findByShoppingCartGuid(CART_GUID)).thenReturn(cartOrder);

		repository.findAll(StringIdentifier.of(SCOPE))
				.test()
				.assertNoErrors()
				.assertValue(orderIdentifier -> CART_GUID.equals(orderIdentifier.getOrderId().getValue())
						&& SCOPE.equals(orderIdentifier.getScope().getValue()));
	}
}
