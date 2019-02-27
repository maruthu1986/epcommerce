package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;

@RunWith(MockitoJUnitRunner.class)
public class CartOrderCacheKeyVariantsTest {
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";

	@Mock
	private CartOrder cartOrder;

	private final CartOrderCacheKeyVariants cartOrderCacheKeyVariants = new CartOrderCacheKeyVariants();

	@Test
	public void get() {
		// Given
		when(cartOrder.getGuid()).thenReturn(CART_ORDER_GUID);
		when(cartOrder.getShoppingCartGuid()).thenReturn(SHOPPING_CART_GUID);

		// When
		Collection<Object[]> results = cartOrderCacheKeyVariants.get(cartOrder);

		// Then
		assertEquals(2, results.size());
		Iterator<Object[]> iterator = results.iterator();
		Object[] result1 = iterator.next();
		Object[] result2 = iterator.next();
		assertEquals(CART_ORDER_GUID, result1[0]);
		assertEquals(SHOPPING_CART_GUID, result2[0]);
	}

	@Test
	public void getType() {
		assertEquals(CartOrder.class, cartOrderCacheKeyVariants.getType());
	}
}