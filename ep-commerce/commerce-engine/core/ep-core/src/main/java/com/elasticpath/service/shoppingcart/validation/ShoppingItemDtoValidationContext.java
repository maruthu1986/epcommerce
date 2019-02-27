/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Validation context for validating shopping item dto.
 */
public interface ShoppingItemDtoValidationContext extends ProductSkuValidationContext {

	/**
	 * Getter for the shopping item dto.
	 * @return the shopping item dto
	 */
	ShoppingItemDto getShoppingItemDto();

	/**
	 * Setter for the shopping item dto.
	 * @param shoppingItemDto the shopping item dto
	 */
	void setShoppingItemDto(ShoppingItemDto shoppingItemDto);

	/**
	 * Getter for parent shopping item that the shopping item dto is being added to.
	 * @return the shopping item
	 */
	Object getParentShoppingItem();

	/**
	 * Setter for parent shopping item that the shopping item dto is being added to.
	 * @param parentShoppingItem the parent shopping item
	 */
	void setParentShoppingItem(Object parentShoppingItem);

	/**
	 * Getter for update property.
	 *
	 * @return value of the update property
	 */
	boolean isUpdate();

	/**
	 * Setter for update property.
	 *
	 * @param update new value for the update property
	 */
	void setUpdate(boolean update);

	/**
	 * Getter for shopping cart.
	 * @return shopping cart.
	 */
	ShoppingCart getShoppingCart();

	/**
	 * Setter for shopping cart.
	 * @param shoppingCart the shopping cart.
	 */
	void setShoppingCart(ShoppingCart shoppingCart);
}