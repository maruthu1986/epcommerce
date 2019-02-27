package com.elasticpath.extensions.service.favorites;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.store.Store;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

import java.util.List;
import java.util.Locale;

public interface FavoriteListService {
    /**
     * Get the favorite list by uidPk.
     *
     * @param uid the uidPk
     * @return the favorite List found
     */
    FavoriteList get(long uid);


    /**
     * Add all items in the favorite list.
     *
     * @param favoriteList the favoriteList
     * @param items        the collection of items to add
     */
    void addAllItems(FavoriteList favoriteList, List<ShoppingItem> items);

    /**
     * <p>Add item into favorite list. If an identical item exists on the favoriteList then the existing item is returned.</p>
     * <p>
     * <p>The boolean value is <code>true</code> if a new item is created, <code>false</code> otherwise.</p>
     *
     * @param favoriteList the favoriteList
     * @param item         the item to be added
     * @return the shopping item added, or the existing identical item
     */
    AddToWishlistResult addItem(FavoriteList favoriteList, ShoppingItem item);

    /**
     * Save the Favorite List.
     *
     * @param favoriteList the favorite list to be saved
     * @return the saved favorite list
     */
    FavoriteList save(FavoriteList favoriteList);

    /**
     * Create the favorite list by the {@link Shopper}.
     *
     * @param shopper the Shopper
     * @return the favorite list created
     */
    FavoriteList createFavoriteList(Shopper shopper);

    /**
     * Find the favorite list by shopper.
     *
     * @param shopper the shopper
     * @return the Favorite List found
     */
    FavoriteList findOrCreateFavoriteListByShopper(Shopper shopper);

    /**
     * Find the favoriteList with prices.
     *
     * @param customerSession the customer session
     * @return the favoriteList found
     */
    FavoriteList findOrCreateFavoriteListWithPrice(CustomerSession customerSession);

    /**
     * Find the favoriteList from its guid.
     *
     * @param guid the guid
     * @return the Favorite List
     */
    FavoriteList findByGuid(String guid);

    /**
     * Add a product to the favoriteList.
     *
     * @param favoriteList the favoriteList
     * @param store        the store
     * @param productSku   sku
     * @return the result of add operation
     */
    AddToWishlistResult addProductSku(FavoriteList favoriteList, Store store, String productSku);

    /**
     * Remove one Favorite list.
     *
     * @param favoriteList the favorite list to be removed
     */
    void remove(FavoriteList favoriteList);

    /**
     * Delete all Empty FavoriteLists that are associated with the list of Shoppers.
     *
     * @param shopperUids the uids of the associated Shoppers.
     * @return the number of deleted FavoriteLists
     */
    int deleteEmptyFavoriteListsByShopperUids(List<Long> shopperUids);

    /**
     * Delete all FavoriteLists that are associated with the list of Shoppers.
     * Even the ones that are not empty.
     *
     * @param shopperUids the uids of the associated Shoppers.
     * @return the number of deleted FavoriteLists
     */
    int deleteAllFavoriteListsByShopperUids(List<Long> shopperUids);

    /**
     * Shares a Favorite Lists with one or many recipients.
     *
     * @param favoritesMessage the favoritesMessage to send, includes the list of recipients
     * @param favoriteList    the favorite list
     * @param storeCode       the code of the Store with which the Favorite List is associated
     * @param locale          the locale
     */
    void shareFavoriteList(WishListMessage favoritesMessage, FavoriteList favoriteList, String storeCode, Locale locale);
}
