/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.shopper.impl;

import java.util.Collection;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;

/**
 * Handles what happens to the {@link CartOrder} coupons when a {@link Shopper} is switched out.
 */
public final class CartOrderCouponsMergerForShopperUpdates implements CustomerSessionShopperUpdateHandler {

	private final CartOrderService cartOrderService;

	/**
	 * @param cartOrderService cartOrderService service
	 */
	public CartOrderCouponsMergerForShopperUpdates(final CartOrderService cartOrderService) {
		this.cartOrderService = cartOrderService;
	}

	/**
	 * Copies the Coupons from previous CartOrder to new CartOrder when shopper changes, if the transition is from anonymous to registered.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void invalidateShopper(final CustomerSession customerSession, final Shopper invalidShopper) {

		final ShoppingCart anonymousCart = invalidShopper.getCurrentShoppingCart();
		final Collection<String> anonymousCartOrderCouponCodes = cartOrderService.getCartOrderCouponCodesByShoppingCartGuid(anonymousCart.getGuid());
		if (anonymousCartOrderCouponCodes.isEmpty()) {
			return;
		}

		final Shopper currentShopper = customerSession.getShopper();
		final ShoppingCart currentShopperCart = currentShopper.getCurrentShoppingCart();

		cartOrderService.createOrderIfPossible(currentShopperCart);

		final CartOrder currentCartOrder = cartOrderService.findByShoppingCartGuid(currentShopperCart.getGuid());
		currentCartOrder.addCoupons(anonymousCartOrderCouponCodes);

		cartOrderService.saveOrUpdate(currentCartOrder);
	}
}
