/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.schema.uri;

import com.elasticpath.rest.schema.uri.ScopedUriBuilder;

/**
 * Builds a URI to Favorites.
 */
public interface FavoritesUriBuilder extends ScopedUriBuilder<FavoritesUriBuilder> {
    /**
     * Set the favorites ID.
     *
     * @param favoriteId item id
     * @return the builder
     */
    FavoritesUriBuilder setFavoriteId(String favoriteId);

    /**
     * Set the form uri.
     *
     * @param formUri form uri.
     * @return the builder
     */
    FavoritesUriBuilder setFormUri(String formUri);

    /**
     * Set the item uri.
     *
     * @param itemUri item uri
     * @return the builder
     */
    FavoritesUriBuilder setItemUri(String itemUri);
}
