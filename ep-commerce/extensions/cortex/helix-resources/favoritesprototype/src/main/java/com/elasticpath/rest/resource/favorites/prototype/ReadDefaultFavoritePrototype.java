/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.favorites.DefaultFavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.DefaultFavoriteResource;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import io.reactivex.Single;

import javax.inject.Inject;

/**
 * Read prototype for the default favoritelist.
 */
public class ReadDefaultFavoritePrototype implements DefaultFavoriteResource.Read {
    private final DefaultFavoriteIdentifier defaultFavoriteIdentifier;

    private final AliasRepository<DefaultFavoriteIdentifier, FavoriteIdentifier> repository;

    /**
     * Constructor.
     *
     * @param defaultFavoriteIdentifier defaultFavoriteIdentifier
     * @param repository                the repository
     */
    @Inject
    public ReadDefaultFavoritePrototype(@RequestIdentifier final DefaultFavoriteIdentifier defaultFavoriteIdentifier,
                                        @ResourceRepository final AliasRepository<DefaultFavoriteIdentifier, FavoriteIdentifier> repository) {
        this.defaultFavoriteIdentifier = defaultFavoriteIdentifier;
        this.repository = repository;
    }

    @Override
    public Single<FavoriteIdentifier> onRead() {
        return repository.resolve(defaultFavoriteIdentifier);
    }
}
