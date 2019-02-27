/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.sellingchannel.director;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Maps data between a {@code ShoppingItemDto} and a {@code ShoppingItem} domain object.
 */
public interface ShoppingItemAssembler {

	/**
	 * Creates a {@code ShoppingItem} from a {@code ShoppingItemDto} for both normal and bundled products.
	 *
	 * @param shoppingItemDto The dto to use.
	 * @return The ShoppingItem.
	 */
	ShoppingItem createShoppingItem(ShoppingItemDto shoppingItemDto);

	/**
	 * Creates a {@code ShoppingItemDto} from a {@code ShoppingItem} for both normal and bundled products.
	 *
	 * @param shoppingItem The item to use.
	 *
	 * @return The ShoppingItemDto.
	 */
	ShoppingItemDto assembleShoppingItemDtoFrom(ShoppingItem shoppingItem);

	/**
	 * Creates a {@code ShoppingItemDto} from a {@code ShoppingItem} for both normal and bundled products.
	 *
	 * @param shoppingItem The item to use.
	 * @param shoppingItemSku the preloaded item sku for the shoppingItem
	 *
	 * @return The ShoppingItemDto.
	 */
	ShoppingItemDto assembleShoppingItemDtoFrom(ShoppingItem shoppingItem, ProductSku shoppingItemSku);
}
