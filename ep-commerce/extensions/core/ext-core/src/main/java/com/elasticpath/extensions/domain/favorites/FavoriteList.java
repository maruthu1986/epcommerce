package com.elasticpath.extensions.domain.favorites;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingList;

public interface FavoriteList extends ShoppingList {
    /**
     * Add item into favorites.
     *
     * @param item the item to be added
     * @return the shopping item added
     */
    ShoppingItem addItem(ShoppingItem item);

    /**
     * Remove item from favorites by sku <em>guid</em>.
     *
     * @param skuGuid the guid (not code) of the sku of the item to be removed
     */
    void removeItemBySkuGuid(String skuGuid);

    /**
     * Remove item from favorites by favorite item uidpk.
     *
     * @param favoriteItemGuid the guid of the favorite item
     */
    void removeItem(String favoriteItemGuid);

    /**
     * Gets the code for this domain model object.
     *
     * @return the unique identifier.
     */
    String getStoreCode();

    /**
     * Sets the code for this domain model object.
     *
     * @param storeCode the new storeCode.
     */
    void setStoreCode(String storeCode);
}
