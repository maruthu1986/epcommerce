/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.ReadFavoriteMembershipsIdentifier;
import com.elasticpath.rest.definition.favorites.ReadFavoriteMembershipsResource;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Observable;

import javax.inject.Inject;

/**
 * Favorites memberships for an item.
 */
public class ReadFavoritesMembershipsPrototype implements ReadFavoriteMembershipsResource.Read {
    private final ItemIdentifier itemIdentifier;

    private final LinksRepository<ItemIdentifier, FavoriteIdentifier> repository;

    /**
     * Constructor.
     *
     * @param favoriteMembershipsIdentifier favoriteMembershipsIdentifier
     */
    @Inject
    public ReadFavoritesMembershipsPrototype(@RequestIdentifier final ReadFavoriteMembershipsIdentifier favoriteMembershipsIdentifier
            , @ResourceRepository final LinksRepository<ItemIdentifier, FavoriteIdentifier> repository) {
        this.itemIdentifier = favoriteMembershipsIdentifier.getItem();
        this.repository = repository;
    }

    @Override
    public Observable<FavoriteIdentifier> onRead() {
        return repository.getElements(itemIdentifier);
    }
}
