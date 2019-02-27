/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Service for validating a shopping cart before a purchase.
 */
public interface PurchaseCartValidationService extends Validator<ShoppingCartValidationContext> {

	/**
	 * Builds validation context.
	 *
	 * @param shoppingCart shopping cart
	 * @param cartOrder cart order
	 * @return validation context
	 */
	ShoppingCartValidationContext buildContext(ShoppingCart shoppingCart, CartOrder cartOrder);

	/**
	 * Builds validation context.
	 *
	 * @param shoppingCart shopping cart
	 * @return validation context
	 */
	ShoppingCartValidationContext buildContext(ShoppingCart shoppingCart);
}
