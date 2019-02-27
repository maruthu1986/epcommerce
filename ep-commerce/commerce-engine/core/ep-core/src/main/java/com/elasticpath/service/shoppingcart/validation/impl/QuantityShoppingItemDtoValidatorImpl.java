/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidator;

/**
 * Validator to check that at least minimum quantity of product is added.
 */
public class QuantityShoppingItemDtoValidatorImpl implements ShoppingItemDtoValidator {

	/**
	 * minimum quantity that should be present.
	 */
	private static final int MINIMUM_QUANTITY = 1;

	/**
	 * minimum quantity that should be present for an UPDATE.
	 */
	private static final int MINIMUM_QUANTITY_FOR_UPDATE = 0;

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "field.invalid.minimum.value";

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingItemDtoValidationContext context) {
		final int quantity = context.getShoppingItemDto().getQuantity();
		boolean thereIsError = false;
		int minQuantity = 0;
		if (context.isUpdate() && quantity < MINIMUM_QUANTITY_FOR_UPDATE) {
			minQuantity = MINIMUM_QUANTITY_FOR_UPDATE;
			thereIsError = true;
		} else if (!context.isUpdate() && quantity < MINIMUM_QUANTITY) {
			minQuantity = MINIMUM_QUANTITY;
			thereIsError = true;
		}
		if (thereIsError) {
			return Collections.singletonList(new StructuredErrorMessage(MESSAGE_ID,
					String.format("'quantity' value '%d' must be greater than or equal to '%d'.", quantity, minQuantity),
					ImmutableMap.of("item-code", context.getProductSku().getSkuCode(),
							"field-name", "quantity",
							"min-value", String.format("%d", minQuantity),
							"invalid-value", String.format("%d", quantity))));
		}
		return Collections.emptyList();
	}
}
