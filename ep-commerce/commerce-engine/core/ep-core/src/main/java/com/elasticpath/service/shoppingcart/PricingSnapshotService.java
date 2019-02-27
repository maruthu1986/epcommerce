/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;

/**
 * Service methods for dealing with a pricing snapshot.
 */
public interface PricingSnapshotService {

	/**
	 * Calculate an immutable pricing snapshot for the given cart.
	 *
	 * @param shoppingCart the cart to examine
	 * @return a snapshot of the pricing
	 */
	ShoppingCartPricingSnapshot getPricingSnapshotForCart(ShoppingCart shoppingCart);

	/**
	 * Calculate an immutable pricing snapshot for the given order SKU.
	 *
	 * @param orderSku the order SKU to examine
	 * @return a snapshot of the pricing
	 */
	ShoppingItemPricingSnapshot getPricingSnapshotForOrderSku(OrderSku orderSku);

}
