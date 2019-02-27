/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutAction;
import com.elasticpath.service.shoppingcart.actions.FinalizeCheckoutActionContext;

/**
 * CheckoutAction to remove all items from the shoppingCart after converting the cart to an order. It alse removes 
 * the cart order.
 */
public class ClearShoppingCartCheckoutAction implements FinalizeCheckoutAction {

	private ShoppingCartService shoppingCartService;
	
	private CartOrderService cartOrderService;

	@Override
	public void execute(final FinalizeCheckoutActionContext context) throws EpSystemException {
		ShoppingCart oldShoppingCart = context.getShoppingCart();
		oldShoppingCart.deactivateCart();
		if (!context.isOrderExchange()) {
			//disconnect the old cart from the shopper and customer's session
			shoppingCartService.disconnectCartFromShopperAndCustomerSession(oldShoppingCart, context);

		}
		//free some memory - doesn't incur new db calls because the cart is not saved nor referenced anywhere else
		oldShoppingCart.clearItems();

		//physical deletion of CartOrder instance associated with the old cart
		cartOrderService.removeIfExistsByShoppingCart(oldShoppingCart);
	}
	
	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	public void setCartOrderService(final CartOrderService cartOrderService) {
		this.cartOrderService = cartOrderService;
	}

	protected CartOrderService getCartOrderService() {
		return cartOrderService;
	}
}
