/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Service for validating adding a shopping item dto to cart.
 */
public interface AddOrUpdateShoppingItemDtoToCartValidationService extends Validator<ShoppingItemDtoValidationContext> {

	/**
	 * Builds validation context.
	 * @param shoppingCart the shopping cart
	 * @param shoppingItemDto the shopping item dto
	 * @param parentShoppingItem the parent shopping item or shopping item dto that the shopping item dto is being added to
	 * @param update flag showing whether
	 * @return the shopping item dto validation context object
	 */
	ShoppingItemDtoValidationContext buildContext(ShoppingCart shoppingCart, ShoppingItemDto shoppingItemDto,
												  Object parentShoppingItem, boolean update);
}




