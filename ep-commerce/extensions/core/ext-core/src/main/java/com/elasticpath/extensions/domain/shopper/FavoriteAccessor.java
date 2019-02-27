/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.extensions.domain.shopper;

import com.elasticpath.extensions.domain.favorites.FavoriteList;

/**
 * Gives access to a {@link FavoriteList} on the implementing object.
 */
public interface FavoriteAccessor {

    /**
     * Gets the current {@link FavoriteList}.
     *
     * @return a {@link FavoriteList}.
     */
    FavoriteList getCurrentFavoriteList();

    /**
     * Sets the current {@link FavoriteList}.
     *
     * @param favoriteList the {@link FavoriteList}.
     */
    void setCurrentFavoriteList(FavoriteList favoriteList);

}