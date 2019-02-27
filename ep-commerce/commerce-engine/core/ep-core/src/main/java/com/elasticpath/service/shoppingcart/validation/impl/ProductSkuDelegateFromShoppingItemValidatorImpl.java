/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidator;

/**
 * Delegates product sku validators from shopping item validators.
 */
public class ProductSkuDelegateFromShoppingItemValidatorImpl
		extends AbstractAggregateValidator<ProductSkuValidationContext, ShoppingItemValidationContext>
		implements ShoppingItemValidator {
	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingItemValidationContext context) {
		return super.validate(context).stream()
				.map(structuredErrorMessage -> new StructuredErrorMessage(structuredErrorMessage.getType(), structuredErrorMessage.getMessageId(),
						structuredErrorMessage.getDebugMessage(), structuredErrorMessage.getData(),
						new StructuredErrorResolution(ShoppingItem.class, context.getShoppingItem().getGuid())))
				.collect(Collectors.toList());
	}
}
