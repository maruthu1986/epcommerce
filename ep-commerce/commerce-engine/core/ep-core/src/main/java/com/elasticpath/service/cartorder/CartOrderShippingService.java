/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.cartorder;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * This Class can perform services for CartOrders related to shipping, 
 * CartOrder should not be used in versions of EP prior to 6.4.
 */
public interface CartOrderShippingService {

	/**
	 * Update the shipping address on the cart order. This may also update the shipping option on the cart order.
	 *
	 * @param shippingAddressGuid shipping address guid to update on cart
	 * @param shoppingCart the shopping cart to read from
	 * @param cartOrder cart order to update
	 * @return true if cart order shipping address is updated, false otherwise.
	 */
	Boolean updateCartOrderShippingAddress(String shippingAddressGuid, ShoppingCart shoppingCart, CartOrder cartOrder);
	
	/**
	 * Populates the transient fields on the shopping cart using the information in the cart order.
	 *
	 * @param shoppingCart the shopping cart
	 * @param cartOrder the cart order
	 * @return the same shopping cart, now populated with the transient fields.
	 * @deprecated use populateAddressAndShippingFields instead as this method will call ShoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices()
	 */
	@Deprecated
	ShoppingCart populateShoppingCartTransientFields(ShoppingCart shoppingCart, CartOrder cartOrder);

	/**
	 * Populates the transient address and shipping fields on the shopping cart using the information in the cart order.
	 * <p/>
	 * Does not call ShoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices()
	 *
	 * @param shoppingCart the shopping cart
	 * @param cartOrder the cart order
	 * @return the same shopping cart, now populated with the transient fields.
	 */
	ShoppingCart populateAddressAndShippingFields(ShoppingCart shoppingCart, CartOrder cartOrder);
}
