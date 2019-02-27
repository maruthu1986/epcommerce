/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartRefresher;

@RunWith(MockitoJUnitRunner.class)
public class CartPostProcessorTest {

	@Mock(answer = RETURNS_SMART_NULLS)
	private ShoppingCart cart;

	@Mock(answer = RETURNS_SMART_NULLS)
	private CartOrderService cartOrderService;

	@Mock(answer = RETURNS_SMART_NULLS)
	private CustomerSession customerSession;

	@Mock(answer = RETURNS_SMART_NULLS)
	private Shopper shopper;

	@Mock(answer = RETURNS_DEEP_STUBS)
	private ShoppingCartRefresher shoppingCartRefresher;

	@InjectMocks
	private CartPostProcessorImpl cartPostProcessor;

	@Test
	public void testPostProcessCartWhenCartIsNull() {

		cartPostProcessor.postProcessCart(null, shopper, customerSession);

		verify(shoppingCartRefresher, never()).refresh(any(ShoppingCart.class));
		verify(cartOrderService, never()).createOrderIfPossible(null);
	}

	@Test
	public void shouldUpdateShopperPriorToProcessing() {

		cartPostProcessor.postProcessCart(cart, shopper, customerSession);

		verify(shopper, atLeastOnce()).updateTransientDataWith(customerSession);
		verify(shopper, atLeastOnce()).setCurrentShoppingCart(cart);
	}

	@Test
	public void shouldCreateIfNotExists() {

		cartPostProcessor.postProcessCart(cart, shopper, customerSession);

		verify(cartOrderService).createOrderIfPossible(cart);
	}

	@Test
	public void shouldNotCreateIfOrderExistsAndExceptionIsThrown() throws Exception {

		when(cartOrderService.createOrderIfPossible(cart))
				.thenThrow(new DataIntegrityViolationException("Cart order exists"));

		cartPostProcessor.postProcessCart(cart, shopper, customerSession);

		verify(cart).getGuid();

	}

}
