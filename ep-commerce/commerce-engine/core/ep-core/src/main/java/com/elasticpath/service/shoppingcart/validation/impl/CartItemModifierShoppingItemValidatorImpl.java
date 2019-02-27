/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Set;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidator;

/**
 * Validator to check that the cart modifiers are correct for a ShoppingItem.
 */
public class CartItemModifierShoppingItemValidatorImpl extends CartModifierValidator implements ShoppingItemValidator {

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingItemValidationContext context) {
		ProductSku productSku = context.getProductSku();

		Set<CartItemModifierGroup> cartItemModifierGroups = productSku.getProduct().getProductType().getCartItemModifierGroups();
		return baseValidate(context.getShoppingItem().getFields(), cartItemModifierGroups);
	}

}
