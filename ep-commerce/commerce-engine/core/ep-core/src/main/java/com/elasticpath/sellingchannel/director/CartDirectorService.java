/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.sellingchannel.director;

import java.util.List;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

/**
 * Service which wraps CartDirector's shopping cart update functionality within Container managed
 * transactional boundaries.
 * <p>
 * Many methods in this service delegate to the CartDirector.  The purpose of the CartDirectorService is that it
 * manages transactions and persists the cart at the end of the call.  This allows outside callers like Controllers and
 * Cortex to safely access CartDirector functionality from within a container managed transaction.
 */
public interface CartDirectorService {

	/**
	 * Adds the given shopping item to the cart and persists the cart afterwards.  Returns the updated ShoppingCart.
	 *
	 * @param shoppingCart {@code ShoppingCart}
	 * @param dto          of new item
	 * @return the new {@link com.elasticpath.domain.shoppingcart.ShoppingItem} and the updated {@link ShoppingCart}
	 */
	ShoppingItem addItemToCart(ShoppingCart shoppingCart, ShoppingItemDto dto);

	/**
	 * Updates the given shopping item in the cart and persists the cart afterwards.  Returns the updated ShoppingCart.
	 *
	 * @param shoppingCart {@code ShoppingCart}
	 * @param itemId       id of cart item for update
	 * @param dto          of new item
	 * @return the ShoppingItem that's updated as a result of the given dto and the updated ShoppingCart.
	 */
	ShoppingCart updateCartItem(ShoppingCart shoppingCart, long itemId, ShoppingItemDto dto);

	/**
	 * Updates the given compound shopping item in the cart and persists the cart afterwards.  This method allows
	 * for cart items that may have constituents, like bundles and/or items with warranties.
	 * <p>
	 * Returns the updated ShoppingCart.
	 *
	 * @param shoppingCart       {@code ShoppingCart}
	 * @param rootItemId         id of the root item for update
	 * @param rootItemDto        root item data for update
	 * @param dependentItemDtos  dependent item data for update
	 * @param associatedItemDtos associated item data for update
	 * @return the ShoppingItem that's updated as a result of the given dto and the updated ShoppingCart.
	 */
	ShoppingCart updateCompoundCartItem(
			ShoppingCart shoppingCart, long rootItemId, ShoppingItemDto rootItemDto,
			List<ShoppingItemDto> dependentItemDtos, List<ShoppingItemDto> associatedItemDtos);

	/**
	 * Removes the given shopping items from the cart and persists the cart afterwards.  Returns the updated ShoppingCart.
	 *
	 * @param shoppingCart    {@code ShoppingCart}
	 * @param doomedItemGuids the guids of the cart item that should be removed
	 * @return the updated {@link ShoppingCart}
	 */
	ShoppingCart removeItemsFromCart(ShoppingCart shoppingCart, String... doomedItemGuids);

	/**
	 * Refreshes the given shopping cart. For example, updating all its {@link ShoppingItem} with the latest prices.
	 * After refreshing the cart, the cart is persisted.  Returns the refreshed, persisted ShoppingCart
	 *
	 * @param shoppingCart shopping cart
	 * @return the refreshed, updated {@link com.elasticpath.domain.shoppingcart.ShoppingCart}
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	ShoppingCart refresh(ShoppingCart shoppingCart);

	/**
	 * Re-applies the catalog promotions to the ShoppingCart so that the
	 * applied rules are tracked on the cart.
	 * If you are here then you probably want to re-apply the catalog promotions
	 * to the cart after you lost the information about which catalog rules
	 * were applied when you called fireRules().
	 *
	 * @param shoppingCart a cart
	 */
	void reApplyCatalogPromotions(ShoppingCart shoppingCart);

	/**
	 * Adds the ProductSku represented by the given SkuCode to the wish list as a wish list item and
	 * persists the wish list.
	 *
	 * @param skuCode the code representing the ProductSku to be added to the cart
	 * @param shopper the customer session
	 * @param store   the store
	 * @return the {@link ShoppingItem} that's added to the cart
	 */
	ShoppingItem addSkuToWishList(String skuCode, Shopper shopper, Store store);

	/**
	 * Move an item from the wish list to the shopping cart and persists both the wish list and shopping cart afterwards.
	 *
	 * @param shoppingCart         the shopping cart
	 * @param dto                  the shopping item dto
	 * @param wishlistLineItemGuid the favorites line item guid
	 * @return the shopping item
	 */
	ShoppingItem moveItemFromWishListToCart(ShoppingCart shoppingCart, ShoppingItemDto dto, String wishlistLineItemGuid);


	/**
	 * Move an item from the wish list to the shopping cart and persists both the wish list and shopping cart
	 * afterwards.
	 *
	 * @param shoppingCart     the shopping cart
	 * @param cartLineItemGuid the cart line item guid
	 * @return the result
	 */
	AddToWishlistResult moveItemFromCartToWishList(ShoppingCart shoppingCart, String cartLineItemGuid);

	/**
	 * Clears all items from the shopping cart and persists the cart afterwards.
	 *
	 * @param shoppingCart shopping cart
	 * @return the empty {@link ShoppingCart}
	 */
	ShoppingCart clearItems(ShoppingCart shoppingCart);

}
