/**
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder;

import io.reactivex.Single;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with the pricing snapshots.
 */
public interface PricingSnapshotRepository {

	/**
	 * Gets the cart pricing snapshot.
	 *
	 * @param shoppingCart the shopping cart
	 * @return the cart's pricing snapshot
	 * @deprecated use {@link #getShoppingCartPricingSnapshotSingle} instead
	 */
	@Deprecated
	ExecutionResult<ShoppingCartPricingSnapshot> getShoppingCartPricingSnapshot(ShoppingCart shoppingCart);

	/**
	 * Gets the cart pricing snapshot.
	 *
	 * @param shoppingCart the shopping cart
	 * @return the cart's pricing snapshot
	 */
	Single<ShoppingCartPricingSnapshot> getShoppingCartPricingSnapshotSingle(ShoppingCart shoppingCart);

	/**
	 * Gets the item pricing snapshot for the given item.
	 *
	 * @param orderSku    the order sku
	 * @return the pricing snapshot for the given item
	 */
	Single<ShoppingItemPricingSnapshot> getPricingSnapshotForOrderSku(OrderSku orderSku);

	/**
	 * Gets the tax pricing snapshot.
	 *
	 * @param shoppingCart the shopping cart
	 * @return the tax snapshot
	 */
	Single<ShoppingCartTaxSnapshot> getShoppingCartTaxSnapshot(ShoppingCart shoppingCart);

	/**
	 * Gets the tax pricing snapshot for the given item.
	 *
	 * @param orderSku the order sku
	 * @return the tax snapshot for the given item
	 */
	Single<ShoppingItemTaxSnapshot> getTaxSnapshotForOrderSku(OrderSku orderSku);
}
