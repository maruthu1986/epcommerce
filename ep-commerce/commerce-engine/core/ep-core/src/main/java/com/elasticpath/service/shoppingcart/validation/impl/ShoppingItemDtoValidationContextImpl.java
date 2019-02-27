/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;

/**
 * Implements {@link ShoppingItemDtoValidationContext}.
 */
public class ShoppingItemDtoValidationContextImpl extends ProductSkuValidationContextImpl
		implements ShoppingItemDtoValidationContext {

	private ShoppingItemDto shoppingItemDto;

	private Object parentShoppingItem;

	private boolean update;

	private ShoppingCart shoppingCart;

	@Override
	public ShoppingItemDto getShoppingItemDto() {
		return shoppingItemDto;
	}

	@Override
	public void setShoppingItemDto(final ShoppingItemDto shoppingItemDto) {
		this.shoppingItemDto = shoppingItemDto;
	}

	@Override
	public Object getParentShoppingItem() {
		return parentShoppingItem;
	}

	@Override
	public void setParentShoppingItem(final Object parentShoppingItem) {
		this.parentShoppingItem = parentShoppingItem;
	}

	@Override
	public boolean isUpdate() {
		return update;
	}

	@Override
	public void setUpdate(final boolean update) {
		this.update = update;
	}

	@Override
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	@Override
	public void setShoppingCart(final ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
}
