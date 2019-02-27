/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites;

import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteLineItemsIdentifier;
import com.elasticpath.rest.definition.favorites.FavoritesIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import org.apache.log4j.Logger;

/**
 * Utility class for building identifiers.
 */
public final class FavoriteIdentifierUtil {
    private static final Logger LOG = Logger.getLogger(FavoriteIdentifierUtil.class);

    private FavoriteIdentifierUtil() {
    }

    /**
     * Builds a FavoritesIdentifier.
     *
     * @param scope scope
     * @return FavoritesIdentifier
     */
    public static FavoritesIdentifier buildFavoritesIdentifier(final String scope) {
        LOG.info("FavoriteIdentifierUtil.buildFavoritesIdentifier(), scope :: "+scope);
        return FavoritesIdentifier.builder()
                .withScope(StringIdentifier.of(scope))
                .build();
    }

    /**
     * Builds a FavoriteIdentifier.
     *
     * @param scope      scope
     * @param favoriteId Favorite id
     * @return FavoriteIdentifier
     */
    public static FavoriteIdentifier buildFavoriteIdentifier(final String scope, final String favoriteId) {
        LOG.info("FavoriteIdentifierUtil.buildFavoriteIdentifier(), favoriteId :: "+favoriteId);
        return FavoriteIdentifier.builder()
                .withFavorites(buildFavoritesIdentifier(scope))
                .withFavoriteId(StringIdentifier.of(favoriteId))
                .build();
    }

    /**
     * Builds a FavoriteLineItemsIdentifier.
     *
     * @param scope      scope
     * @param favoriteId favoriteId
     * @return FavoritelistLineItemsIdentifier
     */
    public static FavoriteLineItemsIdentifier buildFavoriteLineItemsIdentifier(final String scope, final String favoriteId) {
        LOG.info("FavoriteIdentifierUtil.buildFavoriteLineItemsIdentifier()"+favoriteId);
        return FavoriteLineItemsIdentifier.builder()
                .withFavorite(buildFavoriteIdentifier(scope, favoriteId))
                .build();
    }

    /**
     * Builds a favoriteLineItemIdentifier.
     *
     * @param scope      scope
     * @param favoriteId favoriteId
     * @param itemId     item id
     * @return FavoriteLineItemIdentifier
     */
    public static FavoriteLineItemIdentifier buildFavoriteLineItemIdentifier(final String scope, final String favoriteId, final String itemId) {
        LOG.info("FavoriteIdentifierUtil.buildFavoriteLineItemIdentifier() :: "+favoriteId+" :: "+itemId);
        return FavoriteLineItemIdentifier.builder()
                .withFavoriteLineItems(buildFavoriteLineItemsIdentifier(scope, favoriteId))
                .withLineItemId(StringIdentifier.of(itemId))
                .build();
    }
}
