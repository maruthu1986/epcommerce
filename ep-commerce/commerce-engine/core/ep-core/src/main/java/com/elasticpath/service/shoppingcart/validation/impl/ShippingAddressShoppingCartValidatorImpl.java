/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidator;

/**
 * Determines if a valid shipping address has been specified (if cart contains physical SKUs).
 */
public class ShippingAddressShoppingCartValidatorImpl implements ShoppingCartValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "need.shipping.address";

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingCartValidationContext context) {
		final ShoppingCart shoppingCart = context.getShoppingCart();

		if (shoppingCart.requiresShipping() && shoppingCart.getShippingAddress() == null) {
			StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, MESSAGE_ID,
					"Shipping address must be specified.", Collections.emptyMap(),
					new StructuredErrorResolution(ShoppingCart.class, shoppingCart.getGuid()));
			return Collections.singletonList(errorMessage);
		}

		return Collections.emptyList();
	}

}
