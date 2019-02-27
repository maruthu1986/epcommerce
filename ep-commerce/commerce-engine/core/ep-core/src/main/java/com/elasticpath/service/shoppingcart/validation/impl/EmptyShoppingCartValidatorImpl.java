/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidator;

/**
 * Validates that shopping is not empty.
 */
public class EmptyShoppingCartValidatorImpl implements ShoppingCartValidator {

	private static final String MESSAGE_ID = "cart.empty";

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingCartValidationContext context) {
		if (context.getShoppingCart().isEmpty()) {
			StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, MESSAGE_ID,
					"Shopping cart is empty.", Collections.emptyMap());
			return Collections.singletonList(errorMessage);
		}
		return Collections.emptyList();
	}

}
