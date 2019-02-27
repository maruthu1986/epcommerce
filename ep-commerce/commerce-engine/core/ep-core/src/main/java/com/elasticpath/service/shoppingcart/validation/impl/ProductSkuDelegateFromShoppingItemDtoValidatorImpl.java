/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidator;

/**
 * Delegates product sku validator form item dto validators.
 */
public class ProductSkuDelegateFromShoppingItemDtoValidatorImpl
	extends AbstractAggregateValidator<ProductSkuValidationContext, ShoppingItemDtoValidationContext>
	implements ShoppingItemDtoValidator {
}
