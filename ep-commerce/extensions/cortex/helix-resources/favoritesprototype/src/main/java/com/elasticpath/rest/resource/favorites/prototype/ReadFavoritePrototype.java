/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.favorites.FavoriteEntity;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoriteResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Single;

import javax.inject.Inject;

/**
 * Read favorites.
 */
public class ReadFavoritePrototype implements FavoriteResource.Read {
    private final FavoriteIdentifier favoriteIdentifier;

    private final Repository<FavoriteEntity, FavoriteIdentifier> repository;

    /**
     * Constructor.
     *
     * @param favoriteIdentifier favoriteIdentifier
     * @param repository         repository
     */
    @Inject
    public ReadFavoritePrototype(@RequestIdentifier final FavoriteIdentifier favoriteIdentifier,
                                 @ResourceRepository final Repository<FavoriteEntity, FavoriteIdentifier> repository) {
        this.favoriteIdentifier = favoriteIdentifier;
        this.repository = repository;
    }

    @Override
    public Single<FavoriteEntity> onRead() {
        return repository.findOne(favoriteIdentifier);
    }

}
