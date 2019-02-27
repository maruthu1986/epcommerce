/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;

/**
 * Test for {@link ClearShoppingCartCheckoutAction}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClearShoppingCartCheckoutActionTest {

	@Mock
	private CartOrderService cartOrderService;
	@Mock
	private ShoppingCartService shoppingCartService;
	@Mock
	private ShoppingCart shoppingCart;

	@InjectMocks
	private ClearShoppingCartCheckoutAction fixture;

	private FinalizeCheckoutActionContext checkoutContext;

	/**
	 * Test checkout action on order exchange.
	 */
	@Test
	public void testCheckoutActionOnOrderExchange() {
		shouldHaveCheckoutContextAsOrderExchange();

		fixture.execute(checkoutContext);

		verify(shoppingCart).deactivateCart();
		verify(cartOrderService).removeIfExistsByShoppingCart(shoppingCart);
		verify(shoppingCartService, never()).saveOrUpdate(shoppingCart);

	}

	/**
	 * Test basic checkout action.
	 */
	@Test
	public void testDefaultCheckoutAction() {
		shouldHaveDefaultCheckoutContext();

		fixture.execute(checkoutContext);

		verify(shoppingCart).deactivateCart();
		verify(shoppingCartService).disconnectCartFromShopperAndCustomerSession(shoppingCart, checkoutContext);
		verify(cartOrderService).removeIfExistsByShoppingCart(shoppingCart);
	}
	
	
	private void shouldHaveCheckoutContextAsOrderExchange() {
		createCheckoutContext(true);
	}

	private void shouldHaveDefaultCheckoutContext() {
		createCheckoutContext(false);
	}

	private void createCheckoutContext(final boolean isOrderExchange) {
		CheckoutActionContext checkoutActionContext = new CheckoutActionContextImpl(shoppingCart,
																					null,
																					null, null, isOrderExchange, false, null);
		checkoutContext = new FinalizeCheckoutActionContextImpl(checkoutActionContext);
	}
}
