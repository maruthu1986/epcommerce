package com.elasticpath.extensions.dao.favorites;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.extensions.domain.favorites.FavoriteList;

import java.util.List;

public interface FavoritesDao {
    /**
     * find favorite list by shopping context data.
     *
     * @param shopper the customer context data
     * @return the favorite list found
     */
    FavoriteList findByShopper(Shopper shopper);

    /**
     * Get favorite list by uidpk.
     *
     * @param uid the uidpk
     * @return the favorite list found
     */
    FavoriteList get(long uid);

    /**
     * Get favorite list by guid.
     *
     * @param guid the guid
     * @return the favorite list found
     */
    FavoriteList findByGuid(String guid);

    /**
     * save or update favorite list.
     *
     * @param favoriteList the favorite list to be saved or updated.
     * @return the saved/updated favorite list
     */
    FavoriteList saveOrUpdate(FavoriteList favoriteList);

    /**
     * Remove the favoriteList.
     *
     * @param favoriteList the favorite list
     */
    void remove(FavoriteList favoriteList);

    /**
     * Remove all empty FavoriteLists associated with any of the given Shopper.
     *
     * @param shopperUids the uids of the associated Shoppers
     * @return the number of deleted FavoriteLists.
     */
    int deleteEmptyFavoriteListsByShopperUids(List<Long> shopperUids);

    /**
     * Remove all FavoriteLists associated with the given list of Shoppers.
     *
     * @param shopperUids the uids of the associated Shoppers
     * @return the number of deleted FavoriteLists.
     */
    int deleteAllFavoriteListsByShopperUids(List<Long> shopperUids);
}
