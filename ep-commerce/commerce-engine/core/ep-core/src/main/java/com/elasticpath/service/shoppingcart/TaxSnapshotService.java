/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;

/**
 * Service methods for dealing with a tax snapshot.
 */
public interface TaxSnapshotService {

	/**
	 * Calculate an immutable tax snapshot for the given cart.
	 *
	 * @param shoppingCart the cart to examine
	 * @param cartPricingSnapshot the pricing snapshot corresponding to the shopping cart
	 * @return a snapshot of the tax
	 */
	ShoppingCartTaxSnapshot getTaxSnapshotForCart(ShoppingCart shoppingCart, ShoppingCartPricingSnapshot cartPricingSnapshot);

	/**
	 * Calculate an immutable tax snapshot for the given order SKU.
	 *
	 * @param orderSku the order SKU to examine
	 * @param cartPricingSnapshot a snapshot of the cart pricing
	 * @return a snapshot of the item tax
	 */
	ShoppingItemTaxSnapshot getTaxSnapshotForOrderSku(OrderSku orderSku, ShoppingCartPricingSnapshot cartPricingSnapshot);

	/**
	 *
	 * @param orderSku the order SKU to examine
	 * @param itemPricingSnapshot a snapshot of the item pricing
	 * @return a snapshot of the item tax
	 */
	ShoppingItemTaxSnapshot getTaxSnapshotForOrderSku(OrderSku orderSku, ShoppingItemPricingSnapshot itemPricingSnapshot);

}
