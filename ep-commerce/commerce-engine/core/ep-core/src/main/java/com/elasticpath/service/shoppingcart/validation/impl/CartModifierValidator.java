/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.validation.service.CartItemModifierFieldValidationService;

/**
 * Base class for CartItemModifier*Validator classes.
 */
public class CartModifierValidator {
	private CartItemModifierFieldValidationService cartItemModifierFieldValidationService;

	private Set<CartItemModifierField> getCartItemModifierFields(final Set<CartItemModifierGroup> cartItemModifierGroups) {
		return cartItemModifierGroups.stream().map(CartItemModifierGroup::getCartItemModifierFields)
				.flatMap(Set::stream)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * Calls validation service.
	 * @param itemsToValidate items to validate
	 * @param cartItemModifierGroups modifiers groups
	 * @return list of structured error messages
	 */
	protected List<StructuredErrorMessage> baseValidate(final Map<String, String> itemsToValidate,
			final Set<CartItemModifierGroup> cartItemModifierGroups) {
		return cartItemModifierFieldValidationService.validate(itemsToValidate,
				getCartItemModifierFields(cartItemModifierGroups));
	}

	protected CartItemModifierFieldValidationService getCartItemModifierFieldValidationService() {
		return cartItemModifierFieldValidationService;
	}

	public void setCartItemModifierFieldValidationService(final CartItemModifierFieldValidationService cartItemModifierFieldValidationService) {
		this.cartItemModifierFieldValidationService = cartItemModifierFieldValidationService;
	}
}
